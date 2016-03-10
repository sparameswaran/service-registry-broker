package org.cf.serviceregistrybroker.cfutils;

import java.util.HashMap;
import java.util.Map;

public class ServicePlanEntry {

	String serviceName;
	String generatedServiceId;
	Map<String, String> servicePlanMap = new HashMap<String, String>();
	
	public ServicePlanEntry() {
	}
	
	public ServicePlanEntry(String serviceName, String generatedServiceId) {
		this.serviceName = serviceName;
		this.generatedServiceId = generatedServiceId;
	}
	
	public String getServiceName() {
		return serviceName;
	}
	
	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}
	
	public String getGeneratedServiceId() {
		return generatedServiceId;
	}
	
	public void setGeneratedServiceId(String generatedServiceId) {
		this.generatedServiceId = generatedServiceId;
	}
	
	public Map<String, String> getServicePlanMap() {
		return servicePlanMap;
	}
	
	public void setServicePlanMap(Map<String, String> servicePlanMap) {
		this.servicePlanMap = servicePlanMap;
	}
	
	public String lookupServicePlanId(String planName) {
		return this.servicePlanMap.get(planName);
	}
	
	public void clearServicePlans() {
		this.servicePlanMap.clear();
	}
	
	public String putServicePlan(String planName, String planId) {
		return this.servicePlanMap.put(planName, planId);
	}
	
	public String removeServicePlan(String planName) {
		return this.servicePlanMap.remove(planName);
	}
	
}