package bert.oauth2.repository;

public interface Marshaller<T> {

	T fromString(String value);

	String toString(T instance);

}
