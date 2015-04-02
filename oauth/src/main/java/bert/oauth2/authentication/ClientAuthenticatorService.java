package bert.oauth2.authentication;

import java.util.Base64;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;

import rx.Observable;
import bert.oauth2.clients.Client;
import bert.oauth2.repository.Repository;
import bert.oauth2.util.ErrorInfos;

@Service
public class ClientAuthenticatorService implements ClientAuthenticator {

	@Autowired
	private Repository<Client> clientRepository;

	@Override
	public Observable<Client> authenticate(HttpServletRequest request, MultiValueMap<String, String> body) {
		String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
		if (authorizationHeader != null && authorizationHeader.startsWith("Basic ")) {
			String identifier = new String(Base64.getDecoder().decode(authorizationHeader.substring(6)));
			int index = identifier.indexOf(':');
			if (index >= 0) {
				String clientId = identifier.substring(0, index);
				String clientSecret = identifier.substring(index + 1);
				return authenticate(clientId, clientSecret);
			}
		}
		return authenticate(body.getFirst("client_id"), body.getFirst("client_credentials"));
	}

	private Observable<Client> authenticate(String clientId, String clientSecret) {
		return clientRepository.retrieve(clientId).map(
		    client -> client.filter(c -> c.accepts(clientSecret)).orElseThrow(ErrorInfos::unauthorizedClient));
	}

}
