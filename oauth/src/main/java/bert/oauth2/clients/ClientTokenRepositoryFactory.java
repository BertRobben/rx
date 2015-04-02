package bert.oauth2.clients;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import bert.oauth2.repository.CassandraRepository;
import bert.oauth2.repository.JsonMarshaller;
import bert.oauth2.repository.Repository;

import com.datastax.driver.core.Session;

@Configuration
public class ClientTokenRepositoryFactory {

	private static final String TABLE_NAME = "clients";

	@Bean
	public Repository<Client> clientRepository(Session session) {
		return new CassandraRepository<>(TABLE_NAME, new JsonMarshaller<>(Client.class), session);
	}

}
