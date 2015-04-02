package bert.oauth2.authorize;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import bert.oauth2.repository.CassandraRepository;
import bert.oauth2.repository.JsonMarshaller;
import bert.oauth2.repository.Repository;

import com.datastax.driver.core.Session;

@Configuration
public class AuthorizeRepositoryFactory {

	@Bean
	public Repository<AuthorizeRequest> authorizeRequestRepository(Session session) {
		return new CassandraRepository<>("authorize_requests", new JsonMarshaller<>(AuthorizeRequest.class), session);
	}

	@Bean
	public Repository<GrantCode> grantCodeRepository(Session session) {
		return new CassandraRepository<>("grant_codes", new JsonMarshaller<>(GrantCode.class), session);
	}

}
