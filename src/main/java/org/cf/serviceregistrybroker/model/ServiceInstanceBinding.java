package org.cf.serviceregistrybroker.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.MapKeyColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@Entity
@Table(name = "service_bindings")
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE)
public class ServiceInstanceBinding {

	@Id
	private String id;

	@Column(nullable = false)
	private String instanceId;

	@JsonSerialize
	@JsonProperty("service_id")
	@Column(nullable = false)
	private String serviceId;

	@JsonSerialize
	@JsonProperty("plan_id")
	@Column(nullable = false)
	private String planId;

	@JsonSerialize
	@JsonProperty("app_guid")
	@Column(nullable = false)
	private String appGuid;
	
	@JsonSerialize
	@JsonProperty("tags")
	@Column(nullable = true, length = 512)
	private String tags;


	@ElementCollection(fetch = FetchType.LAZY)
	@MapKeyColumn(name="name")
    @Column(name="value")
    @CollectionTable(name="binding_creds_attributes", joinColumns=@JoinColumn(name="binding_creds_attrib_id"))
	protected Map<String,String> credentials = new HashMap<String,String>();

	@JsonSerialize
	@JsonProperty("syslog_drain")
	@Column(nullable = true)
	private String syslogDrainUrl;
	
	public ServiceInstanceBinding() { }
	
	public ServiceInstanceBinding(String id, 
			String serviceInstanceId,
			String serviceId,
			String planId,
			List<String> bullets,
			Map<String,String> credentials,
			String syslogDrainUrl, String appGuid) {
		this.id = id;
		this.planId = planId;
		this.serviceId = serviceId;
		this.instanceId = serviceInstanceId;
		this.syslogDrainUrl = syslogDrainUrl;
		this.appGuid = appGuid;
		this.tags = bullets.toString();
		this.credentials = credentials;
	}

	public String getSyslogDrainUrl() {
		return syslogDrainUrl;
	}

	public void setSyslogDrainUrl(String syslogDrainUrl) {
		this.syslogDrainUrl = syslogDrainUrl;
	}

	public String getTags() {
		return tags;
	}

	public void setTags(String tags) {
		this.tags = tags;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;

		ServiceInstanceBinding that = (ServiceInstanceBinding) o;

		if (!appGuid.equals(that.appGuid))
			return false;
		if (!id.equals(that.id))
			return false;
		if (!instanceId.equals(that.instanceId))
			return false;
		if (!planId.equals(that.planId))
			return false;
		if (!serviceId.equals(that.serviceId))
			return false;

		return true;
	}

	@Override
	public int hashCode() {
		int result = id.hashCode();
		result = 31 * result + instanceId.hashCode();
		result = 31 * result + serviceId.hashCode();
		result = 31 * result + planId.hashCode();
		result = 31 * result + appGuid.hashCode();
		return result;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getInstanceId() {
		return instanceId;
	}

	public void setInstanceId(String instanceId) {
		this.instanceId = instanceId;
	}

	public String getServiceId() {
		return serviceId;
	}

	public void setServiceId(String serviceId) {
		this.serviceId = serviceId;
	}

	public String getPlanId() {
		return planId;
	}

	public void setPlanId(String planId) {
		this.planId = planId;
	}

	public String getAppGuid() {
		return appGuid;
	}

	public void setAppGuid(String appGuid) {
		this.appGuid = appGuid;
	}

	public Map<String, String> getCredentials() {
		return credentials;
	}

	private void setCredentials(Map<String, String> map) {
		this.credentials = map;
	}	
	
	@Override
	public String toString() {
		return "ServiceInstanceBinding [id=" + id + ", instanceId=" + instanceId 
				+ ", serviceId=" + serviceId + ", planId=" + planId 
				+ ", appGuid=" + appGuid + ", tags=" + tags 
				+ ", credentials=" + credentials
				+ ", syslogDrainUrl=" + syslogDrainUrl + "]";
	}


}
