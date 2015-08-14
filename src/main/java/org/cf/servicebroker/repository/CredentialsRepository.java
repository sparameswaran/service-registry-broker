package org.cf.servicebroker.repository;

import org.cf.serviceregistry.servicebroker.model.Credentials;
import org.springframework.data.repository.CrudRepository;

public interface CredentialsRepository extends
		CrudRepository<Credentials, String> {

}
