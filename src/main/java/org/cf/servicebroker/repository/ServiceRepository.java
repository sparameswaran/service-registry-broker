package org.cf.servicebroker.repository;

import org.cf.serviceregistry.servicebroker.model.Service;
import org.springframework.data.repository.CrudRepository;

public interface ServiceRepository extends CrudRepository<Service, String> {

}
