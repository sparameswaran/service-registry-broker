package org.cf.serviceregistry.servicebroker.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "plans")
@JsonIgnoreProperties({ "credentials" })
public class Plan {

	@Id
	private String id;

	@Column(nullable = false)
	private String name;

	@Column(nullable = false)
	private String description;

	@OneToOne(orphanRemoval = true)
	@JoinColumn(name = "plan_cred_id")
	private Credentials credentials;

	public Credentials getCredentials() {
		return credentials;
	}

	public void setCredentials(Credentials credentials) {
		this.credentials = credentials;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
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
}
