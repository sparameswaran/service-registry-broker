package org.cf.serviceregistry.repository;

import org.cf.serviceregistry.servicebroker.model.ServiceBinding;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

@Component
public interface ServiceBindingRepository extends
		CrudRepository<ServiceBinding, String> {

}
