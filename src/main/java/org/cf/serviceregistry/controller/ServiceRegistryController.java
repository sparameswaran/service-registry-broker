package org.cf.serviceregistry.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cf.servicebroker.repository.CredentialsRepository;
import org.cf.servicebroker.repository.PlanRepository;
import org.cf.servicebroker.repository.ServiceRepository;
import org.cf.serviceregistry.servicebroker.model.Credentials;
import org.cf.serviceregistry.servicebroker.model.Plan;
import org.cf.serviceregistry.servicebroker.model.PlanPk;
import org.cf.serviceregistry.servicebroker.model.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ServiceRegistryController {

	Log log = LogFactory.getLog(ServiceRegistryController.class);

	@Autowired
	ServiceRepository serviceRepo;
	
	@Autowired
	PlanRepository planRepo;
	
	@Autowired
	CredentialsRepository credentialsRepo;

	ResponseEntity<String> createCredentials(Credentials newCreds) {
		
		credentialsRepo.save(newCreds);
		log.debug("Credentials created: " + newCreds);
		return new ResponseEntity<>("{}", HttpStatus.OK);
	}
	
		
	ResponseEntity<String> createPlan(Plan newPlan) {
		
		boolean planExists = planRepo.exists(newPlan.getId());
		if (planExists) {
			// We want a new plan specifically for a given service, no overlaps allowed.
			return new ResponseEntity<>(
					"{\"description\": \"Plan with id: "
						+ newPlan.getId()					
						+ " already exists\"}",
						HttpStatus.CONFLICT);		
		}
		
		Credentials newCreds = newPlan.getCredentials();
		if (newCreds != null) {
			ResponseEntity credsCreateStatus = createCredentials(newCreds);
			if (credsCreateStatus.getStatusCode() != HttpStatus.OK) {
				return credsCreateStatus;
			}
		}		
		
		planRepo.save(newPlan);
		log.debug("Service Plan created: " + newPlan);
		return new ResponseEntity<>("{}", HttpStatus.OK);
	}

	public ResponseEntity<String> createService(Service serviceEndpointInstance) {
		
		String serviceId = serviceEndpointInstance.getName();
		
		log.info("Got /services call with payload : "
				+ serviceEndpointInstance);

		boolean exists = serviceRepo.exists(serviceId);
		if (exists) {
			Service existing = serviceRepo.findOne(serviceId);
			return new ResponseEntity<>(
					"{\"description\": \"Service with id: "
						+ serviceId					
						+ " already exists\"}",
						HttpStatus.CONFLICT);		
		} else {
			
			Set<Plan> plans = serviceEndpointInstance.getPlans();
			
			if (plans != null) {
				
				for (Plan newPlan: plans) {
					PlanPk newPlanPk = newPlan.getId();
					log.info("New Plan PK: " + newPlanPk);
					
					newPlanPk.setServiceId(serviceId);
					
					log.info("After setting service Id : New Plan PK: " + newPlanPk);
					
				}
			}

			serviceRepo.save(serviceEndpointInstance);			
		}

		log.debug("Service Instance created: " + serviceEndpointInstance);
		
		return new ResponseEntity<>("{}", HttpStatus.OK);
	}
	
	@RequestMapping("/services")
	public Map<String, Iterable<Service>> services() {
		Map<String, Iterable<Service>> wrapper = new HashMap<>();
		wrapper.put("services", serviceRepo.findAll());
		return wrapper;
	}

	// Bulk creation of service defns
	@RequestMapping(value = "/services", method = RequestMethod.POST)
	public ResponseEntity<String> createServices(
			@RequestBody Service[] serviceEndpointInstances) {
		
		for(Service serviceInstance: serviceEndpointInstances) {
			ResponseEntity<String> response = createService(serviceInstance);
			log.info("Response Entity body for create service: " + response);
			
			if (response.getStatusCode() != HttpStatus.OK) {
				return new ResponseEntity<>("{\"description\": " +
						"\"Problem creating service instances, nested error: " + 
						response.getBody() + " \"}",
				HttpStatus.BAD_REQUEST);
			}
		}
		return new ResponseEntity<>("{}", HttpStatus.OK);

	}
	
	@RequestMapping(value = "/services/{serviceId}", method = RequestMethod.GET)
	public ResponseEntity<Object> find(@PathVariable("serviceId") String serviceId) {

		boolean exists = serviceRepo.exists(serviceId);
		if (exists) {
			Service existing = serviceRepo.findOne(serviceId);
			return new ResponseEntity<Object>(existing, HttpStatus.OK);			
		} else {
			
			return new ResponseEntity<Object>(
					"{\"description\": \"Service with name: "
							+ serviceId + " not found\"}",
					HttpStatus.BAD_REQUEST);
		}
	}

	@RequestMapping(value = "/services/{serviceId}", 
					method = RequestMethod.DELETE)
	public ResponseEntity<String> deleteServiceDefn(
			@PathVariable("serviceId") String serviceId) {
		boolean exists = serviceRepo.exists(serviceId);

		if (exists) {
			Service service = serviceRepo.findOne(serviceId);
		
			// Clean up associated plans
			planRepo.delete(service.getPlans());
			serviceRepo.delete(serviceId);
			return new ResponseEntity<>("{}", HttpStatus.OK);
		} else {
			return new ResponseEntity<>("{}", HttpStatus.GONE);
		}
	}
	
	@RequestMapping(value = "/services/{serviceId}/plans", 
			method = RequestMethod.GET)
	public ResponseEntity<Object> getServicePlanDefns(
		@PathVariable("serviceId") String serviceId) {
		boolean exists = serviceRepo.exists(serviceId);
		
		if (exists) {
			Service serviceDefn = serviceRepo.findOne(serviceId);
			Set<Plan> servicePlanInstances = serviceDefn.getPlans();
			
			Map<String, Iterable<Plan>> wrapper = new HashMap<>();
			wrapper.put("plans", servicePlanInstances);
			return new ResponseEntity<>(wrapper, HttpStatus.OK);			
			
		} else {
			
			return new ResponseEntity<>("{\"description\": \"Service with id "
					+ serviceId + " not found\"}",
			HttpStatus.BAD_REQUEST);
		}
	}

	
	@RequestMapping(value = "/services/{serviceId}/plans/{planId}", 
					method = RequestMethod.GET)
	public ResponseEntity<Object> getServicePlanDefn(
			@PathVariable("serviceId") String serviceId,
			@PathVariable("planId") String planId) {
		boolean exists = serviceRepo.exists(serviceId);

		if (exists) {
			
			PlanPk planPk = new PlanPk();
			planPk.setServiceId(serviceId);
			planPk.setPlanId(planId);
			
			Service serviceDefn = serviceRepo.findOne(serviceId);
			Plan servicePlanInstance = planRepo.findOne(planPk);
			if (serviceDefn.getPlans().contains(servicePlanInstance) 
					&& (servicePlanInstance != null)) {
				return new ResponseEntity<Object>(servicePlanInstance, HttpStatus.OK);
			}
			
			return new ResponseEntity<>("{\"description\": \"Plan with id "
						+ planId + " not found\"}",
				HttpStatus.BAD_REQUEST);
			
		} else {
			return new ResponseEntity<>("{\"description\": \"Service with id "
					+ serviceId + " not found\"}",
			HttpStatus.BAD_REQUEST);
		}
	}

	@RequestMapping(value = "/services/{serviceId}/plans", 
					method = RequestMethod.PUT)
	public ResponseEntity<String> createServicePlanDefn(
			@PathVariable("serviceId") String serviceId,
			@RequestBody Plan servicePlanInstance) {
		boolean exists = serviceRepo.exists(serviceId);

		System.out.println("Map contents");
		
		if (exists) {
			
			PlanPk planPk = new PlanPk();
			planPk.setServiceId(serviceId);
			planPk.setPlanId(servicePlanInstance.getName());

			servicePlanInstance.setId(planPk);
			Service serviceDefn = serviceRepo.findOne(serviceId);
			//Plan existingPlan = planRepo.findOne(planPk);
			
			serviceDefn.addPlan(servicePlanInstance);

			// Override what was earlier there
			planRepo.save(servicePlanInstance);
			serviceRepo.save(serviceDefn);
			
			return new ResponseEntity<>("{}", HttpStatus.OK);
		} else {
			return new ResponseEntity<>("{\"description\": \"Service with id "
					+ serviceId + " not found\"}",
			HttpStatus.BAD_REQUEST);
		}
	}

	
	@RequestMapping(value = "/services/{serviceId}/plans/{planId}", 
					method = RequestMethod.DELETE)
	public ResponseEntity<String> deleteServicePlanDefn(
			@PathVariable("serviceId") String serviceId,
			@PathVariable("planId") String planId) {
		boolean exists = serviceRepo.exists(serviceId);

		if (exists) {
			
			PlanPk planPk = new PlanPk();
			planPk.setPlanId(planId);
			planPk.setServiceId(serviceId);
			
			Plan unwantedPlan = new Plan();
			unwantedPlan.setId(planPk);
			Service serviceDefn = serviceRepo.findOne(serviceId);
			unwantedPlan = planRepo.findOne(planPk);
			
			serviceDefn.getPlans().remove(unwantedPlan);
			
			planRepo.delete(planPk);
			serviceRepo.save(serviceDefn);
			
			return new ResponseEntity<>("{}", HttpStatus.OK);
		} else {
			return new ResponseEntity<>("{}", HttpStatus.GONE);
		}
	}

	@RequestMapping(value = "/services/{serviceId}/plans/{planId}/creds", 
			method = RequestMethod.GET)
	public ResponseEntity<Object> getServicePlanCredsDefn(
		@PathVariable("serviceId") String serviceId,
		@PathVariable("planId") String planId) {
		boolean exists = serviceRepo.exists(serviceId);
		
		if (exists) {
			PlanPk planPk = new PlanPk();
			planPk.setPlanId(planId);
			planPk.setServiceId(serviceId);
			
			Service serviceDefn = serviceRepo.findOne(serviceId);			
			Plan existingPlan = planRepo.findOne(planPk);
			if (existingPlan == null ) {
				return new ResponseEntity<>("{\"description\": \"Plan with id: "
					+ planPk + " not found or not associated with service defn with id " 
					+ serviceId + "\"}",
					HttpStatus.BAD_REQUEST);
			}
			
			Credentials credsInstance = existingPlan.getCredentials();
			if (credsInstance != null) 
				return new ResponseEntity<>(credsInstance, HttpStatus.OK);
		
				
			return new ResponseEntity<>("{\"description\": \"Found no credentials!! \"}",
					HttpStatus.BAD_REQUEST);
			
		} else {
			return new ResponseEntity<>("{\"description\": \"Service with id "
					+ serviceId + " not found\"}",
			HttpStatus.BAD_REQUEST);
		}
	}

	@RequestMapping(value = "/services/{serviceId}/plans/{planId}/creds", 
			method = RequestMethod.PUT)
	public ResponseEntity<String> createServicePlanCredsDefn(
		@PathVariable("serviceId") String serviceId,
		@PathVariable("planId") String planId,
		@RequestBody Credentials servicePlanCredsInstance) {
		boolean exists = serviceRepo.exists(serviceId);
		
		if (exists) {
			PlanPk planPk = new PlanPk();
			planPk.setPlanId(planId);
			planPk.setServiceId(serviceId);
			
			Service serviceDefn = serviceRepo.findOne(serviceId);			
			Plan existingPlan = planRepo.findOne(planPk);
			if (existingPlan == null ) {
				return new ResponseEntity<>("{\"description\": \"Plan with id: "
					+ planPk + " not found or not associated with service defn with id " 
					+ serviceId + "\"}",
					HttpStatus.BAD_REQUEST);
			}
			
			ResponseEntity<String> responseStatus = createCredentials(servicePlanCredsInstance);
		
			if (responseStatus.getStatusCode() != HttpStatus.OK) {
				return new ResponseEntity<>("{\"description\": \"Problem persisting credentials!! \"}",
				HttpStatus.INTERNAL_SERVER_ERROR);
			}
			existingPlan.setCredentials(servicePlanCredsInstance);
			planRepo.save(existingPlan);
			
			return new ResponseEntity<>("{}", HttpStatus.OK);
		} else {
			return new ResponseEntity<>("{\"description\": \"Service with id "
					+ serviceId + " not found\"}",
			HttpStatus.BAD_REQUEST);
		}
	}
	
	@RequestMapping(value = "/services/{serviceId}/plans/{planId}/creds", 
					method = RequestMethod.DELETE)
	public ResponseEntity<String> deleteServicePlanCredsDefn(
			@PathVariable("serviceId") String serviceId,
			@PathVariable("planId") String planId) {
		boolean exists = serviceRepo.exists(serviceId);

		if (exists) {
			
			PlanPk planPk = new PlanPk();
			planPk.setPlanId(planId);
			planPk.setServiceId(serviceId);
			
			Service serviceDefn = serviceRepo.findOne(serviceId);			
			Plan existingPlan = planRepo.findOne(planPk);
			
			if (existingPlan == null ) {
				return new ResponseEntity<>("{\"description\": \"Plan with id "
					+ planPk + " not found or not associated with service defn with id " 
					+ serviceId + "\"}",
					HttpStatus.BAD_REQUEST);
			}

			Credentials existingCreds = existingPlan.getCredentials();
			if (existingCreds == null) {
				return new ResponseEntity<>("{}", HttpStatus.GONE);
			}
			
			existingPlan.setCredentials(null);
			
			// Not sure if we should delete the credentials as 
			// it might be still used by the ServiceBindingInstance
			//credentialsRepo.delete(existingCreds);
			
			planRepo.save(existingPlan);
			
			return new ResponseEntity<>("{}", HttpStatus.OK);
		} else {
			return new ResponseEntity<>("{\"description\": \"Service with id "
					+ serviceId + " not found\"}",
			HttpStatus.BAD_REQUEST);
		}
	}
}
