package org.cf.serviceregistry.servicebroker.model;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@Entity
@Table(name = "services")
@JsonInclude(Include.NON_NULL)
@JsonSerialize(include=JsonSerialize.Inclusion.NON_EMPTY)
public class Service {
	
	private static final Log log = LogFactory.getLog(Service.class);
	
	@Id
	@Column(nullable = false)
	private String id;
	
	@Column(nullable = false)
	private String name;
	
	@Column(nullable = false)
	private String description;

	@Column(nullable = false)
	private boolean bindable = true;

	// Dont miss the mappedBy tag - persistence of the owned relationship will falter...
	@OneToMany(mappedBy="service", orphanRemoval = true, fetch=FetchType.LAZY, cascade=CascadeType.ALL)
	private Set<Plan> plans = new HashSet<Plan>();

	@JsonProperty("metadata")
	@OneToOne(optional = true, orphanRemoval = true, fetch=FetchType.LAZY, cascade=CascadeType.ALL)
	private ServiceMetadata metadata;

	public synchronized void generateAndSetId() {
		//id = UUID.nameUUIDFromBytes(this.getName().getBytes()).toString();
		id = UUID.randomUUID().toString();
	}
	
	public synchronized String getId() {
		if (id == null)
			generateAndSetId();
		 return id;
	}

	public synchronized void setId(String uuid) {
		if ((this.id == null) && (uuid != null))
			this.id = uuid;
		else if (this.getName() != null)
			generateAndSetId();
	}
	
	public ServiceMetadata getMetadata() {
		return metadata;
	}

	public void setMetadata(ServiceMetadata serviceMetadata) {
		this.metadata = serviceMetadata;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
		id = UUID.nameUUIDFromBytes(name.getBytes()).toString();
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String descrp) {
		this.description = descrp;
	}

	public boolean isBindable() {
		return bindable;
	}

	public void setBindable(boolean bindable) {
		this.bindable = bindable;
	}

	public Set<Plan> getPlans() {
		return plans;
	}

	public synchronized void setPlans(Set<Plan> plans) {
		this.plans = plans;
		for(Plan plan: plans) {
			plan.generateAndSetId();
			plan.setService(this);
		}
	}

	public synchronized void addPlan(Plan plan) {		
		if (!this.plans.contains(plan)) {
			this.plans.add(plan);
			plan.setService(this);
		}
	}

	public synchronized void removePlan(Plan plan) {
		if (this.plans.contains(plan)) {
			this.plans.remove(plan);
			plan.setService(null);
		}
	}

	
	@Override
	public String toString() {
		return "Service [name=" + name + ", uuid=" +  id + ", description=" + description
				+ ", plans=" + plans + "]";
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
		Service other = (Service) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

}
