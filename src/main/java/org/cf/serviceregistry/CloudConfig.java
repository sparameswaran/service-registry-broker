package org.cf.serviceregistry;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.cf.serviceregistry.servicebroker.model.Service;
import org.hibernate.dialect.MySQL5Dialect;
import org.hibernate.jpa.HibernatePersistenceProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;

@Configuration
@Profile("cloud")
@EnableJpaRepositories("org.cf.servicebroker.repository")
public class CloudConfig {

	@Bean(name = "entityManagerFactory")
	public LocalContainerEntityManagerFactoryBean entityManagerFactory(
			DataSource dataSource) {
		return createEntityManagerFactoryBean(dataSource,
				MySQL5Dialect.class.getName());
	}

	@Bean(name = "transactionManager")
	public JpaTransactionManager transactionManager(
			EntityManagerFactory entityManagerFactory) {
		return new JpaTransactionManager(entityManagerFactory);
	}

	protected LocalContainerEntityManagerFactoryBean createEntityManagerFactoryBean(
			DataSource dataSource, String dialectClassName) {
		Map<String, String> properties = new HashMap<String, String>();
		properties.put(org.hibernate.cfg.Environment.HBM2DDL_AUTO, "create");
		properties.put(org.hibernate.cfg.Environment.DIALECT, dialectClassName);
		properties.put(org.hibernate.cfg.Environment.SHOW_SQL, "true");
		properties.put(org.hibernate.cfg.Environment.HBM2DDL_IMPORT_FILES,
				"import.sql");

		LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
		em.setDataSource(dataSource);
		em.setPackagesToScan(Service.class.getPackage().getName());
		em.setPersistenceProvider(new HibernatePersistenceProvider());
		em.setJpaPropertyMap(properties);
		return em;
	}

}
