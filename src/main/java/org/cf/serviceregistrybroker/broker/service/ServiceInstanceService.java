package org.cf.serviceregistrybroker.broker.service;

import org.cf.serviceregistrybroker.exception.ServiceBrokerException;
import org.cf.serviceregistrybroker.exception.ServiceInstanceDoesNotExistException;
import org.cf.serviceregistrybroker.exception.ServiceInstanceExistsException;
import org.cf.serviceregistrybroker.exception.ServiceInstanceUpdateNotSupportedException;
import org.cf.serviceregistrybroker.model.ServiceInstance;
import org.cf.serviceregistrybroker.model.dto.CreateServiceInstanceRequest;
import org.cf.serviceregistrybroker.model.dto.DeleteServiceInstanceRequest;


/**
 * Handles instances of service definitions.
 * 
 * @author sgreenberg@gopivotal.com
 */
public interface ServiceInstanceService {

	
	/**
	 * @param serviceInstanceId The id of the serviceInstance
	 * @return The ServiceInstance with the given id or null if one does not exist
	 */
	ServiceInstance getServiceInstance(String serviceInstanceId);

	/**
	 * Create a new instance of a service
	 * @param createServiceInstanceRequest containing the parameters from CloudController
	 * @return The newly created ServiceInstance
	 * @throws ServiceInstanceExistsException if the service instance already exists.
	 * @throws ServiceBrokerException if something goes wrong internally
	 */
	ServiceInstance createServiceInstance(CreateServiceInstanceRequest createServiceInstanceRequest) 
			throws ServiceInstanceExistsException, ServiceBrokerException;
	
	/**
	 * Delete and return the instance if it exists.
	 * @param deleteServiceInstanceRequest containing pertinent information for deleting the service.
	 * @return The deleted ServiceInstance or null if one did not exist.
	 * @throws ServiceBrokerException is something goes wrong internally
	 * 
	 */
	ServiceInstance deleteServiceInstance(DeleteServiceInstanceRequest deleteServiceInstanceRequest) 
			throws ServiceBrokerException;

}