package org.cf.serviceregistrybroker.registry.service;

import org.cf.serviceregistrybroker.exception.PlanDoesNotExistException;
import org.cf.serviceregistrybroker.model.Credentials;

/**
 * Handles instance of Credentials associated with a Plan.
 *
 */
public interface CredentialsService<Credentials> extends ServiceRegistryBaseService {

	
	// No deletion of credentials allowed
}