package bert.oauth2.util;

public class OauthException extends RuntimeException {

	private final ErrorInfo error;

	public OauthException(ErrorInfo error) {
		this(error, null);
	}

	public OauthException(ErrorInfo error, Throwable t) {
		super(error.getMessage(), t);
		this.error = error;
	}

	public ErrorInfo getError() {
		return error;
	}

}
