package org.cf.serviceregistrybroker.exception;

import org.cf.serviceregistrybroker.model.ServiceDefinition;

/**
 * Thrown when a duplicate service definition creation request is
 * received.
 * 
 */
public class ServiceDefinitionExistsException extends ResourceExistsException {

	private static final long serialVersionUID = -914571358227517785L;
	
	public ServiceDefinitionExistsException(String instanceId) {
		super("ServiceDefinition with the given ID already exists: " +
				instanceId);
	}
	
	public ServiceDefinitionExistsException(ServiceDefinition instance) {
		super("ServiceDefinition with the given ID already exists, id: " 
				+ instance.getId());
	}

}