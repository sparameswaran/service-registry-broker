/*
 * Copyright 2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.cf.serviceregistry.serviecbroker.binding;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.HashMap;
import java.util.Map;

import org.cf.serviceregistry.AbstractControllerTest;
import org.cf.serviceregistry.servicebroker.model.Credentials;
import org.cf.serviceregistry.servicebroker.model.Plan;
import org.cf.serviceregistry.servicebroker.model.Service;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import com.fasterxml.jackson.core.JsonProcessingException;

@Ignore
public final class BindingControllerTest extends AbstractControllerTest {

	@Before
	public void loadService() throws Exception {
	
		ResultActions results = this.mockMvc.perform( put("/services").contentType(MediaType
                .APPLICATION_JSON).content(servicePayload().getBytes()).header("X-Broker-Api-Version", "2.3"));
		
		results.andExpect(content().string("success: yes"));
		
		System.out.println("Post Service Status is " + status());
   	 	System.out.println("Post Service content is " + content());
   	 	
   	 	Thread.sleep(300000);
   	 	
   	 	results = this.mockMvc.perform(put("/v2/service_instances/0").content(serviceInstancePayload()).contentType(MediaType
             .APPLICATION_JSON));
   	 	
   	 	results = this.mockMvc.perform(get("/v2/catalog"));
   	 	System.out.println("Catalog content is " + content());
   	 
	}
	
    @Test
    public void create() throws Exception {
    	 ResultActions results = this.mockMvc.perform(put("/v2/service_instances/0/service_bindings/1").content(bindingInstancePayload()).contentType(MediaType
                .APPLICATION_JSON));
             
                results.andExpect(status().isOk())
                .andExpect(jsonPath("$.credentials.licenseKey").exists());
    }

    @Test
    public void testDelete() throws Exception {
        this.mockMvc.perform(delete("/v2/service_instances/0/service_bindings/1")
                .param("service_id", "test-service-id").param("plan_id", "test-plan-id"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").exists());
    }

    private String bindingInstancePayload() throws JsonProcessingException {
        Map<String, String> m = new HashMap<>();
        m.put("service_id", "test-service-id");
        m.put("plan_id", "test-plan-id");
        m.put("app_guid", "test-app-guid");

        String payload = this.objectMapper.writeValueAsString(m);
        return payload;
    }
    
    private String serviceInstancePayload() throws JsonProcessingException {
        Map<String, String> m = new HashMap<>();
        m.put("service_id", "test-service-id");
        m.put("plan_id", "test-plan-id");
        m.put("organization_guid", "test-org-id");
        m.put("space_guid", "test-space-id");

        String payload = this.objectMapper.writeValueAsString(m);
        return payload;
    }

    private String servicePayload() throws JsonProcessingException {
        Map<String, String> m = new HashMap<>();
        
        Service svc = new Service();
        svc.setName("test-name");
        svc.setId("test-service-id");
        svc.setDescription("test-description");
        svc.setBindable(true);
                
        Plan plan = new Plan();        
        plan.setName("test-name");
        plan.setId("test-plan-id");
        plan.setDescription("test-description");
        plan.setFree(true);
        
        Credentials creds = new Credentials();
        creds.setUri("http://test-uri");
        plan.setCredentials(creds);
        
        svc.addPlan(plan);

        String payload = this.objectMapper.writeValueAsString(new Service[] { svc });
        System.out.println("Service Payload: " + payload);
        return payload;
    }

}
