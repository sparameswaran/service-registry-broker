package org.cf.serviceregistry.controller;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cf.serviceregistry.repository.CredentialsRepository;
import org.cf.serviceregistry.repository.PlanRepository;
import org.cf.serviceregistry.repository.ServiceRepository;
import org.cf.serviceregistry.servicebroker.model.Credentials;
import org.cf.serviceregistry.servicebroker.model.Plan;
import org.cf.serviceregistry.servicebroker.model.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
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
		
		newCreds.generateAndSetId();
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

		Optional<Service> services = serviceRepo.findByServiceIdOrName(serviceName);		
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
				if (newCreds == null)
					continue;
				
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
	
	@ExceptionHandler({IllegalArgumentException.class, NullPointerException.class})
	void handleBadRequests(HttpServletResponse response) throws IOException {
	    response.sendError(HttpStatus.BAD_REQUEST.value());
	}
	
	@RequestMapping(value = "/searchService",
			method = RequestMethod.GET)
	public ResponseEntity<Object> servicesWithName(@RequestParam(value="name") String name) {
		List<String> serviceNames = serviceRepo.findServiceContainingName(name);
		return new ResponseEntity<>(serviceNames, HttpStatus.OK);
	}
	
	@RequestMapping(value = "/searchServiceByProvider",
			method = RequestMethod.GET)
	public ResponseEntity<Object> servicesWithProviderName(@RequestParam(value="name") String name) {
		List<String> serviceNames = serviceRepo.findServiceContainingProviderName(name);
		return new ResponseEntity<>(serviceNames, HttpStatus.OK);
	}
	

	@RequestMapping("/services")
	public ResponseEntity<Object> services() {
		return new ResponseEntity<>(serviceRepo.findAll(), HttpStatus.OK);	
	}

	// Bulk creation of service defns
	@RequestMapping(value = "/services", method = RequestMethod.POST)
	public ResponseEntity<String> createServices(
			@RequestBody Service[] serviceEndpointInstances) {
		log.info("Incoming payload: " + serviceEndpointInstances);
		
		for(Service serviceInstance: serviceEndpointInstances) {
			System.out.println("New service: " + serviceInstance);
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
	
	@RequestMapping(value = "/services/{serviceIdOrName}", method = RequestMethod.GET)
	public ResponseEntity<Object> find(@PathVariable("serviceIdOrName") String serviceIdOrName) {

		Optional<Service> services = serviceRepo.findByServiceIdOrName(serviceIdOrName);
		if (services.isPresent()) {
			return new ResponseEntity<>(services.get(), HttpStatus.OK);			
		}	
		
		return new ResponseEntity<>(
				"{\"description\": \"Service with name or id: "
						+ serviceIdOrName + " not found\"}",
				HttpStatus.BAD_REQUEST);		
	}
	
	@RequestMapping(value = "/services/{serviceIdOrName}", method = RequestMethod.PATCH)
	public ResponseEntity<Object> updateService(@PathVariable("serviceIdOrName") String serviceIdOrName, 
										@RequestBody Service updatedServiceInstance) {

		Optional<Service> services = serviceRepo.findByServiceIdOrName(serviceIdOrName);
		if (!services.isPresent()) {
			return new ResponseEntity<Object>(
					"{\"description\": \"Service with id or name: "
							+ serviceIdOrName + " not found\"}",
					HttpStatus.BAD_REQUEST);			
		}
		
		Service service = services.get();

		service.update(updatedServiceInstance);
		serviceRepo.save(service);
		return new ResponseEntity<Object>("{}", HttpStatus.OK);	
	}

	@RequestMapping(value = "/services/{serviceIdOrName}", 
					method = RequestMethod.DELETE)
	public ResponseEntity<Object> deleteServiceDefn(
			@PathVariable("serviceIdOrName") String serviceIdOrName) {
		
		Optional<Service> services = serviceRepo.findByServiceIdOrName(serviceIdOrName);
		if (!services.isPresent()) {
			return new ResponseEntity<>("{}", HttpStatus.GONE);			
		}
		
		Service existingService = services.get();;
		
		// Clean up associated plans
		planRepo.delete(existingService.getPlans());
		serviceRepo.delete(existingService.getId());
		return new ResponseEntity<>("{}", HttpStatus.OK);
	}
	
	
	@RequestMapping(value = "/services/{serviceIdOrName}/plans", 
			method = RequestMethod.GET)
	public ResponseEntity<Object> getServicePlanDefns(
		@PathVariable("serviceIdOrName") String serviceIdOrName) {
		
		Optional<Service> services = serviceRepo.findByServiceIdOrName(serviceIdOrName);
		if (!services.isPresent()) {
			return new ResponseEntity<>(
					"{\"description\": \"Service with id or name: "
							+ serviceIdOrName + " not found\"}",
					HttpStatus.BAD_REQUEST);			
		}
		
		Service existingService = services.get();
		return new ResponseEntity<>(existingService.getPlans(), HttpStatus.OK);		
	}

	
	@RequestMapping(value = "/services/{serviceIdOrName}/plans/{planName}", 
					method = RequestMethod.GET)
	public ResponseEntity<Object> getServicePlanDefn(
			@PathVariable("serviceIdOrName") String serviceIdOrName,
			@PathVariable("planName") String planName) {
		
		Optional<Service> services = serviceRepo.findByServiceIdOrName(serviceIdOrName);
		if (!services.isPresent()) {
			return new ResponseEntity<>(
					"{\"description\": \"Service with id or name: "
							+ serviceIdOrName + " not found\"}",
					HttpStatus.BAD_REQUEST);			
		}
		
		Service existingService = services.get();
		
		Service serviceDefn = existingService;
		
		Plan servicePlanInstance = new Plan();
		servicePlanInstance.setName(planName);
		
		Optional<Plan> plans = planRepo.findByPlanIdOrNameAndServiceId(planName, existingService.getId());
		
		if (plans.isPresent()) {
			return new ResponseEntity<Object>(plans.get(), HttpStatus.OK);
		}
		
		return new ResponseEntity<>("{\"description\": \"Plan with name: "
					+ planName + " not found\"}",
					HttpStatus.BAD_REQUEST);			
	}

	@RequestMapping(value = "/services/{serviceIdOrName}/plans", 
					method = RequestMethod.PUT)
	public ResponseEntity<Object> createServicePlanDefn(
			@PathVariable("serviceIdOrName") String serviceIdOrName,
			@RequestBody Plan servicePlanInstance) {
		
		Optional<Service> services = serviceRepo.findByServiceIdOrName(serviceIdOrName);
		if (!services.isPresent()) {
			return new ResponseEntity<>(
					"{\"description\": \"Service with id or name: "
							+ serviceIdOrName + " not found\"}",
					HttpStatus.BAD_REQUEST);			
		}

		Service existingService = services.get();
		
		//Plan existingPlan = planRepo.findOne(planPk);
		Optional<Plan> plans = planRepo.findByPlanIdOrNameAndServiceId(servicePlanInstance.getName(), 
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
	
	@RequestMapping(value = "/plans/{planId}", method = RequestMethod.GET)
	public ResponseEntity<Object> findPlan(@PathVariable("planId") String planId) {

		Plan plan = planRepo.findOne(planId);
		if (plan != null) {
			return new ResponseEntity<Object>(plan, HttpStatus.OK);			
		}
		
		return new ResponseEntity<Object>(
				"{\"description\": \"Plan with id: "
						+ planId + " not found\"}",
				HttpStatus.BAD_REQUEST);		
	}

	
	@RequestMapping(value = "/plans/{planId}", method = RequestMethod.PATCH)
	public ResponseEntity<Object> updatePlan(@PathVariable("planId") String planId, 
										@RequestBody Plan updatedPlanInstance) {

		Plan plan = planRepo.findOne(planId);
		if (plan != null) {

			plan.update(updatedPlanInstance);
			planRepo.save(plan);
			return new ResponseEntity<Object>("{}", HttpStatus.OK);			
		} 
		
		return new ResponseEntity<Object>(
				"{\"description\": \"Plan with id: "
						+ planId + " not found\"}",
				HttpStatus.BAD_REQUEST);		
	}

	@RequestMapping(value = "/plans/{planId}", 
			method = RequestMethod.DELETE)
	public ResponseEntity<String> deletePlanDefn(
		@PathVariable("planId") String planId) {
	
		Plan existingPlan = planRepo.findOne(planId);
		if (existingPlan == null) {
			return new ResponseEntity<>("{}", HttpStatus.GONE);
		}
		
		// Remove from associated service
		Service service = existingPlan.getService();
		
		service.removePlan(existingPlan);
		planRepo.delete(planId);
		
		return new ResponseEntity<>("{}", HttpStatus.OK);
	}
	
	@RequestMapping(value = "/credentialsForPlan",
			method = RequestMethod.GET)
	public ResponseEntity<Object> getCredsForPlan(@RequestParam(value="planId") String planId) {
		String credId = planRepo.getCredentialIdFromPlanId(planId);
		if (credId == null) {
			return new ResponseEntity<Object>(
					"{\"description\": \"No Credential found for Plan with id: "
							+ planId + "\" }",
					HttpStatus.BAD_REQUEST);		
		}	
		return new ResponseEntity<>(credentialsRepo.findOne(credId), HttpStatus.OK);
	}
	
	@RequestMapping(value = "/credentialsForPlan",
			method = RequestMethod.PUT)
	public ResponseEntity<Object> addCredsForPlan(@RequestParam(value="planId") String planId,
			@RequestBody Credentials newCredsInstance) {
		
		Plan associatedPlan = planRepo.findOne(planId);
		if (associatedPlan == null) {
			return new ResponseEntity<Object>(
					"{\"description\": \"No Plan with id: "
							+ planId + " not found\"}",
					HttpStatus.BAD_REQUEST);		
		}
		
		log.info("Adding credentials for plan: " + associatedPlan.getName() 
				+ "with new credentials attributes: "  + newCredsInstance);
		
		Credentials cred = associatedPlan.getCredentials();
				

		if (cred != null) {
			return new ResponseEntity<Object>(
					"{\"description\": \"Credentials already exist for plan with id: "
							+ planId + " found. Invoke PATCH instead to update credentials\"}",
					HttpStatus.BAD_REQUEST);	
		}
				
		ResponseEntity<String> credsCreateStatus = createCredentials(newCredsInstance);
		associatedPlan.setCredentials(newCredsInstance);
		planRepo.save(associatedPlan);
		
		return new ResponseEntity<Object>("{}", HttpStatus.OK);			
	}
	
	@RequestMapping(value = "/credentialsForPlan",
			method = RequestMethod.PATCH)
	public ResponseEntity<Object> updateCredsForPlan(@RequestParam(value="planId") String planId,
			@RequestBody Credentials updatedCredInstance) {
		
		Plan associatedPlan = planRepo.findOne(planId);
		if (associatedPlan == null) {
			return new ResponseEntity<Object>(
					"{\"description\": \"No Plan with id: "
							+ planId + " not found\"}",
					HttpStatus.BAD_REQUEST);		
		}
		
		Credentials cred = associatedPlan.getCredentials();
		log.info("Updating credentials for plan: " + associatedPlan.getName() 
				+ "with newer credentials attributes: "  + updatedCredInstance);
		
		if (cred != null) {
			cred.update(updatedCredInstance);
			credentialsRepo.save(cred);
			associatedPlan.setCredentials(cred);
			planRepo.save(associatedPlan);
			System.out.println("Returning success...");
			return new ResponseEntity<Object>("{}", HttpStatus.OK);			
		} 
		
		return new ResponseEntity<Object>(
				"{\"description\": \"No Credentials found for plan with id: "
						+ planId + " found. Use PUT instead to add new credentials\"}",
				HttpStatus.BAD_REQUEST);			
	}
	
	@RequestMapping(value = "/credentialsForPlan",
			method = RequestMethod.DELETE)
	public ResponseEntity<Object> deleteCredsForPlan(@RequestParam(value="planId") String planId) {
		
		Plan associatedPlan = planRepo.findOne(planId);
		if (associatedPlan == null) {
			return new ResponseEntity<Object>(
					"{\"description\": \"No Plan with id: "
							+ planId + " not found\"}",
					HttpStatus.BAD_REQUEST);		
		}
		
		Credentials cred = associatedPlan.getCredentials();
		log.info("Removing credentials for plan: " + associatedPlan.getName());
		
		associatedPlan.setCredentials(null);
		planRepo.save(associatedPlan);
		
		return new ResponseEntity<Object>("{}", HttpStatus.OK);			
	}
	
	@RequestMapping(value = "/services/{serviceIdOrName}/plans", 
					method = RequestMethod.PATCH)
	public ResponseEntity<String> updateServicePlanDefn(
			@PathVariable("serviceIdOrName") String serviceIdOrName,
			@RequestBody Plan servicePlanInstance) {
		Optional<Service> services = serviceRepo.findByServiceIdOrName(serviceIdOrName);
		if (!services.isPresent()) {
			return new ResponseEntity<>(
					"{\"description\": \"Service with id or name: "
							+ serviceIdOrName + " not found\"}",
					HttpStatus.BAD_REQUEST);			
		}

		Service existingService = services.get();
		
		//Plan existingPlan = planRepo.findOne(planPk);
		Optional<Plan> plans = planRepo.findByPlanIdOrNameAndServiceId(servicePlanInstance.getName(), 
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

	@RequestMapping(value = "/services/{serviceIdOrName}/plans/{planIdOrName}", 
					method = RequestMethod.DELETE)
	public ResponseEntity<String> deleteServicePlanDefn(
			@PathVariable("serviceIdOrName") String serviceIdOrName,
			@PathVariable("planIdOrName") String planIdOrName) {
		
		Optional<Service> services = serviceRepo.findByServiceIdOrName(serviceIdOrName);
		if (!services.isPresent()) {
			return new ResponseEntity<>(
					"{\"description\": \"Service with id or name: "
							+ serviceIdOrName + " not found\"}",
					HttpStatus.BAD_REQUEST);			
		}

		Service existingService = services.get();

		//Plan existingPlan = planRepo.findOne(planPk);
		Optional<Plan> plans = planRepo.findByPlanIdOrNameAndServiceId(planIdOrName, 
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

	@RequestMapping(value = "/services/{serviceIdOrName}/plans/{planIdOrName}/creds", 
			method = RequestMethod.GET)
	public ResponseEntity<Object> getServicePlanCredsDefn(
		@PathVariable("serviceIdOrName") String serviceIdOrName,
		@PathVariable("planIdOrName") String planIdOrName) {
		
		Optional<Service> services = serviceRepo.findByServiceIdOrName(serviceIdOrName);
		if (!services.isPresent()) {
			return new ResponseEntity<>(
					"{\"description\": \"Service with id or name: "
							+ serviceIdOrName + " not found\"}",
					HttpStatus.BAD_REQUEST);			
		}

		Service existingService = services.get();

		//Plan existingPlan = planRepo.findOne(planPk);
		Optional<Plan> plans = planRepo.findByPlanIdOrNameAndServiceId(planIdOrName, 
																existingService.getId());

		if (!plans.isPresent()) {
			return new ResponseEntity<>("{\"description\": \"Plan with id or name: "
					+ planIdOrName + " not found or not associated with service defn with name: " 
					+ serviceIdOrName + "\"}",
					HttpStatus.BAD_REQUEST);
		}
		
		Plan existingPlan = plans.get();
		Credentials credsInstance = existingPlan.getCredentials();
		if (credsInstance != null) 
			return new ResponseEntity<>(credsInstance, HttpStatus.OK);	
			
		return new ResponseEntity<>("{\"description\": \"Found no credentials!! \"}",
				HttpStatus.BAD_REQUEST);
	}

	@RequestMapping(value = "/services/{serviceIdOrName}/plans/{planIdOrName}/creds", 
			method = RequestMethod.PUT)
	public ResponseEntity<String> createServicePlanCredsDefn(
		@PathVariable("serviceIdOrName") String serviceIdOrName,
		@PathVariable("planIdOrName") String planIdOrName,
		@RequestBody Credentials servicePlanCredsInstance) {
		
		Optional<Service> services = serviceRepo.findByServiceIdOrName(serviceIdOrName);
		if (!services.isPresent()) {
			return new ResponseEntity<>(
					"{\"description\": \"Service with id or name: "
							+ serviceIdOrName + " not found\"}",
					HttpStatus.BAD_REQUEST);			
		}
			
		Service existingService = services.get();
		//Plan existingPlan = planRepo.findOne(planPk);
		Optional<Plan> plans = planRepo.findByPlanIdOrNameAndServiceId(planIdOrName, 
																existingService.getId());

		if (!plans.isPresent()) {
			return new ResponseEntity<>("{\"description\": \"Plan with id or name: "
					+ planIdOrName + " not found or not associated with service defn with name: " 
					+ serviceIdOrName + "\"}",
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
	
	@RequestMapping(value = "/services/{serviceIdOrName}/plans/{planName}/creds", 
			method = RequestMethod.PATCH)
	public ResponseEntity<String> updateServicePlanCredsDefn(
		@PathVariable("serviceIdOrName") String serviceIdOrName,
		@PathVariable("planIdOrName") String planIdOrName,
		@RequestBody Credentials servicePlanCredsInstance) {
		
		Optional<Service> services = serviceRepo.findByServiceIdOrName(serviceIdOrName);
		if (!services.isPresent()) {
			return new ResponseEntity<>(
					"{\"description\": \"Service with id or name: "
							+ serviceIdOrName + " not found\"}",
					HttpStatus.BAD_REQUEST);			
		}
			
		Service existingService = services.get();
		//Plan existingPlan = planRepo.findOne(planPk);
		Optional<Plan> plans = planRepo.findByPlanIdOrNameAndServiceId(planIdOrName, 
																existingService.getId());

		if (!plans.isPresent()) {
			return new ResponseEntity<>("{\"description\": \"Plan with id or name: "
					+ planIdOrName + " not found or not associated with service defn with name: " 
					+ serviceIdOrName + "\"}",
					HttpStatus.BAD_REQUEST);
		}
		
		Plan existingPlan = plans.get();
		Credentials creds = existingPlan.getCredentials();
		
		if (creds != null) {
			creds.update(servicePlanCredsInstance);
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

	@RequestMapping(value = "/credentials/{credId}", method = RequestMethod.GET)
	public ResponseEntity<Object> findCredentials(@PathVariable("credId") String credId) {

		Credentials cred = credentialsRepo.findOne(credId);
		if (cred != null) {
			return new ResponseEntity<Object>(cred, HttpStatus.OK);			
		}
		
		return new ResponseEntity<Object>(
				"{\"description\": \"Credentials with id: "
						+ credId + " not found\"}",
				HttpStatus.BAD_REQUEST);		
	}

	@RequestMapping(value = "/credentials/{credId}", method = RequestMethod.PATCH)
	public ResponseEntity<Object> updateCredentials(@PathVariable("credId") String credentialsId, 
										@RequestBody Credentials updatedCredInstance) {

		System.out.println("Entering credential update against data: " + updatedCredInstance);
		Credentials cred = credentialsRepo.findOne(credentialsId);
		if (cred != null) {

			cred.update(updatedCredInstance);
			credentialsRepo.save(cred);
			System.out.println("REturning success...");
			return new ResponseEntity<Object>("{}", HttpStatus.OK);			
		} 
		
		return new ResponseEntity<Object>(
				"{\"description\": \"Credentials with id: "
						+ credentialsId + " not found\"}",
				HttpStatus.BAD_REQUEST);		
	}
	
	@RequestMapping(value = "/credentials/{credId}", 
			method = RequestMethod.DELETE)
	public ResponseEntity<String> deleteCredentials(
		@PathVariable("credId") String credId) {
	
		Credentials cred = credentialsRepo.findOne(credId);
		if (cred == null) {
			return new ResponseEntity<>("{}", HttpStatus.GONE);
		}
		
		// Remove from associated service
		Optional<Plan> plans = planRepo.findPlanFromCredentialId(credId);
		if (plans.isPresent()) {
			Plan associatedPlan = plans.get();
			associatedPlan.setCredentials(null);
		}
		
		credentialsRepo.delete(credId);
		
		return new ResponseEntity<>("{}", HttpStatus.OK);
	}
	
	@RequestMapping(value = "/services/{serviceIdOrName}/plans/{planIdOrName}/creds", 
					method = RequestMethod.DELETE)
	public ResponseEntity<String> deleteServicePlanCredsDefn(
			@PathVariable("serviceIdOrName") String serviceIdOrName,
			@PathVariable("planIdOrName") String planIdOrName) {
		
		Optional<Service> services = serviceRepo.findByServiceIdOrName(serviceIdOrName);
		if (!services.isPresent()) {
			return new ResponseEntity<>(
					"{\"description\": \"Service with id or name: "
							+ serviceIdOrName + " not found\"}",
					HttpStatus.BAD_REQUEST);			
		}

		Service existingService = services.get();
		
		Optional<Plan> plans = planRepo.findByPlanIdOrNameAndServiceId(planIdOrName, 
																existingService.getId());

		if (!plans.isPresent()) {
			return new ResponseEntity<>("{\"description\": \"Plan with id or name: "
					+ planIdOrName + " not found or not associated with service defn with name: " 
					+ serviceIdOrName + "\"}",
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
