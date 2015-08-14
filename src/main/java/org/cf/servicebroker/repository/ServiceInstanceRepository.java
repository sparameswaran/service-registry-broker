package org.cf.servicebroker.repository;

import org.cf.serviceregistry.servicebroker.model.ServiceInstance;
import org.springframework.data.repository.CrudRepository;

public interface ServiceInstanceRepository extends
		CrudRepository<ServiceInstance, String> {

}
