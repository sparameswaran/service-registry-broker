package org.cf.serviceregistry.servicebroker.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@Entity
@Table(name = "planmetadata")
@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties({ "id", "handler", "hibernateLazyInitializer"})
public class PlanMetadata {
	
	@Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private int id;

	@ElementCollection (targetClass=String.class, fetch = FetchType.LAZY)
	@JsonDeserialize(as = ArrayList.class, contentAs = String.class)
	private List<String>bullets = new ArrayList<String>();

	@JsonProperty("cost")
	@OneToOne(optional = true, orphanRemoval = true, fetch=FetchType.LAZY, cascade = CascadeType.ALL)
	private Cost cost;

	public List<String> getBullets() {
		return bullets;
	}

	public synchronized void setBullets(List<String> bullets) {
		this.bullets = bullets;
	}
	
	public synchronized void addBullet(String bullet) {
		if (bullet != "" && !this.bullets.contains(bullet))
			this.bullets.add(bullet);
	}
	
	public synchronized void removeBullet(String bullet) {
		this.bullets.remove(bullet);
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	public Cost getCost() {
		
		if (cost == null) {
			cost = new Cost();
			cost.setAmount("usd", 0.0);
			cost.setUnit("MONTHLY");
		}
		return cost;
	}

	public void setCost(Cost cost) {
		this.cost = cost;
	}

	public void update(PlanMetadata from) {
		if (from == null)
			return;
		
		if (from.bullets != null) {
			this.bullets = from.bullets;
		}
		
		if (from.cost != null) {
			this.cost = from.cost;
		}
	}
	

}
