package org.cf.serviceregistry.repository;

import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.cf.serviceregistry.servicebroker.model.Plan;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Component;

@Component
public interface PlanRepository extends CrudRepository<Plan, String> {
	@Transactional
	Plan save(Plan plan);
	
	@Query("SELECT p FROM Plan p where p.name = :name")
	Optional<Plan> findByPlanName(@Param("name") String name);
	
	@Query("SELECT p FROM Plan p where p.name = :name and p.service.id = :service_id")
	Optional<Plan> findByPlanNameAndServiceId(@Param("name") String name, @Param("service_id") String service_id);
	
	@Query("SELECT p.credentials.id FROM Plan p where p.id = :plan_id")
	Integer getCredentialIdFromPlanId(@Param("plan_id") String plan_id);
}
