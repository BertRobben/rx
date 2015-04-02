package bert.oauth2.util;

import org.springframework.http.HttpStatus;

public class ErrorInfos {

	public static ErrorInfo malformedBody() {
		return new ErrorInfo(HttpStatus.BAD_REQUEST, OauthErrors.INVALID_REQUEST, 1);
	}

	public static ErrorInfo internalServerError(String msg) {
		return new ErrorInfo(HttpStatus.INTERNAL_SERVER_ERROR, "internal_server_error", 2, msg);
	}

	public static OauthException unauthorizedClient() {
		return new OauthException(new ErrorInfo(HttpStatus.UNAUTHORIZED, OauthErrors.UNAUTHORIZED_CLIENT, 3));
	}

	public static OauthException unauthorized() {
		return new OauthException(new ErrorInfo(HttpStatus.UNAUTHORIZED, OauthErrors.ACCESS_DENIED, 8));
	}

}
