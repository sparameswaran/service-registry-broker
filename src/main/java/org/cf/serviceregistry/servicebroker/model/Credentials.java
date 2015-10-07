package org.cf.serviceregistry.servicebroker.model;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.MapKeyColumn;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "credentials")
@JsonIgnoreProperties({ "id", "handler", "hibernateLazyInitializer" })
public class Credentials {

	@Id
    private String id;
	
	@Column(nullable = true)
	private String uri;

	@Column(nullable = true)
	private String username;

	@Column(nullable = true)
	private String password;
	
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

	public String generateId() {		
		//return UUID.nameUUIDFromBytes((this.getServiceName() + ":" + this.getName()).getBytes()).toString();
		return UUID.randomUUID().toString();
	}
	
	public synchronized void generateAndSetId() {
		if (this.id == null)
			this.id = generateId();
	}
	
	public synchronized void setId(String pk) {
		if ((this.id == null) && (pk != null))
			this.id = pk; 
		else
			generateAndSetId();
	}

	public synchronized String getId () { 
		if (id == null)
			generateAndSetId();
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
	
	public void update(Credentials copyCredentials) {
		
		String uri = copyCredentials.getUri();
		if (uri != null) {
			this.uri = uri;
		}
		
		String username = copyCredentials.getUsername();
		if (username != null) {
			this.username = username;
			// Change the associated password also
			this.password = copyCredentials.getPassword();
		}
		
		this.other.clear();
		
		for(String key:copyCredentials.other.keySet()) {
			other.put(key, copyCredentials.other.get(key) );
		}
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
	
	@Override
	public String toString() {
		return "Credentials [id=" + id + ", other=" + other + ", uri=" + uri
				+ ", username=" + username + ", password=" + password + "]";
	}


}
