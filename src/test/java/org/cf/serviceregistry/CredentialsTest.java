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

package org.cf.serviceregistry;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.Map;

import org.cf.serviceregistry.servicebroker.model.Credentials;

public final class CredentialsTest extends AbstractSerializationTest<Credentials> {

    @Override
    protected void assertContents(Map m) throws IOException {
        assertEquals("http://test-uri", m.get("uri"));
    }

    @Override
    protected Credentials getInstance() {
        Credentials creds = new Credentials();
        creds.setUri("http://test-uri");
        return creds;
    }

}
