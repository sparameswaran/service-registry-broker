package org.cf.serviceregistry;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.Filter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.boot.context.embedded.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.WebUtils;

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
    /*
    @Configuration
	@Order(SecurityProperties.ACCESS_OVERRIDE_ORDER)
	protected static class SecurityConfiguration extends WebSecurityConfigurerAdapter {
		@Override
		protected void configure(HttpSecurity http) throws Exception {
			http.httpBasic().and().authorizeRequests()
					.antMatchers("/index.html", "/home.html", "/login.html", "/").permitAll().anyRequest()
					.authenticated();
					//.csrfTokenRepository(csrfTokenRepository()).and()
					//.addFilterAfter(csrfHeaderFilter(), CsrfFilter.class);
		}
		
	}
	*/

}
