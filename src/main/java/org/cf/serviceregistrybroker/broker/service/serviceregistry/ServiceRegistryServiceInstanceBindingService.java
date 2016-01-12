package org.cf.serviceregistrybroker.broker.service.serviceregistry;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.cf.serviceregistrybroker.broker.service.ServiceInstanceBindingService;
import org.cf.serviceregistrybroker.broker.service.ServiceInstanceService;
import org.cf.serviceregistrybroker.exception.ServiceBrokerException;
import org.cf.serviceregistrybroker.exception.ServiceInstanceBindingExistsException;
import org.cf.serviceregistrybroker.model.Credentials;
import org.cf.serviceregistrybroker.model.Plan;
import org.cf.serviceregistrybroker.model.ServiceInstance;
import org.cf.serviceregistrybroker.model.ServiceInstanceBinding;
import org.cf.serviceregistrybroker.model.dto.CreateServiceInstanceBindingRequest;
import org.cf.serviceregistrybroker.model.dto.DeleteServiceInstanceBindingRequest;
import org.cf.serviceregistrybroker.repository.PlanRepository;
import org.cf.serviceregistrybroker.repository.ServiceInstanceBindingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ServiceRegistryServiceInstanceBindingService implements ServiceInstanceBindingService {

	private static final Logger log = Logger
			.getLogger(ServiceRegistryServiceInstanceBindingService.class);

	@Autowired
	ServiceInstanceBindingRepository repository;

	@Autowired
	ServiceInstanceService serviceInstanceService;
	
	@Autowired
	PlanRepository planRepository;

	@Override
	public ServiceInstanceBinding createServiceInstanceBinding(
			CreateServiceInstanceBindingRequest request)
			throws ServiceInstanceBindingExistsException,
			ServiceBrokerException {

		log.info("Incoming CreateServiceInstanceBindingRequest: " + request);
		
		String bindingId = request.getBindingId();
		if (bindingId == null) {
			throw new ServiceBrokerException("no bindingId in request.");
		}

		ServiceInstanceBinding sib = repository.findOne(bindingId);
		if (sib != null) {
			throw new ServiceInstanceBindingExistsException(sib);
		}

		String serviceInstanceId = request.getServiceInstanceId();
		ServiceInstance serviceInstance = serviceInstanceService
				.getServiceInstance(serviceInstanceId);

		if (serviceInstance == null) {
			throw new ServiceBrokerException("service instance for binding: "
					+ bindingId + " is missing.");
		}
		
		String planId = serviceInstance.getPlanId();
		Plan underlyingPlan = planRepository.findOne(planId);
		
		Map<String, String> credMap = new HashMap<String, String>();
		Credentials creds = underlyingPlan.getCredentials();
		
		ServiceInstanceBinding binding = new ServiceInstanceBinding(bindingId,
												serviceInstanceId, 
												serviceInstance.getServiceId(), 
												planId, 
												creds.getEntries(), 
												null,
												request.getAppGuid());

		log.info("Saving ServiceInstanceBinding: " + binding);
		return repository.save(binding);
	}

	@Override
	public ServiceInstanceBinding deleteServiceInstanceBinding(
			DeleteServiceInstanceBindingRequest request)
			throws ServiceBrokerException {

		log.info("Incoming DeleteServiceInstanceBindingRequest: " + request);
		
		ServiceInstanceBinding binding = repository.findOne(request
				.getBindingId());

		if (binding == null) {
			throw new ServiceBrokerException("binding with id: "
					+ request.getBindingId() + " does not exist.");
		}

		repository.delete(binding);
		return binding;
	}
}