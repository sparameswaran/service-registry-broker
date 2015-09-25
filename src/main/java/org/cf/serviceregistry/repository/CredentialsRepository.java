package org.cf.serviceregistry.repository;

import java.util.List;

import org.cf.serviceregistry.servicebroker.model.Credentials;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

@Component
public interface CredentialsRepository extends
		CrudRepository<Credentials, Integer> {

}
