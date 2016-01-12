package org.cf.serviceregistrybroker.model;

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
    private String imageUrl;

	@Column(nullable = true)
    private String longDescription;

	@Column(nullable = false)
    private String providerDisplayName;

	@Column(nullable = true)
    private String documentationUrl;

	@Column(nullable = true)
    private String supportUrl;

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		System.out.println("Image url being set to: " + imageUrl);
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

	public String getDocumentationUrl() {
		return documentationUrl;
	}

	public void setDocumentationUrl(String documentationUrl) {
		this.documentationUrl = documentationUrl;
	}

	public String getSupportUrl() {
		return supportUrl;
	}

	public void setSupportUrl(String supportUrl) {
		this.supportUrl = supportUrl;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	public void update(ServiceMetadata from) {
		if (from == null)
			return;
		
		if (from.displayName != null) {
			this.displayName = from.displayName;
		}
		
		if (from.providerDisplayName != null) {
			this.providerDisplayName = from.providerDisplayName;
		}
		
		if (from.imageUrl != null && !from.imageUrl.contains("%")) {
			this.imageUrl = from.imageUrl;
		}
		
		if (from.documentationUrl != null) {
			this.documentationUrl = from.documentationUrl;
		}
		
		if (from.longDescription != null) {
			this.longDescription = from.longDescription;
		}
	}

}
