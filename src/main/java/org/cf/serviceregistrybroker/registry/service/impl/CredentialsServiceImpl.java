package org.cf.serviceregistrybroker.registry.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.cf.serviceregistrybroker.exception.CredentialsDoesNotExistException;
import org.cf.serviceregistrybroker.exception.ResourceDoesNotExistException;
import org.cf.serviceregistrybroker.exception.ResourceExistsException;
import org.cf.serviceregistrybroker.exception.ResourceNotDeletableException;
import org.cf.serviceregistrybroker.model.Credentials;
import org.cf.serviceregistrybroker.model.Plan;
import org.cf.serviceregistrybroker.registry.service.CredentialsService;
import org.cf.serviceregistrybroker.registry.service.PlanService;
import org.cf.serviceregistrybroker.repository.CredentialsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CredentialsServiceImpl implements CredentialsService {
	
	private static final Logger log = Logger.getLogger(CredentialsServiceImpl.class);

	@Autowired
	PlanService planService;
	
	@Autowired
	CredentialsRepository credentialsRepository;
	
	public Credentials find(String nameOrId) throws CredentialsDoesNotExistException {
		if (nameOrId == null)
			return null;
		
		return credentialsRepository.findOne(nameOrId);
	}

	@Override
	public Credentials add(String planId, Object item)
			throws ResourceDoesNotExistException, ResourceExistsException {
		Plan associatedPlan = null;
		boolean isPlanNew = false;
		Credentials newCreds = (Credentials)item;
		/*
		if (planId != null) {
			try {
				associatedPlan = (Plan)planService.find(planId);
				Credentials assocCreds = associatedPlan.getCredentials();
				if (assocCreds != null && !assocCreds.equals(newCreds))
					throw new ResourceExistsException("Credentials already exist for Plan: " + planId);
			
			} catch(ResourceDoesNotExistException e) { isPlanNew = true; }
		}
		*/
		newCreds.generateAndSetId();
		credentialsRepository.save(newCreds);
		
		/*
		if ((planId != null) && !isPlanNew) {
			associatedPlan = (Plan)planService.find(planId);
			associatedPlan.setCredentials(newCreds);
			planService.update(associatedPlan);
		}
		*/
		return newCreds;		
	}

	@Override
	public Credentials[] findAll() {
		List<Credentials> target = new ArrayList<Credentials>();
		credentialsRepository.findAll().forEach(target::add);
		return target.toArray( new Credentials[] {});
	}

	@Override
	public Credentials[]  findResourcesByOwner(String ownerId)
			throws ResourceDoesNotExistException {
		Plan associatedPlan = (Plan)planService.find(ownerId);
		List<Credentials> target = new ArrayList<Credentials>();
		target.add(associatedPlan.getCredentials());
		return target.toArray( new Credentials[] {});
	}

	@Override
	public Object update(Object item) throws ResourceDoesNotExistException {
		if (item == null)
			return null;
		
		Credentials updateTo = (Credentials)item;
		Credentials creds = find(updateTo.getId());
		creds.update( (Credentials)item);
		credentialsRepository.save(creds);	
		
		return creds;
	}

	@Override
	public Object delete(String id) throws ResourceDoesNotExistException,
			ResourceNotDeletableException {
		Credentials creds = find(id);
		credentialsRepository.delete(id);
		return creds;
	}

	@Override
	public void add(String parentId, Object[] items)
			throws ResourceExistsException,
			ResourceDoesNotExistException {
		add(parentId, items[0]);
		return;
	}

	@Override
	public Object deleteChild(String ownerId, String childId)
			throws ResourceDoesNotExistException,
			ResourceNotDeletableException {
		return null;
	}

}
