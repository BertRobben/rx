package bert.oauth2.token;

public class PostTokenBody {

	private String client_id;
	private String client_credentials;
	private String grant_type;

	public String getClientId() {
		return client_id;
	}

	public String getClientCredentials() {
		return client_credentials;
	}

	public String getGrantType() {
		return grant_type;
	}
}
