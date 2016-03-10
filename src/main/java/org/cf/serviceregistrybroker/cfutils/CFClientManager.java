package org.cf.serviceregistrybroker.cfutils;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.cloudfoundry.client.CloudFoundryClient;
import org.cloudfoundry.client.v2.domains.DomainResource;
import org.cloudfoundry.client.v2.domains.GetDomainRequest;
import org.cloudfoundry.client.v2.domains.ListDomainsRequest;
import org.cloudfoundry.client.v2.organizations.ListOrganizationsRequest;
import org.cloudfoundry.client.v2.servicebrokers.ListServiceBrokersRequest;
import org.cloudfoundry.client.v2.servicebrokers.ServiceBrokerResource;
import org.cloudfoundry.client.v2.servicebrokers.UpdateServiceBrokerRequest;
import org.cloudfoundry.client.v2.serviceinstances.CreateServiceInstanceRequest;
import org.cloudfoundry.client.v2.serviceinstances.ServiceInstanceEntity;
import org.cloudfoundry.client.v2.serviceplans.ListServicePlansRequest;
import org.cloudfoundry.client.v2.serviceplans.ServicePlanResource;
import org.cloudfoundry.client.v2.serviceplans.UpdateServicePlanRequest;
import org.cloudfoundry.client.v2.serviceplanvisibilities.CreateServicePlanVisibilityRequest;
import org.cloudfoundry.client.v2.serviceplanvisibilities.CreateServicePlanVisibilityRequest.CreateServicePlanVisibilityRequestBuilder;
import org.cloudfoundry.client.v2.services.GetServiceRequest;
import org.cloudfoundry.client.v2.services.GetServiceResponse;
import org.cloudfoundry.client.v2.services.ListServiceServicePlansRequest;
import org.cloudfoundry.client.v2.services.ListServicesRequest;
import org.cloudfoundry.client.v2.services.ListServicesRequest.ListServicesRequestBuilder;
import org.cloudfoundry.client.v2.services.ServiceResource;
import org.cloudfoundry.client.v2.spaces.ListSpacesRequest;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class CFClientManager {

    private static final Logger log = Logger.getLogger(CFClientManager.class);

    public static Mono<String> requestDomainId(CloudFoundryClient cloudFoundryClient, String domain) {
    	/*
    	if (domain != null) {
    	return Mono
                .just(domain)
                .then(domain2 -> requestDomain(cloudFoundryClient, domain2))
                .as(Stream::from)                                               
                .switchIfEmpty(requestFirstDomain(cloudFoundryClient))
                .log("stream.swithcIfEmptyOnDomain")
                .single()                                                       
                .map(resource -> resource.getMetadata().getId());
    	} 
    	*/
    	return
    		requestFirstDomain(cloudFoundryClient)
    		.map(resource -> resource.getMetadata().getId());
    }
    
    private static Mono<DomainResource> requestFirstDomain(CloudFoundryClient cloudFoundryClient) {
        ListDomainsRequest request = ListDomainsRequest.builder()
                .build();

        return cloudFoundryClient.domains().list(request)
        		.as(Flux::from)
    			.flatMap(resource -> Flux.fromIterable(resource.getResources()))
                .log("stream.requestFirstDomain")
                .next();
    }
    
    public static Mono<String> requestDomainName(CloudFoundryClient cloudFoundryClient, String domainId) {
    	
    	GetDomainRequest request = GetDomainRequest.builder()
                    .domainId(domainId)
                    .build();
            
            return cloudFoundryClient.domains().get(request)
                    .map(response -> response.getEntity().getName());
    }
    
    public static Mono<String> requestOrganizationId(CloudFoundryClient cloudFoundryClient, String organization) {
        ListOrganizationsRequest request = ListOrganizationsRequest.builder()
                .name(organization)
                .build();

        return cloudFoundryClient.organizations().list(request)
                .flatMap(response -> Flux.fromIterable(response.getResources()))
                .as(Flux::from)
                .single()
                .map(resource -> resource.getMetadata().getId());
     
        /*
        return cloudFoundryClient.organizations().list(request)
			        .then(response -> Stream
			                .fromIterable(response.getResources())
			                .single())
			        .map(resource -> resource.getMetadata().getId())
			        .log("stream.prePromise")
			        .to(Promise.prepare())
			        .log("stream.postPromise");
		*/
    }
    
    public static Mono<String> requestSpaceId(CloudFoundryClient cloudFoundryClient, String organizationId, String space) {
        
		ListSpacesRequest request = ListSpacesRequest.builder()
		        .organizationId(organizationId)
		        .name(space)
		        .build();
		
		return cloudFoundryClient.spaces().list(request)
		        .flatMap(response -> Flux.fromIterable(response.getResources()))
		        .as(Flux::from)
		        .single()
		        .map(resource -> resource.getMetadata().getId());
    }

    public static Mono<String> requestSpaceId(CloudFoundryClient cloudFoundryClient, Mono<String> organizationId, String space) {
        return organizationId
                .then(organizationId2 -> {
                    ListSpacesRequest request = ListSpacesRequest.builder()
                            .organizationId(organizationId2)
                            .name(space)
                            .build();

                    return cloudFoundryClient.spaces().list(request)
                            .flatMap(response -> Flux.fromIterable(response.getResources()))
                            .as(Flux::from)
                            .single()
                            .map(resource -> resource.getMetadata().getId());
                });
    }
    
    public static Mono<ServiceInstanceEntity> requestServiceInstanceEntity(CloudFoundryClient cloudFoundryClient, String serviceName, Mono<String> spaceId, Mono<String> servicePlanId) {
		return Mono.when(spaceId, servicePlanId)
				.then(tuple -> {
				
					 String spaceId1 = tuple.t1;
					 String servicePlanId1 = tuple.t2;
				   					
					return cloudFoundryClient.serviceInstances()
					.create( CreateServiceInstanceRequest.builder()
							.name(serviceName)
							.servicePlanId(servicePlanId1)
							.spaceId(spaceId1)
							.build())
							.map(resource -> resource.getEntity())
							.log("stream.requestServiceInstance");
					}
				);
	}
	
	public static Mono<String> requestServiceInstance(CloudFoundryClient cloudFoundryClient, String serviceName, Mono<String> spaceId, Mono<String> servicePlanId) {
		return Mono.when(spaceId, servicePlanId)
				.then(tuple -> {
				
					 String spaceId1 = tuple.t1;
					 String servicePlanId1 = tuple.t2;
				   					
					return cloudFoundryClient.serviceInstances()
					.create( CreateServiceInstanceRequest.builder()
							.name(serviceName)
							.servicePlanId(servicePlanId1)
							.spaceId(spaceId1)
							.build())
							.map(resource -> resource.getMetadata().getId())
							.log("stream.requestServiceInstance");
					}
				);
	}
	
	/*
	private static Mono<String> requestServicePlanId(CloudFoundryClient cloudFoundryClient, Mono<String> serviceId) {
		return serviceId
				.then( serviceIdString ->					
						cloudFoundryClient.servicePlans()
							.list( ListServicePlansRequest.builder()
								.serviceId(serviceIdString)
								.build())
							.as(Flux::from)
							.log("stream.requestServicePlanId")
							.flatMap(resource -> Flux.fromIterable(resource.getResources()))
							.log("stream.requestServicePlanIdFlatMap")
							.next()
							.map(resource -> resource.getMetadata().getId())
							.log("stream.requestServicePlanIdResourceNext")
				);			
	}
	*/
	
	public static Mono<GetServiceResponse> requestService(CloudFoundryClient cloudFoundryClient, Mono<String> serviceId) {
		return serviceId
				.then( serviceIdString ->					
						cloudFoundryClient.services()
							.get( GetServiceRequest.builder()
								.serviceId(serviceIdString)
								.build())
							.log("stream.requestService")
				);			
	}
	
	public static Mono<String> requestServiceId(CloudFoundryClient cloudFoundryClient, String serviceLabel) {
		return cloudFoundryClient.services()
					.list( ListServicesRequest.builder()
						.label(serviceLabel)
						.build())
					.as(Flux::from)
					.log("stream.requestServiceId")	
					.flatMap(resource -> Flux.fromIterable(resource.getResources()))
					.log("stream.requestServiceIdFlatMap")
					.next()
					.map(resource -> resource.getMetadata().getId())
					.log("stream.requestServiceIdResourceNext");			
	}
	
	public static Mono<List<ServiceBrokerResource>> requestServiceBrokers(CloudFoundryClient cloudFoundryClient) {
		return cloudFoundryClient.serviceBrokers()
					.list( ListServiceBrokersRequest.builder()
						.build())
					.flatMap(resource -> Flux.fromIterable(resource.getResources()))
					.toList()
					.log("stream.requestServiceBrokers");			
	}
	
	public static Mono<Void> requestUpdateServiceBroker(CloudFoundryClient cloudFoundryClient, String serviceBrokerName,
											String serviceBrokerId, String authenticationUsername, 
											String authenticationPassword, String brokerUrl) {
		return cloudFoundryClient.serviceBrokers()
					.update( UpdateServiceBrokerRequest.builder()
						.authenticationUsername(authenticationUsername)
						.authenticationPassword(authenticationPassword)
						.brokerUrl(brokerUrl)
						.name(serviceBrokerName)
						.serviceBrokerId(serviceBrokerId)
						.build())
					.log("stream.requestUpdateServiceBroker")
					.after();			
	}
	
	public static Mono<Void> requestEnableAccessToServicePlan(CloudFoundryClient cloudFoundryClient, 
			String servicePlanId, String orgId) {
		
		CreateServicePlanVisibilityRequestBuilder builder = CreateServicePlanVisibilityRequest.builder();
		builder.servicePlanId(servicePlanId);		
		builder.organizationId(orgId);
		
		return cloudFoundryClient.servicePlanVisibilities()
					.create( builder.build() )
					.log("stream.requestEnableAccessToServicePlan")
					.after();			
	}
	
	public static Mono<List<ServiceResource>> requestListServices(CloudFoundryClient cloudFoundryClient, 
			String serviceBrokerId, String serviceLabel) {
		
		ListServicesRequestBuilder listServicesRequestBuilder = ListServicesRequest.builder();
		listServicesRequestBuilder.serviceBrokerId(serviceBrokerId);
		
		if (serviceLabel != null) {
			listServicesRequestBuilder.label(serviceLabel);
		}
		
		return cloudFoundryClient.services()
					.list( listServicesRequestBuilder.build())
					.log("stream.requestListServices")		
					.flatMap(resource -> Flux.fromIterable(resource.getResources()))
					.toList()
					.log("stream.requestListServicesResponse");			
	}
	
	public static Mono<List<ServicePlanResource>> requestListServicePlans(CloudFoundryClient cloudFoundryClient, 
			String serviceBrokerId, String serviceId) {
		
		return cloudFoundryClient.servicePlans()
					.list( ListServicePlansRequest.builder()
								.serviceBrokerId(serviceBrokerId)
								.build())
					.log("stream.requestListServicePlans")		
					.flatMap(resource -> Flux.fromIterable(resource.getResources()))
					.toList()
					.log("stream.requestListServicePlansResponse");			
	}
	
	public static Mono<List<ServicePlanResource>> requestListServiceServicePlans(CloudFoundryClient cloudFoundryClient, 
			String serviceBrokerId, String serviceId) {
		
		log.info("Trying to retrieve service plans with Service Broker id: " + serviceBrokerId + " and serviceId: " + serviceId);
		return cloudFoundryClient.services()
					.listServicePlans( ListServiceServicePlansRequest.builder()
								.serviceId(serviceId) // This does not match the known service id...
								//.serviceBrokerId(serviceBrokerId)
								.build())
					.log("stream.requestListServiceServicePlans")		
					.flatMap(resource -> Flux.fromIterable(resource.getResources()))
					.toList()
					.log("stream.requestListServiceServicePlansResponse");			
	}
	
	
	
	public static Mono<Void> requestPublicizeServicePlan(CloudFoundryClient cloudFoundryClient, 
			String servicePlanId, boolean isVisible) {
		
		return cloudFoundryClient.servicePlans()
					.update( UpdateServicePlanRequest.builder()
								.servicePlanId(servicePlanId)
								.publiclyVisible(isVisible)
								.build())
					.log("stream.requestPublicizeService")
					.after();			
	}
	
}
