package org.cf.servicebroker.repository;

import org.springframework.data.repository.*;
import org.cf.serviceregistry.servicebroker.model.Plan;

public interface PlanRepository extends CrudRepository<Plan, String>{

}
