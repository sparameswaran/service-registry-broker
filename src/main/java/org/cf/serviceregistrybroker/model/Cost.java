/*
 * Copyright 2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.cf.serviceregistrybroker.model;

import org.springframework.util.Assert;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapKeyColumn;
import javax.persistence.Table;

@Entity
@Table(name = "cost")
@JsonIgnoreProperties({ "id", "handler", "hibernateLazyInitializer", "planMetadata", "planmetadata_id"  })
final class Cost {

	@Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private int id;
	
	@JsonBackReference
	@ManyToOne
	@JoinColumn(name="planmetadata_id", insertable = true, updatable = false)
	// Mark insertable false for compound keys, shared primary key, cascaded key
	private PlanMetadata planmetadata;
	
	public PlanMetadata getPlanmetadata() {
		return planmetadata;
	}

	public void setPlanmetadata(PlanMetadata planmetadata) {
		this.planmetadata = planmetadata;
	}

	// any "other" tags/key-value pairs    
	@ElementCollection(fetch = FetchType.LAZY)
	@MapKeyColumn(name="currency")
    @Column(name="value")
    @CollectionTable(name="cost_amounts", joinColumns=@JoinColumn(name="cost_amounts_id"))
	private Map<String,Double> amount = new HashMap<String,Double>();

    private String unit = "MONTHLY";

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setAmount(Map<String, Double> amount) {
		this.amount = amount;
	}

	public Map<String, Double> getAmount() {
        Assert.notEmpty(this.amount, "Cost must specify at least one amount");
        return this.amount;
    }

	public String getUnit() {
        Assert.notNull(this.unit, "Cost must specify a unit");
        return this.unit;
    }

	public void setAmount(String currency, Double value) {
        this.amount.put(currency, value);
    }
    
    @JsonAnySetter
    public void set(String currency, Double value) {
    	amount.put(currency, value);
    }

    void setUnit(String unit) {
        this.unit = unit;
    }
    
    public void update(Cost from) {
		if (from == null)
			return;
		
		if (from.amount != null) {
			for(String key: amount.keySet()) {
				this.set(key, amount.get(key));
			}
		}
		
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Cost other = (Cost) obj;
		if (id != other.id)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Cost [id=" + id + ", planmetadata=" + planmetadata
				+ ", amount in usd=" + amount.get("usd") + ", unit=" + unit + "]";
	}

    

}
