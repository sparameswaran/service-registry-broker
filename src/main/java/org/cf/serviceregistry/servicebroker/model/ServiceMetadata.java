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

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public URI getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(URI imageUrl) {
		this.imageUrl = imageUrl;
	}

	public String getLongDescription() {
		return longDescription;
	}

	public void setLongDescription(String longDescription) {
		this.longDescription = longDescription;
	}

	public String getProviderDisplayName() {
		return providerDisplayName;
	}

	public void setProviderDisplayName(String providerDisplayName) {
		this.providerDisplayName = providerDisplayName;
	}

	public URI getDocumentationUrl() {
		return documentationUrl;
	}

	public void setDocumentationUrl(URI documentationUrl) {
		this.documentationUrl = documentationUrl;
	}

	public URI getSupportUrl() {
		return supportUrl;
	}

	public void setSupportUrl(URI supportUrl) {
		this.supportUrl = supportUrl;
	}

	@Column(nullable = true)
    private URI supportUrl;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

}
