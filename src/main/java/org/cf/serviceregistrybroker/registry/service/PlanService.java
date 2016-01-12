package org.cf.serviceregistrybroker.registry.service;

import java.util.Set;

import org.cf.serviceregistrybroker.exception.PlanDoesNotExistException;
import org.cf.serviceregistrybroker.exception.PlanExistsException;
import org.cf.serviceregistrybroker.exception.ServiceDefinitionDoesNotExistException;
import org.cf.serviceregistrybroker.model.Plan;

/**
 * Handles instances of plan definitions associated with a Service Definition.
 *
 */
public interface PlanService<Plan> extends ServiceRegistryBaseService {
	
}