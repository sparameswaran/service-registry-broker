package org.cf.serviceregistry.repository;

import java.util.List;
import java.util.Optional;

import org.cf.serviceregistry.servicebroker.model.Service;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Component;

@Component
public interface ServiceRepository extends CrudRepository<Service, String> {
	
	@Query("SELECT s FROM Service s where s.name = :name")
	Optional<Service> findByServiceName(@Param("name") String name);
	
	@Query("SELECT s.name FROM Service s where s.name LIKE CONCAT('%',:name,'%')")
	List<String> findServiceContainingName(@Param("name") String startName);
	
	@Query("SELECT s.name FROM Service s where s.metadata.providerDisplayName LIKE CONCAT('%',:name,'%')")
	List<String> findServiceContainingProviderName(@Param("name") String name);
}
