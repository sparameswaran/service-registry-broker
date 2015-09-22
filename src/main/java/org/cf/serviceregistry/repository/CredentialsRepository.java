package org.cf.serviceregistry.repository;

import org.cf.serviceregistry.servicebroker.model.Credentials;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

@Component
public interface CredentialsRepository extends
		CrudRepository<Credentials, String> {

}
