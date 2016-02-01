package org.cf.serviceregistrybroker.model;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

@Entity
@Table(name = "service_instances")
@JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE)
public class ServiceInstance {

	@Id
	private String id;

	@JsonSerialize
	@JsonProperty("service_id")
	@Column(nullable = false)
	private String serviceId;

	@JsonSerialize
	@JsonProperty("plan_id")
	@Column(nullable = false)
	private String planId;

	@JsonSerialize
	@JsonProperty("organization_guid")
	@Column(nullable = false)
	private String orgGuid;

	@JsonSerialize
	@JsonProperty("space_guid")
	@Column(nullable = false)
	private String spaceGuid;

	@JsonSerialize
	@JsonProperty("parameters")
	@Column(nullable = true,  length = 512)
	private String parameters;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getServiceId() {
		return serviceId;
	}

	public void setServiceId(String serviceId) {
		this.serviceId = serviceId;
	}

	public String getPlanId() {
		return planId;
	}

	public void setPlanId(String planId) {
		this.planId = planId;
	}

	public String getOrgGuid() {
		return orgGuid;
	}

	public void setOrgGuid(String orgGuid) {
		this.orgGuid = orgGuid;
	}

	public String getSpaceGuid() {
		return spaceGuid;
	}

	public void setSpaceGuid(String spaceGuid) {
		this.spaceGuid = spaceGuid;
	}
	
	public Map<String, Object> getParameters() {
		return convertJsonStringToMap(parameters);
	}

	public void setParameters(Map<String, Object> parameters) {
		this.parameters = convertToJSON(parameters);
	}

	/*
	@SuppressWarnings("unchecked")
	static Map<String, String> convertToMap(String content) {
		if (content == null)
			return null;
		
		HashMap<String, String> content_map = null;
		try {
			content_map = (HashMap<String, String>) (new JSONParser().parse(content));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return content_map;
	}
	*/	

	static Map<String, Object> convertJsonStringToMap(String jsonContent) {
		if (jsonContent == null)
			return null;
		
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Double.class,  new JsonSerializer<Double>() {

              public JsonElement serialize(Double src, Type typeOfSrc,
                                JsonSerializationContext context) {
                            Integer value = (int)Math.round(src);
                                        return new JsonPrimitive(value);
                                                }
                  });

        Gson gson = gsonBuilder.create();
        HashMap<String, Object> map = gson.fromJson(jsonContent, HashMap.class);
        return map;
	}
	
	static String convertToJSON(Map<String, Object> map) {
		if (map == null)
			return null;
		
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Double.class,  new JsonSerializer<Double>() {

              public JsonElement serialize(Double src, Type typeOfSrc,
                                JsonSerializationContext context) {
                            Integer value = (int)Math.round(src);
                                        return new JsonPrimitive(value);
                                                }
                  });

        Gson gson = gsonBuilder.create();
        return gson.toJson(map);
	}

	static Map<String, Object> convertToObjectMap(Map<String, String> srcMap) {
		if (srcMap == null)
			return null;
		
		HashMap<String, Object> targetMap = new HashMap<String, Object>();
		for(String key: srcMap.keySet()) {
			String val = srcMap.get(key);
			Object nativeVal = val;
			try {
				nativeVal = Double.valueOf(val);
				Double double1 = (Double)nativeVal;
				if (double1.doubleValue() == double1.intValue()) {
					nativeVal = new Integer(double1.intValue());
				}				
			} catch(NumberFormatException ipe) {
				String lowerVal = val.trim().toLowerCase();				
				if (lowerVal.equals("true") || lowerVal.equals("false")) {
					nativeVal = Boolean.valueOf(val);
				} 
			}
			targetMap.put(key, nativeVal);
		}
		return targetMap;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null || getClass() != obj.getClass())
			return false;
		ServiceInstance that = (ServiceInstance) obj;

		if (!id.equals(that.id))
			return false;
		if (!planId.equals(that.planId))
			return false;
		if (!serviceId.equals(that.serviceId))
			return false;
		if (!orgGuid.equals(that.orgGuid))
			return false;
		if (!spaceGuid.equals(that.spaceGuid))
			return false;

		return true;
	}

	@Override
	public int hashCode() {
		int result = id.hashCode();
		result = 31 * result + serviceId.hashCode();
		result = 31 * result + planId.hashCode();
		result = 31 * result + orgGuid.hashCode();
		result = 31 * result + spaceGuid.hashCode();
		return result;
	}

}
