package org.cf.serviceregistry.servicebroker.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Embeddable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapKeyColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@Entity
@Table(name = "plans")
@JsonInclude(Include.NON_NULL)
@JsonSerialize(include=JsonSerialize.Inclusion.NON_EMPTY)
@JsonIgnoreProperties({ "credentials", "service", "pkId", "serviceId"})
public class Plan {

	@EmbeddedId
	private PlanPk pkId = new PlanPk();

	@JsonBackReference
	@ManyToOne
	@JoinColumn(name="service_id", insertable = false, updatable = false)
	private Service service;

	@Column(nullable = false)
	private String description;
	
	@JsonProperty("isFree")
	@Column(nullable = true)
	private Boolean isFree = Boolean.TRUE;

	@OneToOne(orphanRemoval = true, cascade = CascadeType.PERSIST)
	@JoinColumn(name = "plan_cred_id")
	private Credentials credentials;
	
	@JsonProperty("metadata")
	@OneToOne(optional = true, orphanRemoval = true, fetch=FetchType.LAZY, cascade = CascadeType.ALL)
	private PlanMetadata metadata;
	
	public boolean isFree() {
		return isFree.booleanValue();
	}

	/*
	public void setFree(Boolean isFree) {
		if (isFree == null)
			isFree = Boolean.TRUE;
		this.isFree = isFree;
	}
	*/

	public void setIsFree(Boolean free) {
		if (isFree == null)
			isFree = Boolean.TRUE;
		this.isFree = isFree;
	}

	public String getId() {
		return UUID.nameUUIDFromBytes((this.service.getName() + ":" + this.getName()).getBytes()).toString();
	}

	public void setId(String id) {
		
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((pkId == null) ? 0 : pkId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Plan other = (Plan) obj;
		if (pkId == null) {
			if (other.pkId != null)
				return false;
		} else if (!pkId.equals(other.pkId))
			return false;
		return true;
	}

	public void setPkId (PlanPk pk) { 
		pkId = pk; 
	}

	public PlanPk getPkId () { return pkId; }

	public Service getService () { return service; }
	
	public String getName () { 
		return pkId.getPlanId(); 
	}

	public void setName (String name) { 
		pkId.setPlanId(name); 
	}
	
	public String getServiceId () { 
		return pkId.getServiceId(); 
	}
	
	public void setServiceId(String serviceId) { 
		pkId.setServiceId(serviceId); 
	}
	
	public Credentials getCredentials() {
		return credentials;
	}

	public void setCredentials(Credentials credentials) {
		this.credentials = credentials;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String descrp) {
		this.description = descrp;
	}
	
	public PlanMetadata getMetadata() {
		return metadata;
	}

	public void setMetadata(PlanMetadata planMetadata) {
		this.metadata = planMetadata;
	}

	@Override
	public String toString() {
		return "Plan [pkId=" + pkId + ", description="
				+ description + "]";
	}
	
	
}
