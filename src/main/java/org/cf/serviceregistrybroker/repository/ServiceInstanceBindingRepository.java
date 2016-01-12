package org.cf.serviceregistrybroker.repository;

import org.cf.serviceregistrybroker.model.ServiceInstanceBinding;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;

@Component
public interface ServiceInstanceBindingRepository extends
		CrudRepository<ServiceInstanceBinding, String> {

}
