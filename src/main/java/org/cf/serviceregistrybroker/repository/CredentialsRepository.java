package org.cf.serviceregistrybroker.repository;

import java.util.List;

import org.cf.serviceregistrybroker.model.Credentials;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

@Component
public interface CredentialsRepository extends
		CrudRepository<Credentials, String> {

}
