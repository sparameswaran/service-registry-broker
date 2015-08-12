package org.cf.servicebroker.repository;

import org.springframework.data.repository.*;
import org.cf.servicebroker.model.ServiceInstance;

public interface ServiceInstanceRepository  extends CrudRepository<ServiceInstance, String>{

}
