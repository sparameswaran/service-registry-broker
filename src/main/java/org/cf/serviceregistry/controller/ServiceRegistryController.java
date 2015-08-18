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
import org.cf.serviceregistry.servicebroker.model.Service;
import org.springframework.beans.factory.annotation.Autowired;
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

	@RequestMapping("/serviceDefns")
	public Map<String, Iterable<Service>> services() {
		Map<String, Iterable<Service>> wrapper = new HashMap<>();
		wrapper.put("services", serviceRepo.findAll());
		return wrapper;
	}

	@RequestMapping(value = "/serviceDefns/{id}", method = RequestMethod.GET)
	public ResponseEntity<Object> find(@PathVariable("id") String id) {

		boolean exists = serviceRepo.exists(id);
		if (exists) {
			Service existing = serviceRepo.findOne(id);
			return new ResponseEntity<Object>(existing, HttpStatus.OK);			
		} else {
			
			return new ResponseEntity<Object>(
					"{\"description\": \"Service with id "
							+ id + " not found\"}",
					HttpStatus.BAD_REQUEST);
		}
	}

	ResponseEntity<String> createCredentials(Credentials newCreds) {
		String id = newCreds.getId();
		if (id == null) {
			id = UUID.randomUUID().toString();
			newCreds.setId(id);
		}
		
		boolean credExists = credentialsRepo.exists(newCreds.getId());
		
		if (credExists) {
			// We want a new plan specifically for a given service, no overlaps allowed.
			return new ResponseEntity<>(
					"{\"description\": \"Credentials with id "
						+ newCreds.getId() + " already exists\"}",
						HttpStatus.CONFLICT);					
		} 
		credentialsRepo.save(newCreds);
		log.debug("Credentials created: " + newCreds);
		return new ResponseEntity<>("{}", HttpStatus.OK);
	}
	
		
	ResponseEntity<String> createPlan(Plan newPlan) {

		String id = newPlan.getId();
		if (id == null) {
			id = UUID.randomUUID().toString();
			newPlan.setId(id);
		}

		boolean planExists = planRepo.exists(newPlan.getId());
		if (planExists) {
			// We want a new plan specifically for a given service, no overlaps allowed.
			return new ResponseEntity<>(
					"{\"description\": \"Plan with id "
						+ newPlan.getId() + " already exists\"}",
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

	ResponseEntity<String> createServices(Service[] serviceEndpointInstances) {
		for(Service serviceInstance: serviceEndpointInstances) {
			ResponseEntity<String> response = createService(serviceInstance);
			if (response.getStatusCode() != HttpStatus.OK) {
				return new ResponseEntity<>("{\"description\": " +
						"\"Problem creating service instances, nested error: " + 
						response.getBody() + " \"}",
				HttpStatus.BAD_REQUEST);
			}
		}
		return new ResponseEntity<>("{}", HttpStatus.OK);
	}
	
	ResponseEntity<String> createService(Service serviceEndpointInstance) {
		
		String id = serviceEndpointInstance.getId();
		if (id == null) {
			id = UUID.randomUUID().toString();
			serviceEndpointInstance.setId(id);
		}
		
		log.info("Got /serviceDefns call with payload : "
				+ serviceEndpointInstance);

		boolean exists = serviceRepo.exists(id);
		if (exists) {
			Service existing = serviceRepo.findOne(id);
			if (existing.equals(serviceEndpointInstance)) {
				return new ResponseEntity<>("{}", HttpStatus.OK);
			} else {
				return new ResponseEntity<>("{}", HttpStatus.CONFLICT);
			}
		} else {
			
			Set<Plan> plans = serviceEndpointInstance.getPlans();
			if (plans != null) {
				for (Plan newPlan: plans) {
					ResponseEntity planCreateStatus = createPlan(newPlan);
					if (planCreateStatus.getStatusCode() != HttpStatus.OK) {
						return planCreateStatus;
					}
				}
			}
		}
		
		log.debug("Service Instance created: " + serviceEndpointInstance);
		serviceRepo.save(serviceEndpointInstance);
		return new ResponseEntity<>("{}", HttpStatus.OK);
	}

	// Bulk creation of service defns
	@RequestMapping(value = "/serviceDefns", method = RequestMethod.POST)
	public ResponseEntity<String> create(
			@RequestBody Service[] serviceEndpointInstances) {
		return createServices(serviceEndpointInstances);
	}

	@RequestMapping(value = "/serviceDefns/{id}", method = RequestMethod.PUT)
	public ResponseEntity<String> create(@PathVariable("id") String serviceId,
			@RequestBody Service serviceEndpointInstance) {	
		
		if (serviceId == null) {				
			serviceId = UUID.randomUUID().toString();
		}
		serviceEndpointInstance.setId(serviceId);
		return createService(serviceEndpointInstance);
	}
	
	@RequestMapping(value = "/serviceDefns/{service_id}", 
					method = RequestMethod.DELETE)
	public ResponseEntity<String> deleteServiceDefn(
			@PathVariable("service_id") String serviceId) {
		boolean exists = serviceRepo.exists(serviceId);

		if (exists) {
			serviceRepo.delete(serviceId);
			return new ResponseEntity<>("{}", HttpStatus.OK);
		} else {
			return new ResponseEntity<>("{}", HttpStatus.GONE);
		}
	}
	
	@RequestMapping(value = "/serviceDefns/{service_id}/{plan_id}", 
					method = RequestMethod.GET)
	public ResponseEntity<Object> getServicePlanDefn(
			@PathVariable("service_id") String serviceId,
			@PathVariable("plan_id") String planId) {
		boolean exists = serviceRepo.exists(serviceId);

		if (exists) {
			Service serviceDefn = serviceRepo.findOne(serviceId);
			Plan servicePlanInstance = planRepo.findOne(planId);
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


	@RequestMapping(value = "/serviceDefns/{service_id}/{plan_id}", 
					method = RequestMethod.PUT)
	public ResponseEntity<String> createServicePlanDefn(
			@PathVariable("service_id") String serviceId,
			@PathVariable("plan_id") String planId,
			@RequestBody Plan servicePlanInstance) {
		boolean exists = serviceRepo.exists(serviceId);

		if (exists) {
			
			if (planId == null) {				
				planId = UUID.randomUUID().toString();
			}

			servicePlanInstance.setId(planId);
			Service serviceDefn = serviceRepo.findOne(serviceId);
			Plan existingPlan = planRepo.findOne(servicePlanInstance.getId());
			if (serviceDefn.getPlans().contains(servicePlanInstance) 
					&& existingPlan.equals(servicePlanInstance)) {
				return new ResponseEntity<>("{}", HttpStatus.OK);
			}
			
			ResponseEntity<String> responseStatus = createPlan(servicePlanInstance);

			if (responseStatus.getStatusCode() != HttpStatus.OK) {
				return new ResponseEntity<>("{\"description\": \"Plan with id "
						+ servicePlanInstance.getId() + " already exists\"}",
				HttpStatus.CONFLICT);
			}
			serviceDefn.addPlan(servicePlanInstance);
			serviceRepo.save(serviceDefn);
			
			return new ResponseEntity<>("{}", HttpStatus.OK);
		} else {
			return new ResponseEntity<>("{\"description\": \"Service with id "
					+ serviceId + " not found\"}",
			HttpStatus.BAD_REQUEST);
		}
	}

	
	@RequestMapping(value = "/serviceDefns/{service_id}/{plan_id}", 
					method = RequestMethod.DELETE)
	public ResponseEntity<String> deleteServicePlanDefn(
			@PathVariable("service_id") String serviceId,
			@PathVariable("plan_id") String planId) {
		boolean exists = serviceRepo.exists(serviceId);

		if (exists) {
			Plan unwantedPlan = new Plan();
			unwantedPlan.setId(planId);
			Service serviceDefn = serviceRepo.findOne(serviceId);
			serviceDefn.getPlans().remove(unwantedPlan);

			serviceRepo.save(serviceDefn);
			return new ResponseEntity<>("{}", HttpStatus.OK);
		} else {
			return new ResponseEntity<>("{}", HttpStatus.GONE);
		}
	}

	@RequestMapping(value = "/serviceDefns/{service_id}/{plan_id}/{creds_id}", 
					method = RequestMethod.PUT)
	public ResponseEntity<String> createServicePlanCredsDefn(
			@PathVariable("service_id") String serviceId,
			@PathVariable("plan_id") String planId,
			@PathVariable("creds_id") String credsId,
			@RequestBody Credentials servicePlanCredsInstance) {
		boolean exists = serviceRepo.exists(serviceId);

		if (exists) {
			if (credsId == null) {			
				credsId = UUID.randomUUID().toString();
			}
			
			servicePlanCredsInstance.setId(credsId);
			Service serviceDefn = serviceRepo.findOne(serviceId);			
			Plan existingPlan = planRepo.findOne(planId);
			if (existingPlan == null || !serviceDefn.getPlans().contains(existingPlan) ) {
				return new ResponseEntity<>("{\"description\": \"Plan with id "
					+ planId + " not found or not associated with service defn with id " 
					+ serviceId + "\"}",
					HttpStatus.BAD_REQUEST);
			}
			
			Credentials existingCreds = credentialsRepo.findOne(credsId);
			
			if (serviceDefn.getPlans().contains(existingPlan)
					&& servicePlanCredsInstance.equals(existingPlan.getCredentials())) {
				return new ResponseEntity<>("{}", HttpStatus.OK);
			}
			
			ResponseEntity<String> responseStatus = createCredentials(servicePlanCredsInstance);

			if (responseStatus.getStatusCode() != HttpStatus.OK) {
				return new ResponseEntity<>("{\"description\": \"Credentials with id "
						+ servicePlanCredsInstance.getId() + " already exists\"}",
				HttpStatus.CONFLICT);
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

	
	@RequestMapping(value = "/serviceDefns/{service_id}/{plan_id}/{creds_id}", 
					method = RequestMethod.DELETE)
	public ResponseEntity<String> deleteServicePlanCredsDefn(
			@PathVariable("service_id") String serviceId,
			@PathVariable("plan_id") String planId,
			@PathVariable("creds_id") String credsId) {
		boolean exists = serviceRepo.exists(serviceId);

		if (exists) {
			Service serviceDefn = serviceRepo.findOne(serviceId);			
			Plan existingPlan = planRepo.findOne(planId);
			
			if (existingPlan == null || !serviceDefn.getPlans().contains(existingPlan) ) {
				return new ResponseEntity<>("{\"description\": \"Plan with id "
					+ planId + " not found or not associated with service defn with id " 
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
