package org.cf.servicebroker.repository;

import org.springframework.data.repository.*;
import org.cf.servicebroker.model.Service;

public interface ServiceRepository extends CrudRepository<Service, String>{

}
