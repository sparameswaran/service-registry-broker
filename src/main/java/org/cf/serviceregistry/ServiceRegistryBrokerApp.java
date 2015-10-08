package org.cf.serviceregistry;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.embedded.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.cf.serviceregistry.BrokerApiVersionFilter;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootApplication
public class ServiceRegistryBrokerApp {

	public static void main(String[] args) {
		SpringApplication.run(ServiceRegistryBrokerApp.class, args);
	}
	
    @Bean
    FilterRegistrationBean brokerApiVersionFilter() {
        FilterRegistrationBean bean = new FilterRegistrationBean();
        bean.setFilter(new BrokerApiVersionFilter());
        bean.addUrlPatterns("/v2/*");

        return bean;
    }
    
    @Bean
    FilterRegistrationBean corsFilter() {
        FilterRegistrationBean bean = new FilterRegistrationBean();
        bean.setFilter(new SimpleCORSFilter());
        bean.addUrlPatterns("/*");

        return bean;
    }
    
    @Bean
    ObjectMapper objectMapper() {
        return new ObjectMapper()
                .setVisibility(PropertyAccessor.GETTER, JsonAutoDetect.Visibility.ANY)
                .setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }

}
