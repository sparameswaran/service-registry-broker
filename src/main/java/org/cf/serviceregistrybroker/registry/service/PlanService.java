package org.cf.serviceregistrybroker.registry.service;

import org.cf.serviceregistrybroker.exception.ResourceDoesNotExistException;
import org.cf.serviceregistrybroker.exception.ServiceBrokerException;

/**
 * Handles instances of plan definitions associated with a Service Definition.
 *
 */
public interface PlanService<Plan> extends ServiceRegistryBaseService {
	void updateServicePlanDefinitionVisibility(String planId, boolean isVisible) 
			throws ResourceDoesNotExistException, ServiceBrokerException;	
}