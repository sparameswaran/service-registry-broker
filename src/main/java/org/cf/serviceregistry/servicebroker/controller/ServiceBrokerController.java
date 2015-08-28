package org.cf.serviceregistry.servicebroker.controller;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cf.servicebroker.repository.CredentialsRepository;
import org.cf.servicebroker.repository.PlanRepository;
import org.cf.servicebroker.repository.ServiceBindingRepository;
import org.cf.servicebroker.repository.ServiceInstanceRepository;
import org.cf.servicebroker.repository.ServiceRepository;
import org.cf.serviceregistry.servicebroker.model.Credentials;
import org.cf.serviceregistry.servicebroker.model.Plan;
import org.cf.serviceregistry.servicebroker.model.PlanPk;
import org.cf.serviceregistry.servicebroker.model.Service;
import org.cf.serviceregistry.servicebroker.model.ServiceBinding;
import org.cf.serviceregistry.servicebroker.model.ServiceInstance;
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
public class ServiceBrokerController {

	Log log = LogFactory.getLog(ServiceBrokerController.class);

	@Autowired
	ServiceRepository serviceRepo;

	@Autowired
	ServiceInstanceRepository serviceInstanceRepo;

	@Autowired
	ServiceBindingRepository serviceBindingRepo;
	
	@Autowired
	PlanRepository planRepo;
	
	@Autowired
	CredentialsRepository credentialRepo;

	@RequestMapping("/v2/catalog")
	public Map<String, Iterable<Service>> catalog() {
		Map<String, Iterable<Service>> wrapper = new HashMap<>();
		wrapper.put("services", serviceRepo.findAll());
		return wrapper;
	}

	@RequestMapping(value = "/v2/service_instances/{id}", method = RequestMethod.PUT)
	public ResponseEntity<String> create(@PathVariable("id") String id,
			@RequestBody ServiceInstance serviceInstance) {

		serviceInstance.setId(id);

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

	@RequestMapping(value = "/v2/service_instances/{instanceId}/service_bindings/{id}", method = RequestMethod.PUT)
	public ResponseEntity<Object> create(
			@PathVariable("instanceId") String instanceId,
			@PathVariable("id") String id,
			@RequestBody ServiceBinding serviceBinding) {

		if (!serviceInstanceRepo.exists(instanceId))
			return new ResponseEntity<Object>(
					"{\"description\": \"Service Instance with id "
							+ instanceId + " not found\"}",
					HttpStatus.BAD_REQUEST);

		ServiceInstance serviceInstance = serviceInstanceRepo.findOne(instanceId);
		
		serviceRepo.findOne(serviceInstance.getServiceId());
		
	
		Plan underlyingPlan = planRepo.findOne(serviceInstance.getPlanId());
		
		
		serviceBinding.setId(id);
		serviceBinding.setInstanceId(instanceId);

		boolean exists = serviceBindingRepo.exists(id);
		if (exists) {
			ServiceBinding existing = serviceBindingRepo.findOne(id);
			if (existing.equals(serviceBinding)) {
				return new ResponseEntity<Object>(
						wrapCredentials(existing.getCredentials()),
						HttpStatus.OK);
			} else {
				return new ResponseEntity<Object>("{}", HttpStatus.CONFLICT);
			}
		} else {
			
			Credentials creds = underlyingPlan.getCredentials();
			serviceBinding.setCredentials(creds);
			serviceBindingRepo.save(serviceBinding);
			return new ResponseEntity<Object>(wrapCredentials(creds),
					HttpStatus.OK);
		}

	}

	@RequestMapping(value = "/v2/service_instances/{instanceId}/service_bindings/{id}", method = RequestMethod.DELETE)
	public ResponseEntity<String> deleteBinding(
			@PathVariable("instanceId") String instanceId,
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

	private Map<String, Object> wrapCredentials(Credentials credentials) {
		Map<String, Object> wrapper = new HashMap<>();
		wrapper.put("credentials", credentials);
		return wrapper;
	}
}
