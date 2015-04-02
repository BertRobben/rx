package bert.oauth2.authorize;

public class AuthorizeRequest {

	private String clientId;
	private String redirectUri;
	private String scope;
	private String state;

	public AuthorizeRequest(String clientId, String redirectUri, String scope, String state) {
		this.clientId = clientId;
		this.redirectUri = redirectUri;
		this.scope = scope;
		this.state = state;
	}

	public String getClientId() {
		return clientId;
	}

	public String getRedirectUri() {
		return redirectUri;
	}

	public String getScope() {
		return scope;
	}

	public String getState() {
		return state;
	}

}
