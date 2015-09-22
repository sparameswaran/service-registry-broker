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

package org.cf.serviceregistry.servicebroker.binding;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.HashMap;
import java.util.Map;

import org.cf.serviceregistry.AbstractControllerTest;
import org.cf.serviceregistry.repository.CredentialsRepository;
import org.cf.serviceregistry.repository.PlanRepository;
import org.cf.serviceregistry.repository.ServiceBindingRepository;
import org.cf.serviceregistry.repository.ServiceInstanceRepository;
import org.cf.serviceregistry.repository.ServiceRepository;
import org.cf.serviceregistry.servicebroker.model.Credentials;
import org.cf.serviceregistry.servicebroker.model.Plan;
import org.cf.serviceregistry.servicebroker.model.PlanMetadata;
import org.cf.serviceregistry.servicebroker.model.Service;
import org.cf.serviceregistry.servicebroker.model.ServiceBinding;
import org.cf.serviceregistry.servicebroker.model.ServiceInstance;
import org.cf.serviceregistry.servicebroker.model.ServiceMetadata;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import com.fasterxml.jackson.core.JsonProcessingException;

public final class BindingControllerTest extends AbstractControllerTest {

	@Autowired
	// Let the DatabaseInitializer load the test service and plan
    protected DatabaseInitialiser databaseInitialiser;
	
    @Test
    public void create() throws Exception {
    	 this.mockMvc.perform(put("/v2/service_instances/test-service-instance-id/service_bindings/test-service-binding-instance-id")
    			.content(bindingInstancePayload())
    			.contentType(MediaType.APPLICATION_JSON))
    			.andExpect(status().isOk())
    			.andExpect(jsonPath("$.credentials.uri").exists());
    }

    @Test
    public void testDelete() throws Exception {
        this.mockMvc.perform(delete("/v2/service_instances/test-service-instance-id/service_bindings/test-service-binding-instance-id")
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
}
