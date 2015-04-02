package bert.oauth2.common;

import javax.servlet.http.HttpServletResponse;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import bert.oauth2.util.ErrorInfo;
import bert.oauth2.util.ErrorInfos;
import bert.oauth2.util.OauthException;

@ControllerAdvice
public class ControllerExceptionHandler {

	@ExceptionHandler(OauthException.class)
	public @ResponseBody ErrorInfo handle(OauthException ex, HttpServletResponse response) {
		response.setContentType(MediaType.APPLICATION_JSON.toString());
		response.setStatus(ex.getError().getStatusCode().value());
		ex.printStackTrace();
		return ex.getError();
	}

	@ExceptionHandler(RuntimeException.class)
	public @ResponseBody ErrorInfo handle(RuntimeException ex, HttpServletResponse response) {
		return handle(new OauthException(ErrorInfos.internalServerError(ex.getMessage())), response);
	}
}
