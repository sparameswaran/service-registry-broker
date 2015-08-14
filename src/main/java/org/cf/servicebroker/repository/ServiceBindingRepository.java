package org.cf.servicebroker.repository;

import org.cf.serviceregistry.servicebroker.model.ServiceBinding;
import org.springframework.data.repository.CrudRepository;

public interface ServiceBindingRepository extends
		CrudRepository<ServiceBinding, String> {

}
