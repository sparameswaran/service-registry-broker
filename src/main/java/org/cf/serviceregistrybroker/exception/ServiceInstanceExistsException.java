package org.cf.serviceregistrybroker.exception;

import org.cf.serviceregistrybroker.model.ServiceInstance;

/**
 * Thrown when a duplicate service instance creation request is
 * received.
 * 
 */
public class ServiceInstanceExistsException extends ResourceExistsException {

	private static final long serialVersionUID = -914571358227517785L;
	
	public ServiceInstanceExistsException(String instanceId) {
		super("ServiceInstance with the given ID already exists: " +
				instanceId);
	}
	
	public ServiceInstanceExistsException(ServiceInstance instance) {
		super("ServiceInstance with the given ID already exists: " +
				"ServiceInstance.id = " + instance.getId() +
				", Service.id = " + instance.getServiceId());
	}

}