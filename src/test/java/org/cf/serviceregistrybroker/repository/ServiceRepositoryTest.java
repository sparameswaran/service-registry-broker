package org.cf.serviceregistrybroker.repository;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Arrays;

import org.cf.serviceregistrybroker.ServiceRegistryBrokerApp;
import org.cf.serviceregistrybroker.controller.registry.ServicesController;
import org.cf.serviceregistrybroker.model.ServiceDefinition;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.TestRestTemplate;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ServiceRegistryBrokerApp.class)
@WebIntegrationTest(value = "server.port=9876")
public class ServiceRepositoryTest {

	@Autowired
	ServicesController serviceRegistryController;
	
	TestRestTemplate restTemplate = new TestRestTemplate();

	@Autowired
    ApplicationContext applicationContext;

    public void printBeans() {
        System.out.println(Arrays.asList(applicationContext.getBeanDefinitionNames()));
    }
    
	@Test
	public void testFindService() {
		ResponseEntity<Object> serviceSet = serviceRegistryController.findAllServices();
		assertNotNull(serviceSet);

		ServiceDefinition s = new ServiceDefinition();
		s.setBindable(true);
		s.setDescription("delete me");
		s.setName("123");
		s.setName("test");

		ResponseEntity<String> resp = serviceRegistryController.createService(
				s);
		assertNotNull(resp);
		assertEquals(HttpStatus.OK, resp.getStatusCode());
		
		printBeans();
		System.out.println("Finished ServiceRepo test");
	}
}
