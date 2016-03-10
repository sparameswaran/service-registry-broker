/*
 * Copyright 2013-2015 the original author or authors.
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

package org.cf.serviceregistrybroker.cfutils;

import org.apache.log4j.Logger;
import org.cloudfoundry.client.v2.domains.ListDomainsRequest;
import org.cloudfoundry.client.v2.servicebrokers.ListServiceBrokersResponse;
import org.cloudfoundry.spring.client.SpringCloudFoundryClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

@Configuration
@EnableAutoConfiguration
@Lazy
public class CFClientUtility {

    private static final Logger log = Logger.getLogger(CFClientUtility.class);
    
    @Bean
	SpringCloudFoundryClient cloudFoundryClient(
			@Value("${cf.target}") String cfTarget,
			@Value("${cf.admin.username}") String cfUsername,
			@Value("${cf.admin.password}") String cfPassword,
			@Value("${cf.skipSslValidation:true}") Boolean skipSslValidation) {

		if (cfTarget.startsWith("https")) {
			cfTarget = cfTarget.substring(8);
		}
		
		SpringCloudFoundryClient cfClient = SpringCloudFoundryClient.builder()
				.host(cfTarget)
				.username(cfUsername)
				.password(cfPassword)
				.skipSslValidation(skipSslValidation)
				.build();
		
		log.info("SUCCESS!! Created CF client and got domains: " 
				+ cfClient.domains().list(ListDomainsRequest.builder().build()).get());
		return cfClient;
	}

}