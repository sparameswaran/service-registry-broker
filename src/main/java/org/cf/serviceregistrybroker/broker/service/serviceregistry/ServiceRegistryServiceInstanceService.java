package org.cf.serviceregistrybroker.broker.service.serviceregistry;

import org.apache.log4j.Logger;
import org.cf.serviceregistrybroker.broker.service.CatalogService;
import org.cf.serviceregistrybroker.broker.service.ServiceInstanceService;
import org.cf.serviceregistrybroker.exception.ServiceBrokerException;
import org.cf.serviceregistrybroker.exception.ServiceInstanceDoesNotExistException;
import org.cf.serviceregistrybroker.exception.ServiceInstanceExistsException;
import org.cf.serviceregistrybroker.model.ServiceDefinition;
import org.cf.serviceregistrybroker.model.ServiceInstance;
import org.cf.serviceregistrybroker.model.dto.CreateServiceInstanceRequest;
import org.cf.serviceregistrybroker.model.dto.DeleteServiceInstanceRequest;
import org.cf.serviceregistrybroker.repository.PlanRepository;
import org.cf.serviceregistrybroker.repository.ServiceInstanceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ServiceRegistryServiceInstanceService implements ServiceInstanceService {

	private static final Logger log = Logger
			.getLogger(ServiceRegistryServiceInstanceService.class);


	@Autowired
	CatalogService catalogService;

	@Autowired
	ServiceInstanceRepository serviceInstanceRepository;

	@Autowired
	PlanRepository planRepository;

	@Override
	public ServiceInstance getServiceInstance(String id) {
		if (id == null)
			return null;
				
		ServiceInstance instance = getInstance(id);
		if (instance == null) {
			log.warn("Service instance with id: " + id + " not found!");
			return null;
		}
		
		return instance;
	}

	@Override
	public ServiceInstance createServiceInstance(
			CreateServiceInstanceRequest request)
			throws ServiceInstanceExistsException, ServiceBrokerException {

		if (request == null || request.getServiceDefinitionId() == null) {
			throw new ServiceBrokerException(
					"invalid CreateServiceInstanceRequest object.");
		}

		if (request.getServiceInstanceId() != null
				&& getInstance(request.getServiceInstanceId()) != null) {
			throw new ServiceInstanceExistsException(serviceInstanceRepository.findOne(request
					.getServiceInstanceId()));
		}

		ServiceDefinition sd = catalogService.getServiceDefinition(request
				.getServiceDefinitionId());

		if (sd == null) {
			throw new ServiceBrokerException(
					"Unable to find service definition with id: "
							+ request.getServiceDefinitionId());
		}

		ServiceInstance serviceInstance = request.createServiceInstance();
		saveInstance(serviceInstance);
		log.info("creating service instance: " + request.getServiceInstanceId()
				+ " service definition: " + request.getServiceDefinitionId());


		return serviceInstance;
	}

	@Override
	public ServiceInstance deleteServiceInstance(
			DeleteServiceInstanceRequest request) throws ServiceBrokerException {

		if (request == null || request.getServiceInstanceId() == null) {
			throw new ServiceBrokerException(
					"invalid DeleteServiceInstanceRequest object.");
		}

		ServiceInstance serviceInstance = getInstance(request.getServiceInstanceId());
		if (serviceInstance == null) {
			throw new ServiceBrokerException("Service instance: "
					+ request.getServiceInstanceId() + " not found.");
		}
		serviceInstance = deleteInstance(serviceInstance);

		log.info("Unregistered service instance: "
				+ serviceInstance);

		return serviceInstance;
	}

	private ServiceInstance getInstance(String id) {
		if (id == null) {
			return null;
		}
		ServiceInstance instance = serviceInstanceRepository.findOne(id);
		return instance;
	}

	private ServiceInstance deleteInstance(ServiceInstance instance) {
		if (instance == null || instance.getId() == null) {
			return null;
		}
		serviceInstanceRepository.delete(instance.getId());
		log.info("Done deletion of service instance: "
				+ instance);
		return instance;
	}

	private ServiceInstance saveInstance(ServiceInstance instance) {
		return serviceInstanceRepository.save(instance);
	}	
	
}