package bert.oauth2.authentication;

import javax.servlet.http.HttpServletRequest;

import org.springframework.util.MultiValueMap;

import rx.Observable;
import bert.oauth2.clients.Client;

public interface ClientAuthenticator {

	Observable<Client> authenticate(HttpServletRequest request, MultiValueMap<String, String> body);

}
