package org.cf.serviceregistry;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.cloud.Cloud;
import org.springframework.cloud.CloudFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;


@Configuration
@ComponentScan
@EnableAutoConfiguration
@EnableJpaRepositories
public class ServiceRegistryBrokerApp {

	@Bean
	public Cloud cloud() {
		//Cloud cloud = new CloudFactory().getCloud();
		//return cloud;

		return null;
	}

	public static void main(String[] args) {
		SpringApplication.run(ServiceRegistryBrokerApp.class, args);
	}
}
