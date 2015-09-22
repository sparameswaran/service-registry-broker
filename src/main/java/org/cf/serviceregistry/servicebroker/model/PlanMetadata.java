package org.cf.serviceregistry.servicebroker.model;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

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
	@JsonDeserialize(as = HashSet.class, contentAs = String.class)
	private Set<String>bullets = new HashSet<String>();

	public Set<String> getBullets() {
		return bullets;
	}

	public synchronized void setBullets(Set<String> bullets) {
		this.bullets = bullets;
	}
	
	public synchronized void setBullets(List<String> bullets) {
		for( String bullet: bullets ) {
			this.bullets.add(bullet);
		}
	}
	
	public synchronized void addBullet(String bullet) {
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
	

}
