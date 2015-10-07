package org.cf.serviceregistry.servicebroker.model;
import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@Entity
@Table(name = "plans")
@JsonInclude(Include.NON_NULL)
@JsonSerialize(include=JsonSerialize.Inclusion.NON_EMPTY)
@JsonIgnoreProperties({ "service", "serviceId" })
public class Plan {
	
	private static final Log log = LogFactory.getLog(Plan.class);

	@Id
	private String id;

	private String name;
	
	@JsonBackReference
	@ManyToOne
	@JoinColumn(name="service_id", insertable = true, updatable = false)
	// Mark insertable false for compound keys, shared primary key, cascaded key
	private Service service;

	@Column(nullable = false)
	private String description;
	
	@JsonProperty("free")
	@Column(nullable = true)
	private Boolean isFree = Boolean.TRUE;

	@OneToOne(orphanRemoval = true, cascade = CascadeType.ALL, fetch=FetchType.LAZY, optional = true)
	@JoinColumn(name = "plan_cred_id", insertable=true, updatable=true, nullable=true, unique=true)
	private Credentials credentials;
	
	@JsonProperty("metadata")
	@OneToOne(optional = true, orphanRemoval = true, fetch=FetchType.LAZY, cascade = CascadeType.ALL)
	private PlanMetadata metadata;
	
	public boolean isFree() {
		return isFree.booleanValue();
	}

	public void setFree(boolean free) {
		if (isFree == null)
			isFree = true;
		this.isFree = free;
	}	
	
	public String generateId() {		
		//return UUID.nameUUIDFromBytes((this.getServiceName() + ":" + this.getName()).getBytes()).toString();
		return UUID.randomUUID().toString();
	}
	
	public synchronized void generateAndSetId() {
		if (this.id == null)
			this.id = generateId();
	}
	
	public synchronized void setId(String pk) {
		if ((this.id == null) && (pk != null))
			this.id = pk; 
		else
			generateAndSetId();
	}

	public synchronized String getId () { 
		if (id == null)
			generateAndSetId();
		return id; 
	}

	public Service getService () { return service; }
	
	public void setService (Service  service) { 
		this.service = service; 
	}
	
	public String getName () { 
		return name; 
	}

	public void setName (String name) { 
		this.name = name; 
	}
	
	@JsonIgnore
	public Credentials getCredentials() {
		return credentials;
	}

	@JsonProperty
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
	
	public void copy(Plan copyPlan) {
		Credentials creds = copyPlan.getCredentials();
		if (creds != null) {
			this.credentials = creds;
		}
		
		PlanMetadata metadata = copyPlan.getMetadata();
		if (metadata != null) {
			this.metadata = metadata;
		}
		
		String descrp = copyPlan.getDescription();
		if (descrp != null) {
			this.description = descrp;
		}
		
		this.isFree = copyPlan.isFree;		
	}

	@Override
	public String toString() {
		return "Plan [name=" + name + ", id=" + id + ", description="
				+ description + ", free=" + isFree 
				+ ", service=" + (service != null? service.getName(): " ")
				+ ", metadata=" + metadata + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
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
		
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
	
	public void update(Plan from) {
		if (from == null)
			return;
		
		if (from.name != null) {
			this.name = from.name;
		}
		
		if (from.description != null) {
			this.description = from.description;
		}
		
		if (from.isFree != this.isFree ) {
			this.isFree = from.isFree;
		}
		
		if (from.metadata != null)
			this.metadata.update(from.metadata);
		
		if (from.credentials != null)
			this.credentials.update(from.credentials);
		
	}
	
	
}
