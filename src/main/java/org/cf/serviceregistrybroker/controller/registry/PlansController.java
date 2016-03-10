package org.cf.serviceregistrybroker.controller.registry;

import java.util.Map;

import org.apache.log4j.Logger;
import org.cf.serviceregistrybroker.controller.broker.BaseController;
import org.cf.serviceregistrybroker.exception.ResourceDoesNotExistException;
import org.cf.serviceregistrybroker.exception.ResourceNotDeletableException;
import org.cf.serviceregistrybroker.model.Plan;
import org.cf.serviceregistrybroker.registry.service.PlanService;
import org.cf.serviceregistrybroker.registry.service.serviceregistry.ServiceRegistryServiceDefinitionService;
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
public class PlansController extends BaseController {

	private static final Logger log = Logger.getLogger(PlansController.class);

	@Autowired
	ServiceRegistryServiceDefinitionService serviceDefnService;
	
	@Autowired
	PlanService planService;

	private ResponseEntity<Object> planNotFound(String planId) {
		log.error("Error: Plan not found with id: " + planId);
		return new ResponseEntity<Object>(
				"{\"description\": \"No Plan with id: "
						+ planId + " was found\" }",
				HttpStatus.NOT_FOUND);
	}
	
	@RequestMapping(value = "/plans", method = RequestMethod.GET)
	public ResponseEntity<Object> findAllPlans() {
		return new ResponseEntity<>(planService.findAll(), HttpStatus.OK);	
	}

	
	@RequestMapping(value = "/plans/{planId}", method = RequestMethod.GET)
	public ResponseEntity<Object> findPlan(@PathVariable("planId") String planId) {

		Plan associatedPlan = null;
		try {
			associatedPlan = (Plan)planService.find(planId);
			return new ResponseEntity<>(associatedPlan, HttpStatus.OK);
		} catch(Exception e) { 
			return planNotFound(planId);
		}
	}

	// Not allowing direct put for plan, better to go via ServiceController
	@RequestMapping(value = "/plans/{planId}", method = RequestMethod.PATCH)
	public ResponseEntity<Object> updatePlan(@PathVariable("planId") String planId, 
										@RequestBody Plan updatedPlanInstance) {

		Plan associatedPlan = null;
		try {
			updatedPlanInstance.setId(planId);
			associatedPlan = (Plan)planService.update(updatedPlanInstance);
			return new ResponseEntity<>(associatedPlan, HttpStatus.OK);
		} catch(Exception e) { 
			return planNotFound(planId);
		}
	}
	
	@RequestMapping(value = "/plansVisibility/{planId}", method = RequestMethod.PATCH)
	public ResponseEntity<Object> updatePlanVisibility(@PathVariable("planId") String planId, 
			@RequestBody Map<String, Boolean> planVisibilityMap ) {

		Plan associatedPlan = null;
		try {
			planService.updateServicePlanDefinitionVisibility(planId, planVisibilityMap.get("visible"));
			associatedPlan = (Plan)planService.find(planId);
			return new ResponseEntity<>(associatedPlan, HttpStatus.OK);
		} catch(ResourceDoesNotExistException e) { 
			return planNotFound(planId);
		}
	}

	@RequestMapping(value = "/plans/{planId}", 
			method = RequestMethod.DELETE)
	public ResponseEntity<String> deletePlanDefn(
		@PathVariable("planId") String planId) {
	
		Plan associatedPlan = null;
		try {
			associatedPlan = (Plan)planService.find(planId);
			serviceDefnService.deleteChild(associatedPlan.getService().getId(), planId);
		} catch(ResourceNotDeletableException | ResourceDoesNotExistException e) { 
			log.error("Error: Plan not found or already deleted with id: " + planId);
			return new ResponseEntity<>("{Plan not found or already deleted }", HttpStatus.GONE);
		} 
		
		try {
			// Check if the plan is still sitting around
			associatedPlan = (Plan)planService.find(planId);
			if (associatedPlan != null) {
				log.error("Error: Plan " + planId + " cannot be deleted, probably in use by applications,"
						+ " unbind and delete service instances using the plan before deleting");
				return new ResponseEntity<>("{Error: Plan " + planId + " cannot be deleted, probably in use by applications,"
						+ " unbind and delete service instances using the plan before deleting}", HttpStatus.PRECONDITION_FAILED);			
			}
		
		} catch(Exception e) { }
		
		return new ResponseEntity<>("{}", HttpStatus.OK);		
	}
}
