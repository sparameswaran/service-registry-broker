package org.cf.serviceregistrybroker.controller.registry;

import org.apache.log4j.Logger;
import org.cf.serviceregistrybroker.controller.broker.BaseController;
import org.cf.serviceregistrybroker.exception.CredentialsDoesNotExistException;
import org.cf.serviceregistrybroker.exception.ResourceDoesNotExistException;
import org.cf.serviceregistrybroker.exception.ResourceNotDeletableException;
import org.cf.serviceregistrybroker.model.Credentials;
import org.cf.serviceregistrybroker.model.Plan;
import org.cf.serviceregistrybroker.registry.service.impl.CredentialsServiceImpl;
import org.cf.serviceregistrybroker.registry.service.impl.PlanServiceImpl;
import org.cf.serviceregistrybroker.repository.CredentialsRepository;
import org.cf.serviceregistrybroker.repository.PlanRepository;
import org.cf.serviceregistrybroker.repository.ServiceDefinitionRepository;
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
public class CredentialsController  extends BaseController {

	private static final Logger log = Logger.getLogger(CredentialsController.class);

	@Autowired
	ServiceDefinitionRepository serviceRepo;
	
	@Autowired
	PlanRepository planRepo;
	
	@Autowired
	CredentialsRepository credentialsRepo;

	@Autowired
	PlanServiceImpl planService;
	
	@Autowired
	CredentialsServiceImpl credsService;
	
	
	private ResponseEntity<Object> planNotFound(String planId) {
		log.error("Error: Plan not found with id: " + planId);
		return new ResponseEntity<Object>(
				"{\"description\": \"No Plan with id: "
						+ planId + " was found\" }",
				HttpStatus.NOT_FOUND);
	}
	
	private ResponseEntity<Object> credentialsNotFound(String credId) {
		log.error("Error: Credentials not found with id: " + credId);
		return new ResponseEntity<Object>(
				"{\"description\": \"Credentials with id: "
						+ credId + " not found\"}",
				HttpStatus.NOT_FOUND);
	}
	
	private ResponseEntity<Object> credentialsDoesNotExist(String planId) {		
		log.error("Error: No Credentials associated for Plan with id: " + planId);
		return new ResponseEntity<Object>(
				"{\"description\": \"No Credentials associated for Plan with id: "
						+ planId + "\"}",
				HttpStatus.NOT_FOUND);
	}

	@RequestMapping(value = "/credentials",
			method = RequestMethod.GET)
	public ResponseEntity<Object> getCredsForPlan(@RequestParam(value="planId") String planId) {
		Plan associatedPlan = null;
		try {
			associatedPlan = planService.find(planId);			
		} catch(Exception e) {
			    return planNotFound(planId);
		}
		
		Credentials creds = associatedPlan.getCredentials();
		if (creds == null) {
			return credentialsDoesNotExist(planId);	
		}
		
		return new ResponseEntity<>(creds, HttpStatus.OK);		
	}
	
	@RequestMapping(value = "/credentials",
			method = RequestMethod.PUT)
	public ResponseEntity<Object> addCredsForPlan(@RequestParam(value="planId") String planId,
			@RequestBody Credentials newCredsInstance) {
		Plan associatedPlan = null;
		try {
			associatedPlan = planService.find(planId);
			Credentials cred = associatedPlan.getCredentials();

			if (cred != null) {
				log.error("Error: Credentials already associated for Plan with id: " + planId);
				return new ResponseEntity<Object>(
						"{\"description\": \"Credentials already exist for plan with id: "
								+ planId + " found. Invoke PATCH instead to update credentials\"}",
						HttpStatus.BAD_REQUEST);	
			}
			
			credsService.add(planId, newCredsInstance);	
			return new ResponseEntity<Object>("{}", HttpStatus.OK);			
		} catch(Exception e) { 		
			return planNotFound(planId);		
		}
	}
	
	@RequestMapping(value = "/credentials",
			method = RequestMethod.PATCH)
	public ResponseEntity<Object> updateCredsForPlan(@RequestParam(value="planId") String planId,
			@RequestBody Credentials updatedCredInstance) {
		
		Plan associatedPlan = null;
		try {
			associatedPlan = planService.find(planId);
			Credentials cred = associatedPlan.getCredentials();
			if (cred == null) {
				return credentialsDoesNotExist(planId);		
			}
			log.info("Updating credentials for plan: " + associatedPlan.getName() 
					+ "with newer credentials attributes: "  + updatedCredInstance);
			
			credsService.update(updatedCredInstance);	
			return new ResponseEntity<Object>("{}", HttpStatus.OK);			
		} catch(Exception e) { 		
			return planNotFound(planId);		
		}
	}
	
	@RequestMapping(value = "/credentials",
			method = RequestMethod.DELETE)
	public ResponseEntity<Object> deleteCredsForPlan(@RequestParam(value="planId") String planId) {
		
		try {			
			Plan associatedPlan = planRepo.findOne(planId);
			if (associatedPlan == null) {
				return planNotFound(planId);		
			}
			
			Credentials cred = associatedPlan.getCredentials();
			if (cred == null)
				return new ResponseEntity<Object>("{}", HttpStatus.GONE);
			
			planService.deleteChild(planId, cred.getId());
			return new ResponseEntity<Object>("{}", HttpStatus.OK);
		} catch (ResourceDoesNotExistException | ResourceNotDeletableException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return new ResponseEntity<Object>(
					"{\"description\": \"Problem deleting credentials for Plan with id: "
							+ planId + "\" }",
					HttpStatus.BAD_REQUEST);	
		}			
	}
	
	@RequestMapping(value = "/credentials/{credId}", method = RequestMethod.GET)
	public ResponseEntity<Object> findCredentials(@PathVariable("credId") String credId) {

		Credentials cred;
		try {
			cred = credsService.find(credId);
			return new ResponseEntity<Object>(cred, HttpStatus.OK);
		} catch (CredentialsDoesNotExistException e) {
				
			return credentialsNotFound(credId);	
		}
	}

	@RequestMapping(value = "/credentials/{credId}", method = RequestMethod.PATCH)
	public ResponseEntity<Object> updateCredentials(@PathVariable("credId") String credId, 
										@RequestBody Credentials updatedCredInstance) {

		log.info("Entering credential update against data: " + updatedCredInstance);
		Credentials cred;
		try {
			credsService.update(updatedCredInstance);
			return new ResponseEntity<Object>("{}", HttpStatus.OK);	
		} catch (ResourceDoesNotExistException e) {
			return credentialsNotFound(credId);		
		}
	}
	
	@RequestMapping(value = "/credentials/{credId}", 
			method = RequestMethod.DELETE)
	public ResponseEntity<Object> deleteCredentials(
		@PathVariable("credId") String credId) {
	
		log.info("Deleting credential with id: " + credId);
		Credentials cred;
		try {
			credsService.delete(credId);
			return new ResponseEntity<Object>("{}", HttpStatus.OK);	
		} catch (ResourceNotDeletableException | ResourceDoesNotExistException e) {
			return credentialsNotFound(credId);			
		} 
	}
	
}
