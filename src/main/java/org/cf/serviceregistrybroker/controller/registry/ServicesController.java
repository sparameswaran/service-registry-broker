package org.cf.serviceregistrybroker.controller.registry;

import java.util.Collection;
import java.util.Map;

import org.apache.log4j.Logger;
import org.cf.serviceregistrybroker.exception.ResourceDoesNotExistException;
import org.cf.serviceregistrybroker.exception.ResourceExistsException;
import org.cf.serviceregistrybroker.exception.ServiceBrokerException;
import org.cf.serviceregistrybroker.model.Credentials;
import org.cf.serviceregistrybroker.model.Plan;
import org.cf.serviceregistrybroker.model.ServiceDefinition;
import org.cf.serviceregistrybroker.registry.service.CredentialsService;
import org.cf.serviceregistrybroker.registry.service.PlanService;
import org.cf.serviceregistrybroker.registry.service.ServiceDefinitionService;
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
public class ServicesController {

	private static final Logger log = Logger.getLogger(ServicesController.class);

	@Autowired
	ServiceDefinitionService serviceDefnService;
	
	@Autowired
	PlanService planService;
	
	@Autowired
	CredentialsService credentialsService;

	private ResponseEntity<Object> planNotFound(String planId) {
		log.error("Error: Plan not found with id: " + planId);
		return new ResponseEntity<Object>(
				"{\"description\": \"No Plan with id: "
						+ planId + " was found\" }",
				HttpStatus.NOT_FOUND);
	}
	
	private ResponseEntity<Object> serviceNotFound(String serviceId) {
		log.error("Error: Service not found with id or name: " + serviceId);
		return new ResponseEntity<Object>(
				"{\"description\": \"No Service with id or name: "
						+ serviceId + " was found\" }",
				HttpStatus.NOT_FOUND);
	}
	
	@RequestMapping(value = "/services", method = RequestMethod.GET)
	public ResponseEntity<Object> findAllServices() {
		return new ResponseEntity<>(serviceDefnService.findAll(), HttpStatus.OK);	
	}
	
	@RequestMapping(value = "/service", method = RequestMethod.POST)
	public ResponseEntity<String> createService(
			@RequestBody ServiceDefinition serviceEndpointInstance) {
		
		String serviceName = serviceEndpointInstance.getName();
		
		ServiceDefinition associatedService = null;
		try {
			associatedService = (ServiceDefinition)serviceDefnService.find(serviceName);			
			if (associatedService != null) {
				return new ResponseEntity<>(
						"{\"description\": \"Service with name: "
							+ serviceName					
							+ " already exists\"}",
							HttpStatus.CONFLICT);
			}
		} catch(Exception e) { }
		
		try {
			serviceDefnService.add(null, serviceEndpointInstance);
			log.debug("Service Instance created: " + serviceEndpointInstance);		
			return new ResponseEntity<>("{}", HttpStatus.OK);
		} catch(Exception e) { 
			return new ResponseEntity<>("{Problem adding service definition!!,"
					+ ", error msg: " + e.getMessage() 
					+ ", check logs for more details.}", 
					HttpStatus.OK);
							
		}
	}
	
	@RequestMapping(value = "/searchByProvider",
			method = RequestMethod.GET)
	public ResponseEntity<Object> servicesWithProviderName(@RequestParam(value="name") String name) {
		Collection associatedServiceNames = null;
		try {
			associatedServiceNames = serviceDefnService.findServiceDefinitionByProvider(name);			
			return new ResponseEntity<>(associatedServiceNames, HttpStatus.OK);
		} catch(ResourceDoesNotExistException e) { 
			return new ResponseEntity<>(
					"{\"description\": \"Service of type provider: "
						+ name					
						+ " does not exists\"}",
						HttpStatus.NOT_FOUND);
		}
	}
	
	@RequestMapping(value = "/searchByName",
			method = RequestMethod.GET)
	public ResponseEntity<Object> serviceWithName(@PathVariable("name") String startname) {
		Collection associatedServiceNames = null;
		try {
			associatedServiceNames = serviceDefnService.findServiceDefinitionByName(startname);			
			return new ResponseEntity<>(associatedServiceNames, HttpStatus.OK);
		} catch(ResourceDoesNotExistException e) { 
			return new ResponseEntity<>(
					"{\"description\": \"Service with starting name: "
						+ startname					
						+ " does not exists\"}",
						HttpStatus.NOT_FOUND);
		}
	}

	// Bulk creation of service defns
	@RequestMapping(value = "/services", method = RequestMethod.POST)
	public ResponseEntity<String> createServices(
			@RequestBody ServiceDefinition[] serviceEndpointInstances) {
		log.info("Incoming payload: " + serviceEndpointInstances);
		
		try {
			serviceDefnService.add(null, serviceEndpointInstances);
			return new ResponseEntity<>("{}", HttpStatus.OK);
		} catch(Exception e) {
			return new ResponseEntity<>("{\"description\": " +
					"\"Problem creating service instances, nested error: " + 
					e.getMessage() + " \"}",
					HttpStatus.BAD_REQUEST);			
		}		
	}
	
	@RequestMapping(value = "/services/{serviceIdOrName}", method = RequestMethod.GET)
	public ResponseEntity<Object> find(@PathVariable("serviceIdOrName") String serviceIdOrName) {

		ServiceDefinition associatedService = null;
		try {
			associatedService = (ServiceDefinition)serviceDefnService.find(serviceIdOrName);			
			return new ResponseEntity<>(associatedService, HttpStatus.OK);
		} catch(ResourceDoesNotExistException e) { 
			return serviceNotFound(serviceIdOrName);
		}
	}
	
	@RequestMapping(value = "/services/{serviceIdOrName}", method = RequestMethod.PATCH)
	public ResponseEntity<Object> updateService(@PathVariable("serviceIdOrName") String serviceIdOrName, 
										@RequestBody ServiceDefinition updatedServiceInstance) {

		ServiceDefinition associatedService = null;
		try {
			associatedService = (ServiceDefinition)serviceDefnService.find(serviceIdOrName);
			updatedServiceInstance.setId(associatedService.getId());
			serviceDefnService.update(updatedServiceInstance);			
			return new ResponseEntity<>(associatedService, HttpStatus.OK);
		} catch(ResourceDoesNotExistException | ServiceBrokerException e) { 
			return serviceNotFound(serviceIdOrName);
		}
	}
	
	@RequestMapping(value = "/servicesVisibility/{serviceId}", method = RequestMethod.PATCH)
	public ResponseEntity<Object> updateServiceVisibility(@PathVariable("serviceId") String serviceId, 
									@RequestBody Map<String, Boolean> serviceVisibilityMap) {

		ServiceDefinition associatedService = null;
		try {
			serviceDefnService.updateServiceDefinitionVisibility(serviceId, serviceVisibilityMap.get("visible"));
			associatedService = (ServiceDefinition)serviceDefnService.find(serviceId);
			
			return new ResponseEntity<>(associatedService, HttpStatus.OK);
		} catch(ResourceDoesNotExistException | ServiceBrokerException e) { 
			return serviceNotFound(serviceId);
		}
	}

	@RequestMapping(value = "/services/{serviceIdOrName}", 
					method = RequestMethod.DELETE)
	public ResponseEntity<Object> deleteServiceDefn(
			@PathVariable("serviceIdOrName") String serviceIdOrName) {
		
		Object associatedService = null;
		
		try {
			try {
				serviceDefnService.delete(serviceIdOrName);	
			} catch(ResourceDoesNotExistException e) { 
				return serviceNotFound(serviceIdOrName);
			}
		
			associatedService = serviceDefnService.find(serviceIdOrName);
			if (associatedService != null) {
				return new ResponseEntity<>("{Error: Service " + serviceIdOrName 
						+ " cannot be deleted, probably in use by applications,"
						+ " unbind and delete service instances before deleting}", 
						HttpStatus.PRECONDITION_FAILED);			
			}
		} catch(Exception e) { }
		
		return new ResponseEntity<>("{}", HttpStatus.OK);
	}
	
	
	@RequestMapping(value = "/services/{serviceIdOrName}/plans", 
			method = RequestMethod.GET)
	public ResponseEntity<Object> getServicePlanDefns(
		@PathVariable("serviceIdOrName") String serviceIdOrName) {

		ServiceDefinition associatedService = null;
		try {
			associatedService = (ServiceDefinition)serviceDefnService.find(serviceIdOrName);
			return new ResponseEntity<>(associatedService.getPlans(), HttpStatus.OK);	
		} catch(ResourceDoesNotExistException e) { 
			return serviceNotFound(serviceIdOrName);
		}	
	}

	
	@RequestMapping(value = "/services/{serviceIdOrName}/plans/{planName}", 
					method = RequestMethod.GET)
	public ResponseEntity<Object> getServicePlanDefn(
			@PathVariable("serviceIdOrName") String serviceIdOrName,
			@PathVariable("planName") String planName) {
		
		ServiceDefinition associatedService = null;
		try {
			
			associatedService = (ServiceDefinition)serviceDefnService.find(serviceIdOrName);
			for(Plan plan: associatedService.getPlans()) {
				if (plan.getName().equals(planName) || plan.getId().equals(planName))
					return new ResponseEntity<>(plan, HttpStatus.OK);
			}
			
			return planNotFound(planName);
		} catch(ResourceDoesNotExistException e) { 
			return serviceNotFound(serviceIdOrName);
		}	
		
	}

	@RequestMapping(value = "/services/{serviceIdOrName}/plans", 
					method = RequestMethod.PUT)
	public ResponseEntity<Object> createServicePlanDefn(
			@PathVariable("serviceIdOrName") String serviceIdOrName,
			@RequestBody Plan servicePlanInstance) {
		
		ServiceDefinition associatedService = null;
		try {
			
			associatedService = (ServiceDefinition)serviceDefnService.find(serviceIdOrName);
			planService.add(associatedService.getId(), servicePlanInstance);
			return new ResponseEntity<>("{}", HttpStatus.OK);
		} catch(ResourceDoesNotExistException | ServiceBrokerException e) { 
			e.printStackTrace();
			log.error("Unable to find service with id or name: " + serviceIdOrName 
					+ " for adding a new plan: " + servicePlanInstance);
			return serviceNotFound(serviceIdOrName);
		} catch (ResourceExistsException e) {
			return new ResponseEntity<>("{\"description\": \"Plan with name: "
					+ servicePlanInstance.getName() + " already exists, use PATCH to update\"}",
			HttpStatus.BAD_REQUEST);
		}
	}
	
	@RequestMapping(value = "/services/{serviceIdOrName}/plans/{planIdOrName}", 
					method = RequestMethod.PATCH)
	public ResponseEntity<Object> updateServicePlanDefn(
			@PathVariable("serviceIdOrName") String serviceIdOrName,
			@PathVariable("planIdOrName") String planIdOrName,
			@RequestBody Plan servicePlanInstance) {
		
		ServiceDefinition associatedService = null;
		try {
			
			associatedService = (ServiceDefinition)serviceDefnService.find(serviceIdOrName);			
		} catch(ResourceDoesNotExistException e) {
			log.error("Unable to find service with id or name: " + serviceIdOrName 
					+ " for updating an existing plan with id: " + planIdOrName 
					+ " and new payload: " + servicePlanInstance);
			return serviceNotFound(serviceIdOrName);
		}
		
		try {
			for(Plan plan: associatedService.getPlans()) {
				if (plan.getName().equals(planIdOrName) || plan.getId().equals(planIdOrName)) {
					servicePlanInstance.setId(plan.getId());
					planService.update(servicePlanInstance);			
					return new ResponseEntity<>("{}", HttpStatus.OK);
				}
			}
			log.error("Unable to find existing plan with id: " + planIdOrName 
					+ " for update with payload: " + servicePlanInstance);

			return planNotFound(planIdOrName);
		} catch (Exception e) {
			e.printStackTrace();
			log.error("Unable to update existing plan with id: " + planIdOrName 
					+ " for update with payload: " + servicePlanInstance 
					+ ", error msg: " + e.getMessage());
			
			return new ResponseEntity<>("{\"description\": \"Update of Plan with name: "
					+ servicePlanInstance.getName() + " failed, check logs\"}",
			HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping(value = "/services/{serviceIdOrName}/plans/{planIdOrName}", 
					method = RequestMethod.DELETE)
	public ResponseEntity<Object> deleteServicePlanDefn(
			@PathVariable("serviceIdOrName") String serviceIdOrName,
			@PathVariable("planIdOrName") String planIdOrName) {
		
		ServiceDefinition associatedService = null;
		try {
			
			associatedService = (ServiceDefinition)serviceDefnService.find(serviceIdOrName);			
		} catch(ResourceDoesNotExistException e) { 
			log.error("Unable to find service with id or name: " + serviceIdOrName 
					+ " for deletion of an existing plan with id: " + planIdOrName);
			return serviceNotFound(serviceIdOrName);
		}
		
		try {
			for(Plan plan: associatedService.getPlans()) {
				if (plan.getName().equals(planIdOrName) || plan.getId().equals(planIdOrName)) {
					serviceDefnService.deleteChild(associatedService.getId(), planIdOrName);			
					return new ResponseEntity<>("{}", HttpStatus.OK);
				}
			}
			log.error("Unable to find existing plan with id: " + planIdOrName 
					+ " for deletion");

			return planNotFound(planIdOrName);

		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>("{\"description\": \"Delete of Plan with name: "
					+ planIdOrName + " failed, check logs, error msg: " +  e.getMessage() + "\"}",
			HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping(value = "/services/{serviceIdOrName}/plans/{planIdOrName}/creds", 
			method = RequestMethod.GET)
	public ResponseEntity<Object> getServicePlanCredsDefn(
		@PathVariable("serviceIdOrName") String serviceIdOrName,
		@PathVariable("planIdOrName") String planIdOrName) {
	
		ServiceDefinition associatedService = null;
		try {			
			associatedService = (ServiceDefinition)serviceDefnService.find(serviceIdOrName);
		} catch(ResourceDoesNotExistException e) { 
			log.error("Unable to find service with id or name: " + serviceIdOrName 
					+ " for getting existing credentials for plan with id: " + planIdOrName);
			return serviceNotFound(serviceIdOrName);
		}
		
		try {
			for(Plan plan: associatedService.getPlans()) {
				if (plan.getName().equals(planIdOrName) || plan.getId().equals(planIdOrName)) {
					return new ResponseEntity<>(plan.getCredentials(), HttpStatus.OK);
				}
			}
		} catch(Exception e) { }
		log.error("Unable to find any matching plan with id: " + planIdOrName 
				+ " to lookup credentials");
		return planNotFound(planIdOrName);		 
	}

	@RequestMapping(value = "/services/{serviceIdOrName}/plans/{planIdOrName}/creds", 
			method = RequestMethod.PUT)
	public ResponseEntity<Object> createServicePlanCredsDefn(
		@PathVariable("serviceIdOrName") String serviceIdOrName,
		@PathVariable("planIdOrName") String planIdOrName,
		@RequestBody Credentials servicePlanCredsInstance) {
		ServiceDefinition associatedService = null;
		try {			
			associatedService = (ServiceDefinition)serviceDefnService.find(serviceIdOrName);
		} catch(ResourceDoesNotExistException e) { 
			log.error("Unable to find service with id or name: " + serviceIdOrName 
					+ " for inserting credentials for an existing plan with id: " + planIdOrName 
					+ " and new payload: " + servicePlanCredsInstance);
			return serviceNotFound(serviceIdOrName);
		}
		
		try {
			for(Plan plan: associatedService.getPlans()) {
				if (plan.getName().equals(planIdOrName) || plan.getId().equals(planIdOrName)) {
					Credentials credentials = plan.getCredentials();
					if (credentials == null) {
						credentials = servicePlanCredsInstance;
						servicePlanCredsInstance.generateAndSetId();
						credentialsService.add(plan.getId(), servicePlanCredsInstance);

						plan.setCredentials(servicePlanCredsInstance);
						planService.update(plan);
						return new ResponseEntity<>("{}", HttpStatus.OK);
					} else {
						return new ResponseEntity<>("{\"description\": \"Credentials already exist,"
								+ " use PATCH to update!! \"}",
								HttpStatus.BAD_REQUEST);
						
						//servicePlanCredsInstance.setId(credentials.getId());
						//credentialsService.update(plan.getId(), servicePlanCredsInstance);
					}
				}
			}

		} catch(Exception e) { }
		log.error("Unable to find any matching plan with id: " + planIdOrName 
				+ " for insertion of credentials");
		return planNotFound(planIdOrName);
	}
	
	@RequestMapping(value = "/services/{serviceIdOrName}/plans/{planIdOrName}/creds", 
			method = RequestMethod.PATCH)
	public ResponseEntity<Object> updateServicePlanCredsDefn(
		@PathVariable("serviceIdOrName") String serviceIdOrName,
		@PathVariable("planIdOrName") String planIdOrName,
		@RequestBody Credentials servicePlanCredsInstance) {
		
		ServiceDefinition associatedService = null;
		try {			
			associatedService = (ServiceDefinition)serviceDefnService.find(serviceIdOrName);
		} catch(ResourceDoesNotExistException e) { 
			log.error("Unable to find service with id or name: " + serviceIdOrName 
					+ " for update of credentials in existing plan with id: " + planIdOrName 
					+ " and new payload: " + servicePlanCredsInstance);
			return serviceNotFound(serviceIdOrName);
		}
		
		try {
			for(Plan plan: associatedService.getPlans()) {
				if (plan.getName().equals(planIdOrName) || plan.getId().equals(planIdOrName)) {
					Credentials credentials = plan.getCredentials();
					if (credentials == null) {
						return new ResponseEntity<>("{\"description\": \"Credentials does not exist,"
								+ " use PUT to add!! \"}",
								HttpStatus.BAD_REQUEST);
						
					} else {
						servicePlanCredsInstance.setId(credentials.getId());
						credentialsService.update(servicePlanCredsInstance);
					
						return new ResponseEntity<>("{}", HttpStatus.OK);
					}
				}
			}
			
		} catch(Exception e) { }
		log.error("Unable to find any matching plan with id: " + planIdOrName 
				+ " for update of credentials");
		return planNotFound(planIdOrName);
	}

	@RequestMapping(value = "/services/{serviceIdOrName}/plans/{planIdOrName}/creds", 
					method = RequestMethod.DELETE)
	public ResponseEntity<Object> deleteServicePlanCredsDefn(
			@PathVariable("serviceIdOrName") String serviceIdOrName,
			@PathVariable("planIdOrName") String planIdOrName) {
		
		ServiceDefinition associatedService = null;
		try {			
			associatedService = (ServiceDefinition)serviceDefnService.find(serviceIdOrName);
		} catch(ResourceDoesNotExistException e) { 
			log.error("Unable to find service with id or name: " + serviceIdOrName 
					+ " for deletion of credentials in plan with id: " + planIdOrName);
			return serviceNotFound(serviceIdOrName);
		}
		
		try {
			for(Plan plan: associatedService.getPlans()) {
				if (plan.getName().equals(planIdOrName) || plan.getId().equals(planIdOrName)) {
					Credentials credentials = plan.getCredentials();
					if (credentials == null) {
						return new ResponseEntity<>("{\"description\": \"Credentials does not exist!! \"}",
								HttpStatus.BAD_REQUEST);
					} else {
						plan.setCredentials(null);
						planService.update(plan);
						credentialsService.delete(credentials.getId());				
						return new ResponseEntity<>("{}", HttpStatus.OK);
					}
				}
			}
			log.error("Unable to find any matching plan with id: " + planIdOrName 
					+ " for deletion of credentials");
			return planNotFound(planIdOrName);

		} catch (Exception e) {
			e.printStackTrace();
			log.error("Unable to delete credentials for plan with id: " + planIdOrName);
			return new ResponseEntity<>("{\"description\": \"Credentials could not be deleted, possibly in use," 
					+ " error msg: " +  e.getMessage()
					+ ", check logs!! \"}",
					HttpStatus.INTERNAL_SERVER_ERROR);
		} 
	}
	
}
