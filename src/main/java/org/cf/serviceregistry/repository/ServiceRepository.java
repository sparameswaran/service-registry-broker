package org.cf.serviceregistry.repository;

import java.util.Optional;

import javax.transaction.Transactional;

import org.cf.serviceregistry.servicebroker.model.Service;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Component;

@Component
public interface ServiceRepository extends CrudRepository<Service, String> {

	@Transactional
	Service save(Service service);
	
	
	@Query("SELECT s FROM Service s where s.name = :name")
	Optional<Service> findByServiceName(@Param("name") String name);
}
