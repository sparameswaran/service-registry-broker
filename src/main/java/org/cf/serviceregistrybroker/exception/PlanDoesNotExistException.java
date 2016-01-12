package org.cf.serviceregistrybroker.exception;

/**
 * Exception denoting an unknown Plan
 * 
 * @author sgreenberg@gopivotal.com
 *
 */
public class PlanDoesNotExistException extends ResourceDoesNotExistException {
	
	private static final long serialVersionUID = -62090827040416788L;

	public PlanDoesNotExistException(String id) {
		super("Plan does not exist with either name or id: " + id);
	}
	
}
