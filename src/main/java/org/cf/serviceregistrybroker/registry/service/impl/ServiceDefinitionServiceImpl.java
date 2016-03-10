package org.cf.serviceregistrybroker.registry.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.apache.log4j.Logger;
import org.cf.serviceregistrybroker.cfutils.CFClientManager;
import org.cf.serviceregistrybroker.cfutils.CFServiceBrokerDelegator;
import org.cf.serviceregistrybroker.exception.MethodNotSupportedException;
import org.cf.serviceregistrybroker.exception.ResourceDoesNotExistException;
import org.cf.serviceregistrybroker.exception.ResourceExistsException;
import org.cf.serviceregistrybroker.exception.ResourceNotDeletableException;
import org.cf.serviceregistrybroker.exception.ServiceDefinitionDoesNotExistException;
import org.cf.serviceregistrybroker.exception.ServiceDefinitionExistsException;
import org.cf.serviceregistrybroker.model.Credentials;
import org.cf.serviceregistrybroker.model.Plan;
import org.cf.serviceregistrybroker.model.ServiceDefinition;
import org.cf.serviceregistrybroker.registry.service.CredentialsService;
import org.cf.serviceregistrybroker.registry.service.PlanService;
import org.cf.serviceregistrybroker.registry.service.ServiceDefinitionService;
import org.cf.serviceregistrybroker.repository.PlanRepository;
import org.cf.serviceregistrybroker.repository.ServiceDefinitionRepository;
import org.cloudfoundry.client.CloudFoundryClient;
import org.cloudfoundry.client.v2.servicebrokers.ServiceBrokerResource;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class ServiceDefinitionServiceImpl implements
		ServiceDefinitionService {

	private static final Logger log = Logger.getLogger(ServiceDefinitionServiceImpl.class);

	@Autowired
	PlanRepository planRepository;
	
	@Autowired
	ServiceDefinitionRepository serviceRepository;
		
	@Autowired
	PlanService planService;

	@Autowired
	CredentialsService credentialsService;
	
	@Autowired
	CloudFoundryClient cfClient;
	
	@Autowired
	CFServiceBrokerDelegator serviceBrokerDelegator;
	
	private ServiceDefinition findByNameOrId(String nameOrId) throws ServiceDefinitionDoesNotExistException {
		if (nameOrId == null)
			return null;
		
		Optional<ServiceDefinition> services = serviceRepository.findByServiceIdOrName(nameOrId);
		if (!services.isPresent())
			throw new ServiceDefinitionDoesNotExistException(nameOrId);
		
		return services.get();
	}
	
	private ServiceDefinition findOne(String id) throws ServiceDefinitionDoesNotExistException {
		if (id == null)
			return null;
		
		return serviceRepository.findOne(id);
	}
	
	@Override
	public Object find(String id) throws ResourceDoesNotExistException {
		return findByNameOrId(id);
	}

	@Override
	public ServiceDefinition[] findAll() {
		
		List<ServiceDefinition> target = new ArrayList<ServiceDefinition>();
		serviceRepository.findAll().forEach(target::add);
		return target.toArray( new ServiceDefinition[] {});
	}

	@Override
	public Collection findServiceDefinitionByProvider(String providerId) {
		List<String> serviceIds = serviceRepository.findServiceContainingProviderName(providerId);
		return serviceIds;
	}
	
	@Override
	public Collection findServiceDefinitionByName(String startname) {
		List<String> serviceIds = serviceRepository.findServiceContainingName(startname);
		return serviceIds;
	}

	@Override
	public Object add(String parentId, Object newResource) throws ResourceExistsException {
		ServiceDefinition newService = (ServiceDefinition)newResource;
		try {
			ServiceDefinition existingService = findByNameOrId(newService.getName());
			if (existingService != null)
				throw new ServiceDefinitionExistsException(newService.getName());
		} catch(ResourceDoesNotExistException e) { }
		
		newService.generateAndSetId();
		Set<Plan> plans = newService.getPlans();		
		if (plans != null) {			
			for (Plan newPlan: plans) {				
				newPlan.generateAndSetId();					
				newPlan.setService(newService);
				log.debug("Associated Plan: " + newPlan);
				
				// Save the Credentials ahead of the Service or Plan
				Credentials newCreds = newPlan.getCredentials();
				if (newCreds == null)
					continue;
				
				try {
					credentialsService.add(null, newCreds);
				} catch(Exception e) { }
				log.debug("After setting service Id, New Plan Id: " + newPlan.getId() 
				 		+ " and associated service is: " + newPlan.getService());				
			}
			serviceRepository.save(newService);			
		}
		
		serviceBrokerDelegator.updateServiceBroker(cfClient);
		for(Plan newPlan: newService.getPlans()) {
			if (newPlan.isVisible())
				serviceBrokerDelegator.updatePlanVisibilityOfServiceBroker(cfClient, newPlan.getService().getName(), 
												newPlan.getName(), true);
		}

		log.debug("Service Definition created: " + newService);		
		return newService;
	}

	@Override
	public Object update(Object item) throws ResourceDoesNotExistException {
		ServiceDefinition updateTo = (ServiceDefinition)item;
		ServiceDefinition existingService = findOne(updateTo.getId());
		existingService.update(updateTo);
		serviceRepository.save(existingService);

		serviceBrokerDelegator.updateServiceBroker(cfClient);
		return existingService;
	}

	@Override
	public Object delete(String id) throws ResourceDoesNotExistException,
			ResourceNotDeletableException {
		ServiceDefinition existingService = findOne(id);;
		
		// Clean up associated plans
		for(Plan plan: existingService.getPlans()) {
			planService.delete(plan.getId());
		}
		serviceRepository.delete(id);
		serviceBrokerDelegator.updateServiceBroker(cfClient);
		return existingService;
	}

	@Override
	public ServiceDefinition[] findResourcesByOwner(String ownerId)
			throws MethodNotSupportedException, ResourceDoesNotExistException {
		// TODO Auto-generated method stub
		throw new MethodNotSupportedException("No owner for Service Definition!!");
	}

	@Override
	public void add(String parentId, Object[] items)
			throws ResourceExistsException,
			ResourceDoesNotExistException {
		for(Object newService: items) {
			add(null, newService);
		}
		serviceBrokerDelegator.updateServiceBroker(cfClient);
	}

	@Override
	public Object deleteChild(String ownerId, String childId)
			throws ResourceDoesNotExistException,
			ResourceNotDeletableException {
		ServiceDefinition existingService = findOne(ownerId);;
		
		// Clean up associated plans
		for(Plan plan: existingService.getPlans()) {
			if (plan.getId().equals(childId) || plan.getName().equals(childId)) {
				existingService.removePlan(plan);
				planService.delete(plan.getId());
				serviceRepository.save(existingService);
				return existingService;
			}
		}
		serviceBrokerDelegator.updateServiceBroker(cfClient);
		return existingService;
	}

	public void updateServiceDefinitionVisibility(String serviceId, boolean isVisible) 
			throws ServiceDefinitionDoesNotExistException {
		ServiceDefinition serviceDefinition = null;
		
		serviceDefinition = findOne(serviceId);
		for(Plan plan: serviceDefinition.getPlans()) {
			plan.setVisible(isVisible);			
		}
		serviceRepository.save(serviceDefinition);
		
		log.info("Updating service visibility...");
		serviceBrokerDelegator.updateServiceVisibilityOfServiceBroker(cfClient, serviceDefinition.getName(), isVisible);
		
		
	}

}
