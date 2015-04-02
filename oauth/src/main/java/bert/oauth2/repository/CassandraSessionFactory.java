package bert.oauth2.repository;

import javax.annotation.PreDestroy;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Session;

@Configuration
public class CassandraSessionFactory {

	private static final String KEYSPACE = "oauth";
	private Cluster cluster;

	@Bean
	public Session session() {
		cluster = Cluster.builder().addContactPoint("localhost").build();
		return cluster.connect(KEYSPACE);
	}

	@PreDestroy
	public void closeCluster() {
		cluster.close();
	}
}
