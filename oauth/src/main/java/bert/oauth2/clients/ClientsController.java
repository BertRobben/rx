package bert.oauth2.clients;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

import rx.Observable;
import rx.functions.Func1;
import bert.oauth2.authentication.TokenAuthenticator;
import bert.oauth2.repository.Repository;
import bert.oauth2.token.AccessToken;
import bert.oauth2.util.AsyncHelper;
import bert.oauth2.util.ErrorInfo;
import bert.oauth2.util.ErrorInfos;
import bert.oauth2.util.OauthErrors;
import bert.oauth2.util.OauthException;

import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
@RequestMapping("/clients")
public class ClientsController {

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private Repository<Client> clientRepository;

	@Autowired
	private TokenAuthenticator tokenAuthenticator;

	@RequestMapping(method = RequestMethod.GET)
	public DeferredResult<List<Client>> getAllClients(HttpServletRequest request) {
		return withScope(request, "admin:manage_clients", token -> clientRepository.retrieveAll().toList());
	}

	private <T> DeferredResult<T> withScope(HttpServletRequest request, String scope, Func1<AccessToken, Observable<T>> f) {
		return AsyncHelper.asDeferredResult(checkScope(request, scope).flatMap(f));
	}

	private Observable<AccessToken> checkScope(HttpServletRequest request, String scope) {
		return tokenAuthenticator.authenticate(request, scope);
	}

	@RequestMapping(method = RequestMethod.POST)
	public DeferredResult<ResponseEntity<Client>> createClient(@RequestBody Client client, HttpServletRequest request) {
		if (client.getId() == null) {
			client.setId(UUID.randomUUID().toString());
		}
		return withScope(
		    request,
		    "admin:manage_clients",
		    token -> clientRepository.store(client.getId(), client, client.getTimeToLive()).map(
		        c -> new ResponseEntity<>(c, HttpStatus.CREATED)));
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public DeferredResult<ResponseEntity<Client>> getClient(@PathVariable String id, HttpServletRequest request) {
		return withScope(request, "admin:manage_clients", token -> clientRepository.retrieve(id)
		    .map(this::asResponseEntity));
	}

	private OauthException invalidClient() {
		return new OauthException(new ErrorInfo(HttpStatus.NOT_FOUND, OauthErrors.INVALID_CLIENT, 7));
	}

	private ResponseEntity<Client> asResponseEntity(Optional<Client> client) {
		// don't inline this or you'll get NPE's in the eclipse editor
		Client c = client.orElseThrow(this::invalidClient);
		return new ResponseEntity<Client>(c, HttpStatus.OK);
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.PATCH)
	public DeferredResult<Client> patchClient(@PathVariable String id, @RequestBody String body,
	    HttpServletRequest request) {
		return withScope(
		    request,
		    "admin:manage_clients",
		    token -> retrieveClient(id).flatMap(
		        client -> clientRepository.store(id, patch(client, body), client.getTimeToLive())));
	}

	private Observable<Client> retrieveClient(String id) {
		return clientRepository.retrieve(id).map(optClient -> {
			if (optClient.isPresent()) {
				return optClient.get();
			}
			throw invalidClient();
		});
	}

	private Client patch(Client client, String body) {
		try {
			return objectMapper.readerForUpdating(client).readValue(body);
		} catch (IOException e) {
			throw new OauthException(ErrorInfos.malformedBody(), e);
		}
	}
}