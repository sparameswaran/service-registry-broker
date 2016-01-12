package org.cf.serviceregistrybroker.exception;

/**
 * Thrown when a request is received for an unknown ServiceInstance.
 * 
 *
 */
public class ServiceInstanceDoesNotExistException extends ResourceDoesNotExistException {
	
	private static final long serialVersionUID = -62090827040416788L;

	
	public ServiceInstanceDoesNotExistException(String serviceInstanceId) {
		super("ServiceInstance does not exist: id = " + serviceInstanceId);
	}

}