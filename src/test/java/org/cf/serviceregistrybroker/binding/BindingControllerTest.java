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

package org.cf.serviceregistrybroker.binding;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.HashMap;
import java.util.Map;

import org.cf.serviceregistrybroker.AbstractControllerTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import com.fasterxml.jackson.core.JsonProcessingException;

public final class BindingControllerTest extends AbstractControllerTest {

	@Autowired
    protected DatabaseInitialiser databaseInitialiser;
	
    @Test
    public void create() throws Exception {
    	 this.mockMvc.perform(put("/v2/service_instances/test-service-instance-id/service_bindings/test-service-binding-instance-id")
    			.content(bindingInstancePayload())
    			.contentType(MediaType.APPLICATION_JSON))
    	 		.andDo(print())
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
