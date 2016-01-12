package org.cf.serviceregistrybroker.exception;


/**
 * Thrown when a duplicate plan creation request is
 * received.
 * 
 */
public class ResourceExistsException extends Exception {

	private static final long serialVersionUID = -914571358227517785L;
	
	public ResourceExistsException(String id) {
		super("Resource with the given ID already exists: " +
				id);
	}

}