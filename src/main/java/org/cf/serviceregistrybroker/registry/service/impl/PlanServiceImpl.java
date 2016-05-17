package org.cf.serviceregistrybroker.registry.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.log4j.Logger;
import org.cf.serviceregistrybroker.cfutils.CFServiceBrokerDelegator;
import org.cf.serviceregistrybroker.exception.PlanDoesNotExistException;
import org.cf.serviceregistrybroker.exception.ResourceDoesNotExistException;
import org.cf.serviceregistrybroker.exception.ResourceExistsException;
import org.cf.serviceregistrybroker.exception.ResourceNotDeletableException;
import org.cf.serviceregistrybroker.exception.ServiceBrokerException;
import org.cf.serviceregistrybroker.exception.ServiceDefinitionDoesNotExistException;
import org.cf.serviceregistrybroker.model.Credentials;
import org.cf.serviceregistrybroker.model.Plan;
import org.cf.serviceregistrybroker.model.ServiceDefinition;
import org.cf.serviceregistrybroker.registry.service.CredentialsService;
import org.cf.serviceregistrybroker.registry.service.PlanService;
import org.cf.serviceregistrybroker.repository.PlanRepository;
import org.cf.serviceregistrybroker.repository.ServiceDefinitionRepository;
import org.cloudfoundry.client.CloudFoundryClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PlanServiceImpl implements PlanService {

	private static final Logger log = Logger.getLogger(PlanServiceImpl.class);

	@Autowired
	ServiceDefinitionRepository serviceRepository;
	
	@Autowired
	PlanRepository planRepository;
	
	@Autowired
	CredentialsService credsService;
	
	@Autowired
	CloudFoundryClient cfClient;	
	
	@Autowired
	CFServiceBrokerDelegator serviceBrokerDelegator;
	
	private Plan findByNameOrId(String nameOrId) throws PlanDoesNotExistException {
		if (nameOrId == null)
			throw new PlanDoesNotExistException(null);;
		
		Optional<Plan> plans = planRepository.findByPlanIdOrName(nameOrId);
		if (!plans.isPresent())
			throw new PlanDoesNotExistException(nameOrId);
		
		return plans.get();
	}
	
	private Plan findOne(String id) throws PlanDoesNotExistException {
		if (id == null)
			throw new PlanDoesNotExistException(null);;
		
		return planRepository.findOne(id);
	}
	
	private ServiceDefinition findService(String nameOrId) throws ServiceDefinitionDoesNotExistException {
		Optional<ServiceDefinition> services = serviceRepository.findByServiceIdOrName(nameOrId);
		if (!services.isPresent())
			throw new ServiceDefinitionDoesNotExistException(nameOrId);
		
		return services.get();
	}

	@Override
	public Plan find(String planId) throws ResourceDoesNotExistException {
		return findByNameOrId(planId);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Plan[] findAll() {
		
		List<Plan> target = new ArrayList<Plan>();
		planRepository.findAll().forEach(target::add);
		return target.toArray( new Plan[] {});
	}

	@Override
	public Plan[] findResourcesByOwner(String ownerId)
			throws ResourceDoesNotExistException {
		ServiceDefinition service = findService(ownerId);
		return service.getPlans().toArray( new Plan[] {});
	}

	@Override
	public Object add(String ownerId, Object plan)
			throws ResourceExistsException, ResourceDoesNotExistException, ServiceBrokerException {
		Plan newPlan = (Plan)plan;
		ServiceDefinition parentService = findService(ownerId);
		
		if (parentService == null)
			parentService = newPlan.getService();

		for(Plan existingPlan: parentService.getPlans()) {
			if (existingPlan.getName().equals(newPlan.getName()))
				throw new ResourceExistsException(newPlan.getName());
		}
	
		newPlan.generateAndSetId();
		parentService.addPlan(newPlan);
		
		Credentials creds = newPlan.getCredentials();
		if (creds != null) {			
			credsService.add(newPlan.getId(), creds);
		}

		serviceRepository.save(parentService);		
		serviceBrokerDelegator.updateServiceBroker(cfClient);
		
		if (newPlan.isVisible()) {
			serviceBrokerDelegator.updatePlanVisibilityOfServiceBroker(cfClient, newPlan.getService().getName(), newPlan.getName(), true);
		}
		return newPlan;
	}

	@Override
	public Object update(Object item) throws ResourceDoesNotExistException, ServiceBrokerException {
		// TODO Auto-generated method stub
		Plan updateTo = (Plan)item;
		String planId = updateTo.getId();
		Plan plan = findOne(planId);
		plan.update(updateTo);

		planRepository.save(plan);
		serviceBrokerDelegator.updateServiceBroker(cfClient);
		return plan;
	}

	@Override
	public Plan delete(String planId) throws ResourceDoesNotExistException,
			ResourceNotDeletableException {
		try {
			Plan plan = findOne(planId);
			credsService.delete(plan.getCredentials().getId());
			planRepository.delete(planId);
			serviceBrokerDelegator.updateServiceBroker(cfClient);
			return plan;
		} catch(Exception e) { 
			return null; 
		}
	}

	@Override
	public void add(String ownerId, Object[] items)
			throws ResourceExistsException,
			ResourceDoesNotExistException, ServiceBrokerException {
		Plan[] newPlans = (Plan[])items;
		ServiceDefinition parentService = findService(ownerId);
		
		for(Plan newPlan: newPlans) {
			if (parentService == null)
				parentService = newPlan.getService();
	
			for(Plan existingPlan: parentService.getPlans()) {
				if (existingPlan.getName().equals(newPlan.getName()))
					throw new ResourceExistsException(newPlan.getName());
			}
			this.add(ownerId, newPlan);
		}
		serviceBrokerDelegator.updateServiceBroker(cfClient);
		for(Plan newPlan: newPlans) {
			if (newPlan.isVisible()) {
				serviceBrokerDelegator.updatePlanVisibilityOfServiceBroker(cfClient, 
											newPlan.getService().getName(), newPlan.getName(), true);
			}
		}
		return;
	}

	@Override
	public Object deleteChild(String planId, String childId)
			throws ResourceDoesNotExistException,
			ResourceNotDeletableException, ServiceBrokerException {
		
		Plan plan = findOne(planId);
		Credentials creds = plan.getCredentials();
		if (creds != null && childId.equals(plan.getCredentials().getId())) {
			plan.setCredentials(null);
			credsService.delete(childId);
			planRepository.save(plan);
		}
		serviceBrokerDelegator.updateServiceBroker(cfClient);
		return plan;
	}
	
	public void updateServicePlanDefinitionVisibility(String planId, boolean isVisible) 
			throws ResourceDoesNotExistException, ServiceBrokerException {
		Plan plan = findOne(planId);		
		plan.setVisible(isVisible);
		planRepository.save(plan);
		serviceBrokerDelegator.updatePlanVisibilityOfServiceBroker(cfClient, plan.getService().getName(), plan.getName(), isVisible);
	}

}
