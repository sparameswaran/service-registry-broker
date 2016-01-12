package org.cf.serviceregistrybroker.exception;

import org.cf.serviceregistrybroker.model.Plan;

/**
 * Thrown when a duplicate plan creation request is
 * received.
 * 
 */
public class PlanExistsException extends ResourceExistsException {

	private static final long serialVersionUID = -914571358227517785L;
	
	public PlanExistsException(String instanceId) {
		super("Plan with the given ID already exists: " +
				instanceId);
	}
	public PlanExistsException(Plan instance) {
		super("Plan with the given ID already exists: " +
				"Plan.id = " + instance.getId() +
				", Service.id = " + instance.getService().getId());
	}

}