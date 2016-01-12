package org.cf.serviceregistrybroker.exception;

public class ResourceNotDeletableException extends Exception {

	private static final long serialVersionUID = -62090827040416788L;

	public ResourceNotDeletableException() {
		super("Resource cannot be deleted!!");
	}

	public ResourceNotDeletableException(String string) {
		super(string);
	}
}
