package bert.oauth2.util;

import org.springframework.http.HttpStatus;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class ErrorInfo {

	private String error;
	@JsonIgnore
	private HttpStatus statusCode;
	private int code;
	private String message;

	public ErrorInfo(HttpStatus statusCode, String error, int code) {
		this(statusCode, error, code, null);
	}

	public ErrorInfo(HttpStatus statusCode, String error, int code, String message) {
		this.statusCode = statusCode;
		this.error = error;
		this.code = code;
		this.message = message;
	}

	public HttpStatus getStatusCode() {
		return statusCode;
	}

	public String getError() {
		return error;
	}

	public int getCode() {
		return code;
	}

	public String getMessage() {
		return message;
	}

}
