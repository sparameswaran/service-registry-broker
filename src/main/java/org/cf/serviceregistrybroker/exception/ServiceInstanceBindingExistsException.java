package org.cf.serviceregistrybroker.exception;

import org.cf.serviceregistrybroker.model.ServiceInstanceBinding;

/**
 * Thrown when a duplicate request to bind to a service instance is 
 * received.
 * 
 */
public class ServiceInstanceBindingExistsException extends ResourceExistsException {

	private static final long serialVersionUID = -914571358227517785L;
	
	public ServiceInstanceBindingExistsException(String instanceId) {
		super("ServiceInstanceBinding with the given ID already exists: " 
				+ instanceId);
	}
	
	public ServiceInstanceBindingExistsException(ServiceInstanceBinding binding) {
		super("ServiceInstanceBinding already exists: serviceInstanceBinding.id = "
				+ binding.getId()
				+ ", serviceInstance.id = " + binding.getInstanceId());
	}

}