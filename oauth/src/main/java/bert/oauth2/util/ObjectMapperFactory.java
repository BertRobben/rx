package bert.oauth2.util;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.ObjectMapper;

@Configuration
public class ObjectMapperFactory {

	@Bean
	public ObjectMapper objectMapper() {
		return new ObjectMapper();
	}
}
