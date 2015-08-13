package org.cf.servicebroker.repository;

import org.springframework.data.repository.*;
import org.cf.serviceregistry.servicebroker.model.Credentials;

public interface CredentialsRepository extends CrudRepository<Credentials, String>{

}
