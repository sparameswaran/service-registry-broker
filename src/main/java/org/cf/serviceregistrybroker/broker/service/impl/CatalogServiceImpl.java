package org.cf.serviceregistrybroker.broker.service.impl;

import org.apache.commons.collections.IteratorUtils;
import org.apache.log4j.Logger;
import org.cf.serviceregistrybroker.broker.service.CatalogService;
import org.cf.serviceregistrybroker.model.Catalog;
import org.cf.serviceregistrybroker.model.ServiceDefinition;
import org.cf.serviceregistrybroker.repository.ServiceDefinitionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CatalogServiceImpl implements CatalogService {

	private static final Logger LOG = Logger.getLogger(CatalogServiceImpl.class);

	@Autowired
	ServiceDefinitionRepository serviceRepo;

	@Override
	public Catalog getCatalog() {		
		
		return new Catalog(IteratorUtils.toList(serviceRepo.findAll().iterator()));
	}

	@Override
	public ServiceDefinition getServiceDefinition(String id) {
		if (id == null) {
			return null;
		}

		for (ServiceDefinition sd : getCatalog().getServiceDefinitions()) {
			if (sd.getId().equals(id)) {
				return sd;
			}
		}
		return null;
	}

}