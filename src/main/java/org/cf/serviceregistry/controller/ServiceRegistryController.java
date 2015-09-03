package org.cf.serviceregistry.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

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
					"{\"description\": \"Plan with name: "
					 	+ newPlan.getName()
					 	+ " and id: "
						+ newPlan.getId()					
						+ " already exists\"}",
						HttpStatus.CONFLICT);		
		}
		
		Credentials newCreds = newPlan.getCredentials();
		if (newCreds != null) {
			//log.info("Credentials to be inserted: " + newCreds);
			ResponseEntity<String> credsCreateStatus = createCredentials(newCreds);
			if (credsCreateStatus.getStatusCode() != HttpStatus.OK) {
				return credsCreateStatus;
			}
		}		
		
		newPlan.generateAndSetId();	
		
		planRepo.save(newPlan);
		log.debug("Service Plan created: " + newPlan);
		return new ResponseEntity<>("{}", HttpStatus.OK);
	}

	public ResponseEntity<String> createService(Service serviceEndpointInstance) {
		
		String serviceName = serviceEndpointInstance.getName();
		
		//log.info("Got /services call with payload : "
		//		+ serviceEndpointInstance);

		Optional<Service> services = serviceRepo.findByServiceName(serviceName);		
		if (services.isPresent()) {
			
			return new ResponseEntity<>(
					"{\"description\": \"Service with name: "
						+ serviceName					
						+ " already exists\"}",
						HttpStatus.CONFLICT);		
		} 
			
		serviceEndpointInstance.generateAndSetId();
		//log.info("After setting service Id, service id: " + serviceEndpointInstance.getId());
		
		Set<Plan> plans = serviceEndpointInstance.getPlans();		
		if (plans != null) {			
			for (Plan newPlan: plans) {				
				newPlan.setService(serviceEndpointInstance);
				newPlan.generateAndSetId();					
				//log.info("Associated Plan: " + newPlan);
				
				// Save the Credentials ahead of the Service or Plan
				Credentials newCreds = newPlan.getCredentials();					
				ResponseEntity<String> responseStatus = createCredentials(newCreds);
				if (responseStatus.getStatusCode() != HttpStatus.OK) {
					return new ResponseEntity<>("{\"description\": \"Problem persisting credentials!! \"}",
					HttpStatus.INTERNAL_SERVER_ERROR);
				}
				
				//log.info("After setting service Id, New Plan Id: " + newPlan.getId() 
				// 		+ " and associated service is: " + newPlan.getService());				
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
	
	@RequestMapping(value = "/services/{serviceName}", method = RequestMethod.GET)
	public ResponseEntity<Object> find(@PathVariable("serviceName") String serviceName) {

		Optional<Service> services = serviceRepo.findByServiceName(serviceName);
		if (services.isPresent()) {

			Service existingService = services.get();
			return new ResponseEntity<Object>(existingService, HttpStatus.OK);			
		} 
		
		return new ResponseEntity<Object>(
				"{\"description\": \"Service with name: "
						+ serviceName + " not found\"}",
				HttpStatus.BAD_REQUEST);		
	}

	@RequestMapping(value = "/services/{serviceName}", 
					method = RequestMethod.DELETE)
	public ResponseEntity<String> deleteServiceDefn(
			@PathVariable("serviceName") String serviceName) {
		
		Optional<Service> services = serviceRepo.findByServiceName(serviceName);
		if (!services.isPresent()) {
			return new ResponseEntity<>("{}", HttpStatus.GONE);
		}
			
		Service existingService = services.get();
		
		// Clean up associated plans
		planRepo.delete(existingService.getPlans());
		serviceRepo.delete(serviceName);
		return new ResponseEntity<>("{}", HttpStatus.OK);
	}
	
	@RequestMapping(value = "/services/{serviceName}/plans", 
			method = RequestMethod.GET)
	public ResponseEntity<Object> getServicePlanDefns(
		@PathVariable("serviceName") String serviceName) {
		
		Optional<Service> services = serviceRepo.findByServiceName(serviceName);
		if (!services.isPresent()) {
			return new ResponseEntity<>("{\"description\": \"Service with name: "
					+ serviceName + " not found\"}",
			HttpStatus.BAD_REQUEST);
		}
		
		Service existingService = services.get();
		Set<Plan> servicePlanInstances = existingService.getPlans();
		
		Map<String, Iterable<Plan>> wrapper = new HashMap<>();
		wrapper.put("plans", servicePlanInstances);
		return new ResponseEntity<>(wrapper, HttpStatus.OK);		
	}

	
	@RequestMapping(value = "/services/{serviceName}/plans/{planName}", 
					method = RequestMethod.GET)
	public ResponseEntity<Object> getServicePlanDefn(
			@PathVariable("serviceName") String serviceName,
			@PathVariable("planName") String planName) {
		
		Optional<Service> services = serviceRepo.findByServiceName(serviceName);
		if (!services.isPresent()) {
			return new ResponseEntity<>("{\"description\": \"Service with name: "
					+ serviceName + " not found\"}",
			HttpStatus.BAD_REQUEST);
		}

		Service existingService = services.get();
		
		Service serviceDefn = existingService;
		
		Plan servicePlanInstance = new Plan();
		servicePlanInstance.setName(planName);
		
		Optional<Plan> plans = planRepo.findByPlanNameAndServiceId(planName, existingService.getId());
		
		if (plans.isPresent()) {
			return new ResponseEntity<Object>(plans.get(), HttpStatus.OK);
		}
		
		return new ResponseEntity<>("{\"description\": \"Plan with name: "
					+ planName + " not found\"}",
					HttpStatus.BAD_REQUEST);			
	}

	@RequestMapping(value = "/services/{serviceName}/plans", 
					method = RequestMethod.PUT)
	public ResponseEntity<String> createServicePlanDefn(
			@PathVariable("serviceName") String serviceName,
			@RequestBody Plan servicePlanInstance) {
		Optional<Service> services = serviceRepo.findByServiceName(serviceName);
		if (!services.isPresent()) {
			return new ResponseEntity<>("{\"description\": \"Service with name: "
					+ serviceName + " not found\"}",
			HttpStatus.BAD_REQUEST);
		}

		Service existingService = services.get();
		
		//Plan existingPlan = planRepo.findOne(planPk);
		Optional<Plan> plans = planRepo.findByPlanNameAndServiceId(servicePlanInstance.getName(), 
																existingService.getId());
		
		if (plans.isPresent()) {
			return new ResponseEntity<>("{\"description\": \"Plan with name: "
					+ servicePlanInstance.getName() + " already exists, use PATCH to update\"}",
			HttpStatus.BAD_REQUEST);
		}
			
		servicePlanInstance.generateAndSetId();
		existingService.addPlan(servicePlanInstance);
		serviceRepo.save(existingService);
	
		return new ResponseEntity<>("{}", HttpStatus.OK);
	}
	

	@RequestMapping(value = "/services/{serviceName}/plans", 
					method = RequestMethod.PATCH)
	public ResponseEntity<String> updateServicePlanDefn(
			@PathVariable("serviceName") String serviceName,
			@RequestBody Plan servicePlanInstance) {
		Optional<Service> services = serviceRepo.findByServiceName(serviceName);
		if (!services.isPresent()) {
			return new ResponseEntity<>("{\"description\": \"Service with name: "
					+ serviceName + " not found\"}",
			HttpStatus.BAD_REQUEST);
		}

		Service existingService = services.get();
		
		//Plan existingPlan = planRepo.findOne(planPk);
		Optional<Plan> plans = planRepo.findByPlanNameAndServiceId(servicePlanInstance.getName(), 
																existingService.getId());
		
		if (!plans.isPresent()) {
			return new ResponseEntity<>("{\"description\": \"Plan with name: "
					+ servicePlanInstance.getName() + " does not exist, use PUT to insert\"}",
			HttpStatus.BAD_REQUEST);
		}
		
		Plan plan = plans.get();			
		plan.copy(servicePlanInstance);
		planRepo.save(plan);
	
		return new ResponseEntity<>("{}", HttpStatus.OK);
	}

	@RequestMapping(value = "/services/{serviceName}/plans/{planName}", 
					method = RequestMethod.DELETE)
	public ResponseEntity<String> deleteServicePlanDefn(
			@PathVariable("serviceName") String serviceName,
			@PathVariable("planName") String planName) {
		
		Optional<Service> services = serviceRepo.findByServiceName(serviceName);
		if (!services.isPresent()) {
			return new ResponseEntity<>("{\"description\": \"Service with name: "
					+ serviceName + " not found\"}",
			HttpStatus.BAD_REQUEST);
		}

		Service existingService = services.get();

		//Plan existingPlan = planRepo.findOne(planPk);
		Optional<Plan> plans = planRepo.findByPlanNameAndServiceId(planName, 
																existingService.getId());

		if (!plans.isPresent()) {
			return new ResponseEntity<>("{}", HttpStatus.GONE);
		}

		Plan unwantedPlan = plans.get();
		existingService.getPlans().remove(unwantedPlan);
		
		planRepo.delete(unwantedPlan.getId());
		serviceRepo.save(existingService);
		
		return new ResponseEntity<>("{}", HttpStatus.OK);
	}

	@RequestMapping(value = "/services/{serviceName}/plans/{planName}/creds", 
			method = RequestMethod.GET)
	public ResponseEntity<Object> getServicePlanCredsDefn(
		@PathVariable("serviceName") String serviceName,
		@PathVariable("planName") String planName) {
		
		Optional<Service> services = serviceRepo.findByServiceName(serviceName);
		if (!services.isPresent()) {
			return new ResponseEntity<>("{\"description\": \"Service with name: "
					+ serviceName + " not found\"}",
			HttpStatus.BAD_REQUEST);
		}

		Service existingService = services.get();

		//Plan existingPlan = planRepo.findOne(planPk);
		Optional<Plan> plans = planRepo.findByPlanNameAndServiceId(planName, 
																existingService.getId());

		if (!plans.isPresent()) {
			return new ResponseEntity<>("{\"description\": \"Plan with name: "
					+ planName + " not found or not associated with service defn with name: " 
					+ serviceName + "\"}",
					HttpStatus.BAD_REQUEST);
		}
		
		Plan existingPlan = plans.get();
		Credentials credsInstance = existingPlan.getCredentials();
		if (credsInstance != null) 
			return new ResponseEntity<>(credsInstance, HttpStatus.OK);	
			
		return new ResponseEntity<>("{\"description\": \"Found no credentials!! \"}",
				HttpStatus.BAD_REQUEST);
	}

	@RequestMapping(value = "/services/{serviceName}/plans/{planName}/creds", 
			method = RequestMethod.PUT)
	public ResponseEntity<String> createServicePlanCredsDefn(
		@PathVariable("serviceName") String serviceName,
		@PathVariable("planName") String planName,
		@RequestBody Credentials servicePlanCredsInstance) {
		
		Optional<Service> services = serviceRepo.findByServiceName(serviceName);
		if (!services.isPresent()) {
			return new ResponseEntity<>("{\"description\": \"Service with name: "
					+ serviceName + " not found\"}",
			HttpStatus.BAD_REQUEST);
		}
			
		Service existingService = services.get();
		//Plan existingPlan = planRepo.findOne(planPk);
		Optional<Plan> plans = planRepo.findByPlanNameAndServiceId(planName, 
																existingService.getId());

		if (!plans.isPresent()) {
			return new ResponseEntity<>("{\"description\": \"Plan with name: "
					+ planName + " not found or not associated with service defn with name: " 
					+ serviceName + "\"}",
					HttpStatus.BAD_REQUEST);
		}
		
		Plan existingPlan = plans.get();

		if (existingPlan.getCredentials() != null) {
			return new ResponseEntity<>("{\"description\": \"Credentials already exist, use PATCH to update!! \"}",
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
	}
	
	@RequestMapping(value = "/services/{serviceName}/plans/{planName}/creds", 
			method = RequestMethod.PATCH)
	public ResponseEntity<String> updateServicePlanCredsDefn(
		@PathVariable("serviceName") String serviceName,
		@PathVariable("planName") String planName,
		@RequestBody Credentials servicePlanCredsInstance) {
		
		Optional<Service> services = serviceRepo.findByServiceName(serviceName);
		if (!services.isPresent()) {
			return new ResponseEntity<>("{\"description\": \"Service with name: "
					+ serviceName + " not found\"}",
			HttpStatus.BAD_REQUEST);
		}
			
		Service existingService = services.get();
		//Plan existingPlan = planRepo.findOne(planPk);
		Optional<Plan> plans = planRepo.findByPlanNameAndServiceId(planName, 
																existingService.getId());

		if (!plans.isPresent()) {
			return new ResponseEntity<>("{\"description\": \"Plan with name: "
					+ planName + " not found or not associated with service defn with name: " 
					+ serviceName + "\"}",
					HttpStatus.BAD_REQUEST);
		}
		
		Plan existingPlan = plans.get();
		Credentials creds = existingPlan.getCredentials();
		
		if (creds != null) {
			creds.copy(servicePlanCredsInstance);
		} else {
			ResponseEntity<String> responseStatus = createCredentials(servicePlanCredsInstance);
		
			if (responseStatus.getStatusCode() != HttpStatus.OK) {
				return new ResponseEntity<>("{\"description\": \"Problem persisting credentials!! \"}",
				HttpStatus.INTERNAL_SERVER_ERROR);
			}
			existingPlan.setCredentials(servicePlanCredsInstance);
		}
		
		planRepo.save(existingPlan);
		
		return new ResponseEntity<>("{}", HttpStatus.OK);
	}
	
	@RequestMapping(value = "/services/{serviceName}/plans/{planName}/creds", 
					method = RequestMethod.DELETE)
	public ResponseEntity<String> deleteServicePlanCredsDefn(
			@PathVariable("serviceName") String serviceName,
			@PathVariable("planName") String planName) {
		
		Optional<Service> services = serviceRepo.findByServiceName(serviceName);
		if (!services.isPresent()) {
			return new ResponseEntity<>("{\"description\": \"Service with name: "
					+ serviceName + " not found\"}",
			HttpStatus.BAD_REQUEST);
		}

		Service existingService = services.get();
		
		Optional<Plan> plans = planRepo.findByPlanNameAndServiceId(planName, 
																existingService.getId());

		if (!plans.isPresent()) {
			return new ResponseEntity<>("{\"description\": \"Plan with name: "
					+ planName + " not found or not associated with service defn with name: " 
					+ serviceName + "\"}",
					HttpStatus.BAD_REQUEST);
		}
		
		Plan existingPlan = plans.get();
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
	}
}
