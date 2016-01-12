package org.cf.serviceregistrybroker.repository;

import org.cf.serviceregistrybroker.model.ServiceInstance;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

@Component
public interface ServiceInstanceRepository extends
		CrudRepository<ServiceInstance, String> {

}
