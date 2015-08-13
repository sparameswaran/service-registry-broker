package org.cf.servicebroker.repository;

import org.springframework.data.repository.*;
import org.cf.serviceregistry.servicebroker.model.Service;

public interface ServiceRepository extends CrudRepository<Service, String>{

}
