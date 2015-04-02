package bert.oauth2.repository;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonMarshaller<T> implements Marshaller<T> {

	private final Class<T> clazz;
	private final ObjectMapper objectMapper = new ObjectMapper();

	public JsonMarshaller(Class<T> clazz) {
		this.clazz = clazz;
	}

	@Override
	public T fromString(String value) {
		try {
			return objectMapper.readValue(value, clazz);
		} catch (IOException e) {
			throw new IllegalArgumentException(value, e);
		}
	}

	@Override
	public String toString(T instance) {
		try {
			return objectMapper.writeValueAsString(instance);
		} catch (JsonProcessingException e) {
			throw new IllegalArgumentException(e);
		}
	}

}
