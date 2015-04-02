package bert.oauth2.token;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import bert.oauth2.repository.CassandraRepository;
import bert.oauth2.repository.JsonMarshaller;
import bert.oauth2.repository.Repository;

import com.datastax.driver.core.Session;

@Configuration
public class AccessTokenRepositoryFactory {

	private static final String TABLE_NAME = "access_tokens";

	@Bean
	public Repository<AccessToken> accessTokenRepository(Session session) {
		return new CassandraRepository<>(TABLE_NAME, new JsonMarshaller<>(AccessToken.class), session);
	}

}
