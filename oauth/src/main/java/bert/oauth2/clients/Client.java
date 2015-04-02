package bert.oauth2.clients;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonSetter;

@JsonInclude(Include.NON_EMPTY)
@JsonIgnoreProperties({ "timeToLive", "secret" })
public class Client {

	private String id;
	@JsonIgnore
	private int timeToLive;
	private String name;
	private List<String> grantTypes = new ArrayList<>();
	@JsonIgnore
	private String secret;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<String> getGrantTypes() {
		return grantTypes;
	}

	public void setGrantTypes(List<String> grantTypes) {
		this.grantTypes = grantTypes;
	}

	public String getSecret() {
		return secret;
	}

	@JsonSetter
	public void setSecret(String secret) {
		this.secret = secret;
	}

	public boolean accepts(String clientCredentials) {
		return this.secret == null || this.secret.equals(clientCredentials);
	}

	@JsonSetter
	public void setTimeToLive(int timeToLive) {
		this.timeToLive = timeToLive;
	}

	public int getTimeToLive() {
		return timeToLive;
	}
}
