package org.cf.servicebroker.repository;

import org.cf.serviceregistry.servicebroker.model.Plan;
import org.springframework.data.repository.CrudRepository;

public interface PlanRepository extends CrudRepository<Plan, String> {

}
