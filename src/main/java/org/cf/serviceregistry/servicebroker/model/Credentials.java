package org.cf.serviceregistry.servicebroker.model;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "credentials")
@JsonIgnoreProperties({ "id" })
public class Credentials {

	@Id
	private String id;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getCertName() {
		return certName;
	}

	public void setCertName(String certName) {
		this.certName = certName;
	}

	public String getCertLocation() {
		return certLocation;
	}

	public void setCertLocation(String certLocation) {
		this.certLocation = certLocation;
	}

	public String getCertFormat() {
		return certFormat;
	}

	public void setCertFormat(String certFormat) {
		this.certFormat = certFormat;
	}

	@Column(nullable = true)
	private String certLocation;

	@Column(nullable = true)
	private String certFormat;

	@Column(nullable = true)
	private String certName;

	@Column(nullable = false)
	private String uri;

	@Column(nullable = true)
	private String username;

	@Column(nullable = true)
	private String password;

}
