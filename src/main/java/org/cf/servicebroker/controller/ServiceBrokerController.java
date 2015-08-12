package org.cf.servicebroker.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.Cloud;
import org.springframework.cloud.app.ApplicationInstanceInfo;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.cf.servicebroker.model.*;
import org.cf.servicebroker.repository.*;

import java.util.*;

import javax.annotation.PostConstruct;

@RestController
public class ServiceBrokerController {

	Log log = LogFactory.getLog(ServiceBrokerController.class);
	
	@Autowired
	Cloud cloud;
	
	@Autowired
	ServiceRepository serviceRepo;
	
	@Autowired
	ServiceInstanceRepository serviceInstanceRepo;
	
	@Autowired
	ServiceBindingRepository serviceBindingRepo;
	
		
	
	@RequestMapping("/v2/catalog")
	public Map<String, Iterable<Service>> catalog() {
		Map<String, Iterable<Service>> wrapper = new HashMap<>();
		wrapper.put("services", serviceRepo.findAll());
		return wrapper;
	}
	
	@RequestMapping(value = "/v2/service_instances/{id}", method=RequestMethod.PUT)
	public ResponseEntity<String> create( 
										@PathVariable("id") 
										String id, 
										@RequestBody 
										ServiceInstance serviceInstance) {
		
		serviceInstance.setId(id);
		
		//log.info("Got /v2/service_instances call with payload : " + serviceInstance);
		
		boolean exists = serviceInstanceRepo.exists(id);
		if (exists) {
			ServiceInstance existing = serviceInstanceRepo.findOne(id);
			if (existing.equals(serviceInstance)) {
				return new ResponseEntity<>("{}", HttpStatus.OK);
			} else {
				return new ResponseEntity<>("{}", HttpStatus.CONFLICT);
			}
		} else {
			serviceInstanceRepo.save(serviceInstance);
			return new ResponseEntity<>("{}", HttpStatus.OK);			
		}
		
	}
	
	@RequestMapping(value = "/v2/service_instances/{instanceId}/service_bindings/{id}", method=RequestMethod.PUT)
	public ResponseEntity<Object> create( 
										@PathVariable("instanceId") 
										String instanceId, 
										@PathVariable("id") 
										String id, 
										@RequestBody 
										ServiceBinding serviceBinding) {
		
		if (!serviceInstanceRepo.exists(instanceId))
			return new ResponseEntity<Object> ("{\"description\": \"Service Instance with id " + instanceId + " not found\"}", 
					HttpStatus.BAD_REQUEST);
		
		//log.info("Got /v2/service_instances/id/... call with payload against service instance: " + instanceId + ", and binding id: " + id + " and body: " + serviceBinding);
		
		serviceBinding.setId(id);
		serviceBinding.setInstanceId(instanceId);
		
		
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
		
	}
	
    @RequestMapping(value = "/v2/service_instances/{instanceId}/service_bindings/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<String> deleteBinding(@PathVariable("instanceId") String instanceId,
                                                @PathVariable("id") String id,
                                                @RequestParam("service_id") String serviceId,
                                                @RequestParam("plan_id") String planId) {
        boolean exists = serviceBindingRepo.exists(id);

        if (exists) {
            serviceBindingRepo.delete(id);
            return new ResponseEntity<>("{}", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("{}", HttpStatus.GONE);
        }
    }

    @RequestMapping(value = "/v2/service_instances/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<String> delete(@PathVariable("id") String id,
                                         @RequestParam("service_id") String serviceId,
                                         @RequestParam("plan_id") String planId) {
        boolean exists = serviceRepo.exists(id);

        if (exists) {
            serviceRepo.delete(id);
            // Actualservice.delete(id);
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
