package org.cf.serviceregistry.repository;

import org.cf.serviceregistry.servicebroker.model.ServiceInstance;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;

@Component
public interface ServiceInstanceRepository extends
		CrudRepository<ServiceInstance, String> {

}
