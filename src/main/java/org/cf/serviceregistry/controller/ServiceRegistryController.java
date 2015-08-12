package org.cf.serviceregistry.controller;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cf.servicebroker.model.Credentials;
import org.cf.servicebroker.model.Plan;
import org.cf.servicebroker.model.Service;
import org.cf.servicebroker.repository.ServiceRepository;
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
	
	@RequestMapping("/serviceDefns")
	public Map<String, Iterable<Service>> services() {
		Map<String, Iterable<Service>> wrapper = new HashMap<>();
		wrapper.put("services", serviceRepo.findAll());
		return wrapper;
	}
	
	@RequestMapping(value = "/serviceDefns/{id}", method=RequestMethod.PUT)
	public ResponseEntity<String> create( 
										@PathVariable("id") 
										String id, 
										@RequestBody 
										Service serviceEndpointInstance) {
		
		serviceEndpointInstance.setId(id);
		
		log.info("Got /serviceDefns call with payload : " + serviceEndpointInstance);
		
		boolean exists = serviceRepo.exists(id);
		if (exists) {
			Service existing = serviceRepo.findOne(id);
			if (existing.equals(serviceEndpointInstance)) {
				return new ResponseEntity<>("{}", HttpStatus.OK);
			} else {
				return new ResponseEntity<>("{}", HttpStatus.CONFLICT);
			}
			
			/*
			boolean exists = serviceBindingRepo.exists(id);
			if (exists) {
				ServiceBinding existing = serviceBindingRepo.findOne(id);
				if (existing.equals(serviceBinding)) {
					return new ResponseEntity<Object>(wrapCredentials(existing.getCredentials()), HttpStatus.OK);
				} else {
					return new ResponseEntity<Object>("{}", HttpStatus.CONFLICT);
				}
			} else {
				Credentials creds = new Credentials();
				creds.setId(UUID.randomUUID().toString());
				creds.setUri("http://" + myUri() + "/Sample/" + instanceId);
				creds.setUsername("testuser");
				creds.setPassword("testpaswd");
				serviceBinding.setCredentials(creds);
				serviceBindingRepo.save(serviceBinding);
				return new ResponseEntity<Object>(wrapCredentials(creds), HttpStatus.OK);			
			}
			*/
		} else {
			serviceRepo.save(serviceEndpointInstance);
			return new ResponseEntity<>("{}", HttpStatus.OK);			
		}
		
	}
	
	
   @RequestMapping(value = "/serviceDefns/{service_id}", method = RequestMethod.DELETE)
    public ResponseEntity<String> deleteServiceDefn(@RequestParam("service_id") String serviceId) {
        boolean exists = serviceRepo.exists(serviceId);

        if (exists) {
            serviceRepo.delete(serviceId);
            return new ResponseEntity<>("{}", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("{}", HttpStatus.GONE);
        }
    }

	   
    @RequestMapping(value = "/serviceDefns/{service_id}/{plan_id}", method = RequestMethod.DELETE)
    public ResponseEntity<String> deleteServicePlanDefn(@RequestParam("service_id") String serviceId,
                                                @RequestParam("plan_id") String planId) {
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
    
 
	
	private String myUri() {
//		ApplicationInstanceInfo appInstanceInfo  = cloud.getApplicationInstanceInfo();
//		List<Object> uris = (List<Object>) appInstanceInfo.getProperties().get("uri");
//		return uris.get(0).toString();
		return "TestServiceBroker";
	}
	
	private Map<String, Object> wrapCredentials(Credentials credentials) {
        Map<String, Object> wrapper = new HashMap<>();
        wrapper.put("credentials", credentials);
        return wrapper;
    }
}
