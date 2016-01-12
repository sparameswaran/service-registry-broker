package org.cf.serviceregistrybroker.repository;

import java.util.List;
import java.util.Optional;

import org.cf.serviceregistrybroker.model.ServiceDefinition;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Component;

@Component
public interface ServiceDefinitionRepository extends CrudRepository<ServiceDefinition, String> {
	
	@Query("SELECT s FROM ServiceDefinition s where s.name = :name")
	Optional<ServiceDefinition> findByServiceName(@Param("name") String name);
	
	@Query("SELECT s FROM ServiceDefinition s where s.name = :name OR s.id = :name")
	Optional<ServiceDefinition> findByServiceIdOrName(@Param("name") String name);
	
	@Query("SELECT s.name FROM ServiceDefinition s where s.name LIKE CONCAT('%',:name,'%')")
	List<String> findServiceContainingName(@Param("name") String startName);
	
	@Query("SELECT s.name FROM ServiceDefinition s where s.metadata.providerDisplayName LIKE CONCAT('%',:name,'%')")
	List<String> findServiceContainingProviderName(@Param("name") String name);
}
