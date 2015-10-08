package org.cf.serviceregistry.servicebroker.binding;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.cf.serviceregistry.repository.CredentialsRepository;
import org.cf.serviceregistry.repository.PlanRepository;
import org.cf.serviceregistry.repository.ServiceInstanceRepository;
import org.cf.serviceregistry.repository.ServiceRepository;
import org.cf.serviceregistry.servicebroker.model.Credentials;
import org.cf.serviceregistry.servicebroker.model.Plan;
import org.cf.serviceregistry.servicebroker.model.PlanMetadata;
import org.cf.serviceregistry.servicebroker.model.Service;
import org.cf.serviceregistry.servicebroker.model.ServiceBinding;
import org.cf.serviceregistry.servicebroker.model.ServiceInstance;
import org.cf.serviceregistry.servicebroker.model.ServiceMetadata;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Component;

@Component
public class DatabaseInitialiser  {

	@Autowired
	ServiceRepository serviceRepo;

	@Autowired
	ServiceInstanceRepository serviceInstanceRepo;

    private Service service() {

        Service svc = new Service();
        svc.setName("test-service");
        svc.setId("test-service-id");
        svc.setDescription("test-description");
        svc.setBindable(true);

        ServiceMetadata svcM = new ServiceMetadata();
        svcM.setDisplayName("test-service");
        svcM.setProviderDisplayName("test-provider");
        svcM.setId(0);
        svc.setMetadata(svcM);

        Plan plan = new Plan();
        plan.setName("test-plan");
        plan.setId("test-plan-id");
        plan.setDescription("test-description");
        plan.setFree(true);

        PlanMetadata planM = new PlanMetadata();
        planM.addBullet("basic");
        planM.setId(100);

        plan.setMetadata(planM);

        Credentials creds = new Credentials();
        creds.setUri("http://test-uri");
        creds.setId("100");
        plan.setCredentials(creds);

        svc.addPlan(plan);
        return svc;
    }

    private ServiceInstance serviceInstance() {
    	ServiceInstance svcI = new ServiceInstance();
    	svcI.setOrgGuid("test-org-id");
    	svcI.setSpaceGuid("test-space-id");
    	svcI.setId("test-service-instance-id");
    	svcI.setPlanId("test-plan-id");
    	svcI.setServiceId("test-service-id");

    	return svcI;
    }

    private ServiceBinding serviceBinding() {
    	ServiceBinding svcB = new ServiceBinding();
    	svcB.setAppGuid("test-app-guid");
    	svcB.setInstanceId("1");
    	svcB.setId("test-service-binding-instance-id");
    	svcB.setInstanceId("test-service-instance-id");
    	svcB.setPlanId("test-plan-id");
    	svcB.setServiceId("test-service-id");

    	return svcB;
    }

    @PostConstruct
    public void load() {
        // Initialise your database here: create schema, use DBUnit to load data, etc.
		serviceRepo.save(service());
		serviceInstanceRepo.save(serviceInstance());
    }


    @PreDestroy
    public void cleanup() {
    	serviceInstanceRepo.delete(serviceInstance());
		serviceRepo.delete("test-service-id");
    }
}
