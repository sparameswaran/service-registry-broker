package org.cf.serviceregistrybroker.registry.service;

import java.util.Collection;

import org.cf.serviceregistrybroker.exception.ServiceDefinitionDoesNotExistException;

/**
 * Handles instances of service definitions.
 *
 */
public interface ServiceDefinitionService<ServiceDefinition> extends ServiceRegistryBaseService  {
	
	Collection findServiceDefinitionByProvider(String name)
			throws ServiceDefinitionDoesNotExistException;	
	
	Collection findServiceDefinitionByName(String startname)
			throws ServiceDefinitionDoesNotExistException;	
	
}