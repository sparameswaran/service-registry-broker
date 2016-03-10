package org.cf.serviceregistrybroker.cfutils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.cloudfoundry.client.CloudFoundryClient;
import org.cloudfoundry.client.v2.servicebrokers.ServiceBrokerResource;
import org.cloudfoundry.client.v2.serviceplans.ServicePlanResource;
import org.cloudfoundry.client.v2.services.ServiceResource;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;


@Configuration
@EnableAutoConfiguration
@Lazy
public class ServiceBrokerAppResource {
	private static final String VCAP_APPLICATION_URI = "application_uris";
	private static final Logger log = Logger.getLogger(ServiceBrokerAppResource.class);
	
	private String appName;
	private String[] appUris;
	
	private String brokerUri;
	private String brokerName;
	private String brokerId;
	
	@Value("${security.user.name}")
    private String brokerUsername;
    
    @Value("${security.user.password}")
    private String brokerPassword;
	
	@Value("${VCAP_APPLICATION}")
    private String vcapAppEnv;
	
	private Map<String, ServicePlanEntry> managedServicePlanMap = new HashMap<String, ServicePlanEntry>();
    
	
	/*
	private static ServiceBrokerAppResource theOne = new ServiceBrokerAppResource();
	
	public static ServiceBrokerAppResource getInstance() {
		return theOne;
	}	
	
	private ServiceBrokerAppResource() { }
	*/
	
	@Bean
	public ServiceBrokerAppResource serviceBrokerResource() {
		 return new ServiceBrokerAppResource();
	}
	
	public String getAppName() {
		return appName;
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}

	public String getVcapAppEnv() {
		return vcapAppEnv;
	}

	public void setVcapAppEnv(String vcapAppEnv) {
		this.vcapAppEnv = vcapAppEnv;
	}

	public synchronized String[] getAppUris() {
		if (vcapAppEnv == null)
			return null;
		
		if (appUris != null)
			return appUris;
		
		JSONParser jsonParser = new JSONParser();
		try {
			//log.debug("vcapAppEnv: " +  vcapAppEnv);
			JSONObject vcapAppEnvJson = (JSONObject) jsonParser.parse(vcapAppEnv);
			JSONArray jsonArray = (JSONArray)vcapAppEnvJson.get(VCAP_APPLICATION_URI);
			appUris = new String[jsonArray.size()];
			for(int i = 0; i < jsonArray.size(); i++) {
				Object jsonEntry = jsonArray.get(i);
				appUris[i] = jsonEntry.toString();
			}
			
			//log.debug("Got appUris: " + appUris);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return appUris;
	}

	public void setAppUris(String[] appUris) {
		this.appUris = appUris;
	}

	public String getBrokerUri() {
		return brokerUri;
	}

	public void setBrokerUri(String brokerUri) {
		this.brokerUri = brokerUri;
	}

	public String getBrokerName() {
		return brokerName;
	}

	public void setBrokerName(String brokerName) {
		this.brokerName = brokerName;
	}

	public String getBrokerUsername() {
		return brokerUsername;
	}

	public void setBrokerUsername(String brokerUsername) {
		this.brokerUsername = brokerUsername;
	}

	public String getBrokerPassword() {
		return brokerPassword;
	}

	public void setBrokerPassword(String brokerPassword) {
		this.brokerPassword = brokerPassword;
	}
    
	public String getBrokerId() {
		return brokerId;
	}

	public void setBrokerId(String brokerId) {
		this.brokerId = brokerId;
	}

	private boolean appUriMatches(String absoluteUri) {
		String[] appUris = getAppUris();
		if (appUris == null || absoluteUri == null)
			return false;
		
		for(String appUri: appUris) {
			if (absoluteUri.indexOf(appUri) > 0)
				return true;
		}
		return false;
	}
	
	private synchronized void populateBrokerDetails(CloudFoundryClient cfClient) {
		if (getBrokerName() != null && this.getBrokerUri() != null)
			return;
		
		String serviceBrokerUrl = "";
		String serviceBrokerName = "";
		List<ServiceBrokerResource> serviceBrokers = CFAppManager.requestServiceBrokers(cfClient).get();
		for(ServiceBrokerResource sbResource: serviceBrokers) {
			serviceBrokerName = sbResource.getEntity().getName();
			serviceBrokerUrl = sbResource.getEntity().getBrokerUrl();
			
			if (appUriMatches(serviceBrokerUrl)) {
				this.setBrokerName(serviceBrokerName);
				this.setBrokerUri(serviceBrokerUrl);
				this.setBrokerId(sbResource.getMetadata().getId());
				log.debug("Got matching service broker: " + sbResource);
				return;
			}
		}
		
		populateManagedServices(cfClient, null);
	}
	
	
	private void updateServiceBrokerOnCF(CloudFoundryClient cfClient) {
		CFAppManager.requestUpdateServiceBroker(cfClient, 
												this.brokerName,
												this.brokerId,
												this.brokerUsername, 
												this.brokerPassword, 
												this.brokerUri)
					.get();
	
	}
	
	public void updateServiceBroker(CloudFoundryClient cfClient) {
		this.populateBrokerDetails(cfClient);
		this.updateServiceBrokerOnCF(cfClient);
	}
	
	private void populateManagedServices(CloudFoundryClient cfClient, String targetServiceName) {
		this.populateBrokerDetails(cfClient);
		
		List<ServiceResource> serviceResources = CFAppManager.requestListServices(cfClient, this.getBrokerId(), targetServiceName).get();
		for(ServiceResource serviceResource: serviceResources) {	
			String serviceName = serviceResource.getEntity().getLabel();
			String cfGeneratedServiceId = serviceResource.getMetadata().getId();
			
			ServicePlanEntry servicePlanEntry = managedServicePlanMap.get(serviceName);
			
			if (servicePlanEntry == null) {
				servicePlanEntry = new ServicePlanEntry();
				servicePlanEntry.setServiceName(serviceName);
			}
		
			servicePlanEntry.setGeneratedServiceId(cfGeneratedServiceId);
			servicePlanEntry.clearServicePlans();
			
			managedServicePlanMap.put(serviceName, servicePlanEntry);
			List<ServicePlanResource> servicePlanResources = CFAppManager.requestListServiceServicePlans(cfClient, 
					this.getBrokerId(), cfGeneratedServiceId).get();
			for(ServicePlanResource servicePlanResource: servicePlanResources) {	
				String servicePlanName = servicePlanResource.getEntity().getName();
				String servicePlanId = servicePlanResource.getMetadata().getId();
				servicePlanEntry.putServicePlan(servicePlanName, servicePlanId);
			}
		}
	}
	
	public void updateServiceVisibilityOfServiceBroker(CloudFoundryClient cfClient, String targetServiceName, boolean isVisible) {
		
		log.info("Enabling ServicePlan visibility for service with name: " + targetServiceName);
		
		// Reload the caches
		populateManagedServices(cfClient, targetServiceName);
		
		ServicePlanEntry servicePlanEntry = managedServicePlanMap.get(targetServiceName);
		if (servicePlanEntry == null || servicePlanEntry.getGeneratedServiceId() == null) {
			populateManagedServices(cfClient, targetServiceName);
			servicePlanEntry = managedServicePlanMap.get(targetServiceName);
		}
	
		if (servicePlanEntry == null) {
			log.error("Unable to find any service associated with service: " + targetServiceName);
			return;
		}
			
		/*
		 * This wont work till CC fixes the problem with query thats encoded
		 * This works: GET    /v2/service_plans?q%3Dservice_broker_guid%2520IN%252085f6d468-f537-459e-aaaa-0263ba4ceb74
		 * This fails: GET    /v2/service_plans?q=service_broker_guid%2520IN%252085f6d468-f537-459e-aaaa-0263ba4ceb74
		 * The failing query is generated by cf java client
		List<ServicePlanResource> servicePlanResources = CFAppManager.requestListServicePlans(cfClient, this.getBrokerId(), serviceId).get();
		log.info("3 Found # of service plans: " + servicePlanResources.size());
		for(ServicePlanResource servicePlanResource: servicePlanResources) {
			
			log.info("3 Found service plan: " + servicePlanResource);
			String servicePlanName = servicePlanResource.getEntity().getName();
			String servicePlanId = servicePlanResource.getMetadata().getId();
			
			//CFAppManager.requestPublicizeServicePlan(cfClient, servicePlanId, isVisible).get();
		}
		*/
		
		Map<String, String> servicePlanMap = servicePlanEntry.getServicePlanMap();
		for(String servicePlanName: servicePlanMap.keySet()) {
			String cfServicePlanId = servicePlanMap.get(servicePlanName);
			CFAppManager.requestPublicizeServicePlan(cfClient, cfServicePlanId, isVisible).get();
		}
		
		log.info("Done with updating visibility of service plans");
	}

	public void updatePlanVisibilityOfServiceBroker(CloudFoundryClient cfClient, String targetServiceName, String targetPlanName, boolean isVisible) {
		/*
		this.populateBrokerDetails(cfClient);
		log.info("Enabling ServicePlan visibility for service with name: " + targetServiceName 
				+ ", plan name: " + targetPlanName + " and appResource: " + this);

		List<ServiceResource> serviceResources = CFAppManager.requestListServices(cfClient, this.getBrokerId(), 
				targetServiceName).get();
		log.info("1 Found # of services: " + serviceResources.size());

		for(ServiceResource serviceResource: serviceResources) {			
			log.info("1 Found service: " + serviceResource);
			String serviceName = serviceResource.getEntity().getLabel();
			String cfGeneratedServiceId = serviceResource.getMetadata().getId();
			//String serviceBrokerId = serviceResource.getEntity().getServiceBrokerId();

			// found the matching service using service label
			if (serviceName.equals(targetServiceName)) {
				List<ServicePlanResource> servicePlanResources = CFAppManager.requestListServiceServicePlans(cfClient, 
						this.getBrokerId(), cfGeneratedServiceId).get();
				log.info("2 Found # of service plans: " + servicePlanResources.size());
				for(ServicePlanResource servicePlanResource: servicePlanResources) {

					log.info("2 Found service plan: " + servicePlanResource);
					
					String servicePlanName = servicePlanResource.getEntity().getName();
					String servicePlanId = servicePlanResource.getMetadata().getId();

					if (servicePlanName.equals(targetPlanName)) {
							CFAppManager.requestPublicizeServicePlan(cfClient, servicePlanId, isVisible).get();
					}
				}
			}
		}
		*/

		log.info("Enabling ServicePlan visibility for service with name: " + targetServiceName 
				+ ", plan name: " + targetPlanName);
		
		// Reload the caches
		populateManagedServices(cfClient, targetServiceName);
		
		ServicePlanEntry servicePlanEntry = managedServicePlanMap.get(targetServiceName);
		if (servicePlanEntry == null || servicePlanEntry.getGeneratedServiceId() == null) {
			populateManagedServices(cfClient, targetServiceName);
			servicePlanEntry = managedServicePlanMap.get(targetServiceName);
		}
	
		if (servicePlanEntry == null) {
			log.error("Unable to find any service associated with service: " + targetServiceName);
			return;
		}
		
		Map<String, String> servicePlanMap = servicePlanEntry.getServicePlanMap();
		String cfServicePlanId = servicePlanMap.get(targetPlanName);
		if (cfServicePlanId != null) {
			CFAppManager.requestPublicizeServicePlan(cfClient, cfServicePlanId, isVisible).get();
		} else {
			log.error("Unable to find any service plan associated with service: " + targetServiceName + " and plan: " + targetPlanName);
		}

		log.info("Done with updating visibility of specified service plan");
	}
	
	@Override
	public String toString() {
		return "ServiceBrokerAppResource [appName=" + appName + ", appUris=" + Arrays.toString(appUris)
				+ ", brokerId="+ brokerId + ", brokerUri=" + brokerUri + ", brokerName=" + brokerName 
				+ ", brokerUsername=" + brokerUsername + ", brokerPassword=" + brokerPassword + "]";
	}
	
}
