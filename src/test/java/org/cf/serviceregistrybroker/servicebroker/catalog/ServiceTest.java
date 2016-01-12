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

package org.cf.serviceregistrybroker.servicebroker.catalog;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;

import org.cf.serviceregistrybroker.AbstractSerializationTest;
import org.cf.serviceregistrybroker.model.ServiceDefinition;

public final class ServiceTest extends AbstractSerializationTest<ServiceDefinition> {

    @Override
    protected void assertContents(Map m) throws IOException {
        assertEquals("test-name", m.get("name"));
        assertEquals(getId().toString(), m.get("id"));
        assertEquals("test-description", m.get("description"));
        assertTrue((Boolean) m.get("bindable"));
        assertNull(m.get("tags"));
        assertNull(m.get("metadata"));
        assertNull(m.get("requires"));
        assertEquals(new ArrayList(), m.get("plans"));
        //assertEquals(roundTrip(getDashboardClient()), m.get("dashboard_client"));
    }

    @Override
    protected ServiceDefinition getInstance() {
        // @formatter:off
        ServiceDefinition svc = new ServiceDefinition();
        svc.setName("test-name");
        svc.setId("" + getId());
        svc.setDescription("test-description");
        svc.setBindable(true);
        return svc;
        // @formatter:on
    }

    public UUID getId() {
        return UUID.nameUUIDFromBytes(new byte[0]);
    }

    /*
    public DashboardClient getDashboardClient() {
        return new DashboardClient(null)
                .id("test-id")
                .secret("test-secret")
                .redirectUri(URI.create("http://test.redirect.uri"));
    }
    */

}
