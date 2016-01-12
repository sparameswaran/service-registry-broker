package org.cf.serviceregistrybroker.exception;

/**
 * Exception denoting an unknown ServiceDefintion
 * 
 * @author sgreenberg@gopivotal.com
 *
 */
public class ServiceDefinitionDoesNotExistException extends ResourceDoesNotExistException {
	
	private static final long serialVersionUID = -62090827040416788L;	
	
	public ServiceDefinitionDoesNotExistException(String serviceDefinitionId) {
		super("ServiceDefinition does not exist with either name or id: " + serviceDefinitionId);
	}
	
}
