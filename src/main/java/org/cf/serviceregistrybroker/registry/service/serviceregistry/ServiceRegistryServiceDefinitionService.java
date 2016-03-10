package org.cf.serviceregistrybroker.registry.service.serviceregistry;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.apache.log4j.Logger;
import org.cf.serviceregistrybroker.cfutils.CFAppManager;
import org.cf.serviceregistrybroker.cfutils.ServiceBrokerAppResource;
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
public class ServiceRegistryServiceDefinitionService implements
		ServiceDefinitionService {

	private static final Logger log = Logger.getLogger(ServiceRegistryServiceDefinitionService.class);
	private static final String VCAP_APPLICATION_URI = "application_uris";
	
	@Value("${VCAP_APPLICATION}")
    private static String vcapAppEnv;
	
	private static String[] cfAppUris;
	
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
	ServiceBrokerAppResource serviceBrokerResource;
	
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
		
		serviceBrokerResource.updateServiceBroker(cfClient);
		for(Plan newPlan: newService.getPlans()) {
			if (newPlan.isVisible())
				serviceBrokerResource.updatePlanVisibilityOfServiceBroker(cfClient, newPlan.getService().getName(), 
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

		serviceBrokerResource.updateServiceBroker(cfClient);
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
		serviceBrokerResource.updateServiceBroker(cfClient);
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
		serviceBrokerResource.updateServiceBroker(cfClient);
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
		serviceBrokerResource.updateServiceBroker(cfClient);
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
		serviceBrokerResource.updateServiceVisibilityOfServiceBroker(cfClient, serviceDefinition.getName(), isVisible);
		
		
	}

}
