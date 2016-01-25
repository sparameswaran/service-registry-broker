package org.cf.serviceregistrybroker.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.apache.log4j.Logger;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
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
	@CollectionTable(name = "plan_metadata_bullets", uniqueConstraints = @UniqueConstraint(columnNames = {
	        "plan_metadata_id", "bullets" }))
	@JsonDeserialize(as = ArrayList.class, contentAs = String.class)
	private List<String>bullets = new ArrayList<String>();

	// Dont miss the mappedBy tag - persistence of the owned relationship will falter...
	@OneToMany(mappedBy="planmetadata", orphanRemoval = true, fetch=FetchType.LAZY, cascade=CascadeType.ALL)
	private Set<Cost> costs = new HashSet<Cost>();

	private static final Logger log = Logger.getLogger(PlanMetadata.class);

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
	
	public Set<Cost> getCosts() {
		if (costs.size() == 0) {
			Cost cost = new Cost();
			cost.setAmount("usd", 0.0);
			costs.add(cost);
		}
		return costs;
	}

	public void setCosts(Set<Cost> fromCosts) {
		this.costs.clear();
		this.costs.addAll(fromCosts);
		
		for(Cost cost: this.costs.toArray(new Cost[] {} )) {
			cost.setPlanmetadata(this);
			//log.info("Added cost: " + cost);
		}
	}

	public void update(PlanMetadata from) {
		if (from == null)
			return;
		
		if (from.bullets != null) {
			this.bullets = from.bullets;
		}
		
		if (from.costs != null) {
			this.costs.clear();
			
			this.costs.addAll(from.costs);
			for(Cost cost: this.costs.toArray(new Cost[] {} )) {
				cost.setPlanmetadata(this);
				//log.info("Added cost: " + cost);
			}
			
		}
	}
	

}
