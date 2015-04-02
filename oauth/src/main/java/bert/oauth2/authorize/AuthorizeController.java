package bert.oauth2.authorize;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

import rx.Observable;
import bert.oauth2.accounts.Account;
import bert.oauth2.authentication.ClientAuthenticator;
import bert.oauth2.authentication.UserAuthenticator;
import bert.oauth2.clients.Client;
import bert.oauth2.repository.Repository;
import bert.oauth2.util.AsyncHelper;
import bert.oauth2.util.ErrorInfo;
import bert.oauth2.util.OauthException;

@RestController
@RequestMapping("/oauth/authorize")
public class AuthorizeController {

	private static final int GRANT_CODE_TIME_TO_LIVE = 5 * 60;

	private static final int AUTHORIZE_REQUEST_TTL = 10 * 60;

	@Autowired
	private Repository<AuthorizeRequest> authorizeRequestRepository;

	@Autowired
	private UserAuthenticator userAuthenticator;

	@Autowired
	private ClientAuthenticator clientAuthenticator;

	@Autowired
	private Repository<Client> clientRepository;

	@Autowired
	private Repository<GrantCode> grantCodeRepository;

	@RequestMapping(method = RequestMethod.GET)
	public DeferredResult<ResponseEntity<Void>> authorize(@RequestParam("response_type") String responseType,
			@RequestParam("client_id") String clientId,
			@RequestParam(value = "redirect_uri", required = false) String redirectUri,
			@RequestParam(required = false) String scope, @RequestParam(required = false) String state) {
		verifyClient(clientId, redirectUri);
		AuthorizeRequest authorizeRequest = new AuthorizeRequest(clientId, redirectUri, scope, state);
		String reqId = UUID.randomUUID().toString();
		Observable<AuthorizeRequest> req = authorizeRequestRepository.store(reqId, authorizeRequest, AUTHORIZE_REQUEST_TTL);
		return AsyncHelper.asDeferredResult(req.map(r -> ResponseEntity.status(HttpStatus.FOUND)
				.header(HttpHeaders.LOCATION, "/login?reqId=" + reqId).build()));
	}

	private Observable<Void> verifyClient(String clientId, String redirectUri) {
		return clientRepository.retrieve(clientId).map(c -> {
			return null;
		});
	}

	@RequestMapping(value = "/user", method = RequestMethod.POST)
	public DeferredResult<ResponseEntity<Void>> authenticateUser(MultiValueMap<String, String> body) {
		String reqId = body.getFirst("requestId");
		String username = body.getFirst("username");
		String password = body.getFirst("password");
		Observable<Account> account = userAuthenticator.authenticate(username, password);
		Observable<AuthorizeRequest> request = account.flatMap(a -> authorizeRequestRepository.retrieve(reqId).map(
				opt -> opt.orElseThrow(this::unknownAuthorizeRequest)));
		return AsyncHelper.asDeferredResult(request.flatMap(this::grantCodeToResponseEntity));
	}

	private OauthException unknownAuthorizeRequest() {
		return new OauthException(new ErrorInfo(HttpStatus.NOT_FOUND, "Unknown authorize request", 10));
	}

	private Observable<ResponseEntity<Void>> grantCodeToResponseEntity(AuthorizeRequest r) {
		Observable<GrantCode> grantCode = newGrantCode(r);
		return grantCode.map(gc -> asResponseEntity(r.getState(), gc));
	}

	private Observable<GrantCode> newGrantCode(AuthorizeRequest req) {
		GrantCode gc = new GrantCode();
		gc.setClientId(req.getClientId());
		gc.setCode(UUID.randomUUID().toString());
		gc.setRedirectUri(req.getRedirectUri());
		return grantCodeRepository.store(gc.getCode(), gc, GRANT_CODE_TIME_TO_LIVE);
	}

	private ResponseEntity<Void> asResponseEntity(String state, GrantCode gc) {
		return ResponseEntity.status(HttpStatus.FOUND)
				.header(HttpHeaders.LOCATION, gc.getRedirectUri() + "?state=" + state + "&code=" + gc.getCode()).build();
	}

}