package org.cf.servicebroker.repository;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.cf.servicebroker.model.Service;
import org.cf.serviceregistry.ServiceRegistryBrokerApp;
import org.cf.serviceregistry.controller.ServiceRegistryController;
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
public class ServiceRepositoryTest {

	@Autowired
	ServiceRegistryController serviceRegistryController;

	TestRestTemplate restTemplate = new TestRestTemplate();

	@Test
	public void testFindService() {
		Service s = new Service();
		s.setBindable(true);
		s.setDescription("delete me");
		s.setId("123");
		s.setName("test");

		ResponseEntity<String> resp = serviceRegistryController.create(s.getId(), s);
		assertNotNull(resp);
		assertEquals(HttpStatus.OK, resp.getStatusCode());
	}
}
