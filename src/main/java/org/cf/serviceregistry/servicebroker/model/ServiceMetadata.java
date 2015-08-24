package org.cf.serviceregistry.servicebroker.model;

import java.net.URI;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@Entity
@Table(name = "servicemetadata")
@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties({ "id", "handler", "hibernateLazyInitializer"})
public class ServiceMetadata {
	
	@Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private int id;

	@Column(nullable = false)
    private String displayName;

	@Column(nullable = true)
    private URI imageUrl;

	@Column(nullable = true)
    private String longDescription;

	@Column(nullable = false)
    private String providerDisplayName;

	@Column(nullable = true)
    private URI documentationUrl;

	@Column(nullable = true)
    private URI supportUrl;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

}
