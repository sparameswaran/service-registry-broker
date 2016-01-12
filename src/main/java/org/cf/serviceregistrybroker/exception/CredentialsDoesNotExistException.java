package org.cf.serviceregistrybroker.exception;

/**
 * Exception denoting an unknown Plan
 * 
 * @author sgreenberg@gopivotal.com
 *
 */
public class CredentialsDoesNotExistException extends ResourceDoesNotExistException {
	
	private static final long serialVersionUID = -62090827040416788L;

	public CredentialsDoesNotExistException(String id) {
		super("Credentials does not exist with id: " + id);
	}
	
}
