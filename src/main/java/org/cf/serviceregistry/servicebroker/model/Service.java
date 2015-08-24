package org.cf.serviceregistry.servicebroker.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@Entity
@Table(name = "services")
@JsonInclude(Include.NON_NULL)
@JsonSerialize(include=JsonSerialize.Inclusion.NON_EMPTY)
public class Service {

	@Id
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
			plan.setServiceId(this.name);
		}
	}

	public synchronized void addPlan(Plan plan) {
		if (!this.plans.contains(plan))
			this.plans.add(plan);
		
		plan.setServiceId(this.name);
	}

	public synchronized void removePlan(Plan plan) {
		if (this.plans.contains(plan))
			this.plans.remove(plan);
	}

	
	@Override
	public String toString() {
		return "Service [name=" + name + ", description=" + description
				+ ", plans=" + plans + "]";
	}

}
