package org.cf.servicebroker.repository;

import org.springframework.data.repository.*;
import org.cf.serviceregistry.servicebroker.model.ServiceBinding;

public interface ServiceBindingRepository  extends CrudRepository<ServiceBinding, String>{

}
