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
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.cf.serviceregistry.AbstractSerializationTest;
import org.cf.serviceregistry.servicebroker.model.PlanMetadata;

public final class PlanMetadataTest extends AbstractSerializationTest<PlanMetadata> {

    @Override
    protected void assertContents(Map m) {    	
        assertEquals(getBullets(), m.get("bullets"));
        assertNotNull(m.get("costs"));
    }

    @Override
    protected PlanMetadata getInstance() {
    	PlanMetadata planM = new PlanMetadata();
    	List<String> bullets = new ArrayList<String>();
    	bullets.add("test-bullet-1"); 
    	bullets.add("test-bullet-2");
    	planM.setBullets(bullets);
        return planM;
    }

    private List<String> getBullets() {
    	List<String> bullets = new ArrayList<String>();
    	bullets.add("test-bullet-1"); 
    	bullets.add("test-bullet-2");
    	
        return bullets;
    }

}
