package org.cf.serviceregistry.servicebroker.model;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "credentials")
@JsonIgnoreProperties({ "id" })
public class Credentials {

	
	@Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private int id;
	
	// any "other" tags/key-value pairs    
	@ElementCollection(fetch = FetchType.LAZY)
	@MapKeyColumn(name="name")
    @Column(name="value")
    @CollectionTable(name="creds_other_attributes", joinColumns=@JoinColumn(name="creds_other_attrib_id"))
	protected Map<String,String> other = new HashMap<String,String>();

    // "any getter" needed for serialization    
    @JsonAnyGetter
    public Map<String,String> any() {
    	return other;
    }

    @JsonAnySetter
    public void set(String name, String value) {
    	other.put(name, value);
    }

	public void setId(int id) {
		this.id = id;
	}
	
	public int getId() {
		return id;
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

	@Column(nullable = false)
	private String uri;

	@Column(nullable = true)
	private String username;

	@Column(nullable = true)
	private String password;

}
