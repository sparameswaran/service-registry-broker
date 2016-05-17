package org.cf.serviceregistrybroker.registry.service;

import java.util.Set;

import org.cf.serviceregistrybroker.exception.MethodNotSupportedException;
import org.cf.serviceregistrybroker.exception.ResourceDoesNotExistException;
import org.cf.serviceregistrybroker.exception.ResourceExistsException;
import org.cf.serviceregistrybroker.exception.ResourceNotDeletableException;
import org.cf.serviceregistrybroker.exception.ServiceBrokerException;

public interface ServiceRegistryBaseService<T> {

	public abstract T find(String id)
			throws ResourceDoesNotExistException;

	public abstract T[] findAll();

	public abstract T[] findResourcesByOwner(String ownerId)
			throws MethodNotSupportedException, ResourceDoesNotExistException;

	public abstract T add(String parentId, T item)
			throws ResourceExistsException, ResourceDoesNotExistException, ServiceBrokerException;

	public abstract void add(String parentId, T[] items)
			throws ResourceExistsException, ResourceDoesNotExistException, ServiceBrokerException;

	public abstract T update(T item)
			throws ResourceDoesNotExistException, ServiceBrokerException;	
	
	public abstract T delete(String id)
			throws ResourceDoesNotExistException, ResourceNotDeletableException, ServiceBrokerException;
	
	public abstract T deleteChild(String ownerId, String childId)
			throws ResourceDoesNotExistException, ResourceNotDeletableException, ServiceBrokerException;

}