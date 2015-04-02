package bert.oauth2.token;

import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

import rx.Observable;
import bert.oauth2.GrantTypes;
import bert.oauth2.accounts.Account;
import bert.oauth2.authentication.ClientAuthenticator;
import bert.oauth2.authentication.UserAuthenticator;
import bert.oauth2.clients.Client;
import bert.oauth2.repository.Repository;
import bert.oauth2.util.AsyncHelper;
import bert.oauth2.util.ErrorInfo;
import bert.oauth2.util.OauthErrors;
import bert.oauth2.util.OauthException;

@RestController
@RequestMapping("/oauth/token")
public class TokenController {

	private static final int TOKEN_EXPIRATION_TIME = 1000;

	private static final ErrorInfo UNSUPPORTED_GRANT_TYPE = new ErrorInfo(HttpStatus.BAD_REQUEST,
	    OauthErrors.UNSUPPORTED_GRANT_TYPE, 5);

	@Autowired
	private Repository<AccessToken> accessTokenRepository;

	@Autowired
	private ClientAuthenticator clientAuthenticator;

	@Autowired
	private UserAuthenticator userAuthenticator;

	@Autowired
	private Repository<Client> clientRepository;

	@RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public DeferredResult<HttpEntity<AccessToken>> createToken(@RequestBody MultiValueMap<String, String> body,
	    HttpServletRequest request) {
		Observable<Client> client = clientAuthenticator.authenticate(request, body);
		return AsyncHelper.asDeferredResult(client.flatMap(c -> doCreateTokenGivenClient(body, c).map(
		    this::withTokenHeaders)));
	}

	private HttpEntity<AccessToken> withTokenHeaders(AccessToken token) {
		return ResponseEntity.ok().header(HttpHeaders.CACHE_CONTROL, "no-store").header(HttpHeaders.PRAGMA, "no-cache")
		    .body(token);
	}

	private Observable<AccessToken> doCreateTokenGivenClient(MultiValueMap<String, String> body, Client client) {
		String grantType = body.getFirst("grant_type");
		String scope = body.getFirst("scope");
		if (GrantTypes.CLIENT_CREDENTIALS.equals(grantType)) {
			AccessToken accessToken = new AccessToken(UUID.randomUUID().toString(), TOKEN_EXPIRATION_TIME);
			accessToken.setScope(scope);
			return accessTokenRepository.store(accessToken.getAccessToken(), accessToken, accessToken.getExpiresIn());
		}
		if (GrantTypes.PASSWORD.equals(grantType)) {
			return userAuthenticator.authenticate(body.getFirst("username"), body.getFirst("password")).flatMap(
			    account -> doPasswordFlow(body, client, account));
		}
		throw new OauthException(UNSUPPORTED_GRANT_TYPE);
	}

	private Observable<AccessToken> doPasswordFlow(MultiValueMap<String, String> body, Client client, Account account) {
		AccessToken accessToken = new AccessToken(UUID.randomUUID().toString(), TOKEN_EXPIRATION_TIME);
		accessToken.setScope(body.getFirst("scope"));
		accessToken.setUserName(account.getUserName());
		return accessTokenRepository.store(accessToken.getAccessToken(), accessToken, accessToken.getExpiresIn());
	}

	@RequestMapping(value = "/{token}", method = RequestMethod.GET)
	public DeferredResult<AccessToken> verifyToken(@PathVariable("token") String token) {
		return AsyncHelper.asDeferredResult(retrieveToken(token));
	}

	private Observable<AccessToken> retrieveToken(String token) {
		return accessTokenRepository.retrieve(token).map(
		    t -> t.orElseThrow(() -> new OauthException(new ErrorInfo(HttpStatus.NOT_FOUND, "invalid_token", 7))));
	}

}