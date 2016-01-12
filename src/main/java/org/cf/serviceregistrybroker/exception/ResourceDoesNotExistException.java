package org.cf.serviceregistrybroker.exception;

public class ResourceDoesNotExistException extends Exception {

	private static final long serialVersionUID = -62090827040416788L;

	public ResourceDoesNotExistException(String id) {
		super("Resource does not exist with either name or id: " + id);
	}
}
