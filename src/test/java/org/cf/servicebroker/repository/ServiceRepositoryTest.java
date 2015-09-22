package org.cf.servicebroker.repository;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Map;

import org.cf.serviceregistry.ServiceRegistryBrokerApp;
import org.cf.serviceregistry.controller.ServiceRegistryController;
import org.cf.serviceregistry.servicebroker.model.Service;
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
//@Ignore
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
		s.setName("123");
		s.setName("test");

		ResponseEntity<String> resp = serviceRegistryController.createService(
				s);
		assertNotNull(resp);
		assertEquals(HttpStatus.OK, resp.getStatusCode());
		
		System.out.println("Finished ServiceRepo test");
	}
}
