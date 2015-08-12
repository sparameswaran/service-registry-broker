package org.cf.servicebroker.repository;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Map;

import org.cf.servicebroker.model.Service;
import org.cf.serviceregistry.ServiceRegistryBrokerApp;
import org.cf.serviceregistry.controller.ServiceRegistryController;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.TestRestTemplate;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ServiceRegistryBrokerApp.class)
@WebIntegrationTest(value = "server.port=9876")
@Ignore
public class ServiceRepositoryTest {

	@Autowired
	ServiceRegistryController serviceRegistryController;

	TestRestTemplate restTemplate = new TestRestTemplate();

	@Test
	public void testFindService() {
		Map<String, Iterable<Service>> m = serviceRegistryController.services();
		assertNotNull(m);

		Service s = new Service();
		s.setBindable(true);
		s.setDescription("delete me");
		s.setId("123");
		s.setName("test");

		ResponseEntity<String> resp = serviceRegistryController.create(
				s.getId(), s);
		assertNotNull(resp);
		assertEquals(HttpStatus.OK, resp.getStatusCode());
	}
}
