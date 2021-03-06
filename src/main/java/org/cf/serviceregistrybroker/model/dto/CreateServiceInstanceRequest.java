package org.cf.serviceregistrybroker.model.dto;

import java.util.Map;
import java.util.Objects;

import org.apache.commons.beanutils.BeanUtils;
import org.cf.serviceregistrybroker.model.Plan;
import org.cf.serviceregistrybroker.model.ServiceDefinition;
import org.cf.serviceregistrybroker.model.ServiceInstance;
import org.hibernate.validator.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * A request sent by the cloud controller to create a new instance
 * of a service.
 * 
 * @author sgreenberg@gopivotal.com
 */
@JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE)
public class CreateServiceInstanceRequest {

	@NotEmpty
	@JsonSerialize
	@JsonProperty("service_id")
	private String serviceDefinitionId;
	
	@NotEmpty
	@JsonSerialize
	@JsonProperty("plan_id")
	private String planId;
	
	@NotEmpty
	@JsonSerialize
	@JsonProperty("organization_guid")
	private String organizationGuid;
	
	@NotEmpty
	@JsonSerialize
	@JsonProperty("space_guid")
	private String spaceGuid;
	
	@JsonSerialize
	@JsonProperty("parameters")
	private Map<String, Object> parameters;

	//Cloud Controller doesn't send the definition, it's populated later
	@JsonIgnore
	private ServiceDefinition serviceDefinition;

	//Cloud Controller doesn't send instanceId in the body
	@JsonIgnore
	private String serviceInstanceId;
	
	public CreateServiceInstanceRequest() {
	}
	
	public CreateServiceInstanceRequest(String serviceDefinitionId, String planId, String organizationGuid, String spaceGuid, boolean async, Map<String, Object> parameters) {
		this.serviceDefinitionId = serviceDefinitionId;
		this.planId = planId;
		this.organizationGuid = organizationGuid;
		this.spaceGuid = spaceGuid;
		this.parameters = parameters;
	}

	public String getServiceDefinitionId() {
		return serviceDefinitionId;
	}

	public ServiceDefinition getServiceDefinition() {
		return serviceDefinition;
	}
	
	public Plan getPlan() {
		return serviceDefinition.findPlan(planId);
	}
	
	public String getPlanId() {
		return planId;
	}

	public String getOrganizationGuid() {
		return organizationGuid;
	}

	public String getSpaceGuid() {
		return spaceGuid;
	}

	public String getServiceInstanceId() {
		return serviceInstanceId;
	}

	public Map<String, Object> getParameters() {
		return parameters;
	}

	public <T> T getParameters(Class<T> cls) {
		try {
			T bean = cls.newInstance();
			BeanUtils.populate(bean, parameters);
			return bean;
		} catch (Exception e) {
			throw new IllegalArgumentException("Error mapping parameters to class of type " + cls.getName());
		}
	}

	public CreateServiceInstanceRequest withServiceDefinition(ServiceDefinition svc) {
		this.serviceDefinition = svc;
		return this;
	}

	public CreateServiceInstanceRequest withServiceInstanceId(final String serviceInstanceId) {
		this.serviceInstanceId = serviceInstanceId;
		return this;
	}

	public CreateServiceInstanceRequest and() {
		return this;
	}
	
	public ServiceInstance createServiceInstance() {
		
		ServiceInstance instance = new ServiceInstance();
		instance.setId(serviceInstanceId);
		instance.setPlanId(planId);
		instance.setOrgGuid(organizationGuid);
		instance.setSpaceGuid(spaceGuid);
		instance.setServiceId(serviceDefinitionId);
		instance.setParameters(parameters);
		return instance;
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		CreateServiceInstanceRequest that = (CreateServiceInstanceRequest) o;
		return Objects.equals(serviceDefinitionId, that.serviceDefinitionId) &&
				Objects.equals(planId, that.planId) &&
				Objects.equals(organizationGuid, that.organizationGuid) &&
				Objects.equals(spaceGuid, that.spaceGuid) &&
				Objects.equals(parameters, that.parameters);
	}

	@Override
	public int hashCode() {
		return Objects.hash(serviceDefinitionId, planId, organizationGuid, spaceGuid, parameters);
	}

	@Override
	public String toString() {
		return "CreateServiceInstanceRequest [serviceDefinitionId="
				+ serviceDefinitionId + ", planId=" + planId
				+ ", organizationGuid=" + organizationGuid + ", spaceGuid="
				+ spaceGuid + ", parameters=" + parameters
				+ ", serviceDefinition=" + serviceDefinition
				+ ", serviceInstanceId=" + serviceInstanceId + "]";
	}
	
	
}