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

package org.cf.serviceregistry.servicebroker.catalog;

import static org.junit.Assert.assertEquals;

import java.net.URI;
import java.util.Map;

import org.cf.serviceregistry.AbstractSerializationTest;
import org.cf.serviceregistry.servicebroker.model.ServiceMetadata;

public final class ServiceMetadataTest extends AbstractSerializationTest<ServiceMetadata> {

    @Override
    protected void assertContents(Map m) {
        assertEquals("test-display-name", m.get("displayName"));
        assertEquals("http://test.image.url", m.get("imageUrl"));
        assertEquals("test-long-description", m.get("longDescription"));
        assertEquals("test-provider-display-name", m.get("providerDisplayName"));
        assertEquals("http://test.documentation.url", m.get("documentationUrl"));
        assertEquals("http://test.support.url", m.get("supportUrl"));
    }

    @Override
    protected ServiceMetadata getInstance() {
        ServiceMetadata svcM = new ServiceMetadata();
        svcM.setDisplayName("test-display-name");
        svcM.setImageUrl(URI.create("http://test.image.url"));
        svcM.setLongDescription("test-long-description");
        svcM.setProviderDisplayName("test-provider-display-name");
        svcM.setDocumentationUrl(URI.create("http://test.documentation.url"));
        svcM.setSupportUrl(URI.create("http://test.support.url"));

        return svcM;
    }

}
