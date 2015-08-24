package org.cf.servicebroker.repository;

import javax.transaction.Transactional;

import org.cf.serviceregistry.servicebroker.model.Plan;
import org.cf.serviceregistry.servicebroker.model.PlanPk;
import org.cf.serviceregistry.servicebroker.model.Service;
import org.springframework.data.repository.CrudRepository;

public interface PlanRepository extends CrudRepository<Plan, PlanPk> {
	@Transactional
	Plan save(Plan plan);
}
