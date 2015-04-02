package bert.oauth2.authentication;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import rx.Observable;
import bert.oauth2.repository.Repository;
import bert.oauth2.token.AccessToken;
import bert.oauth2.util.ErrorInfos;

@Service
public class TokenAuthenticatorService implements TokenAuthenticator {

	@Autowired
	private Repository<AccessToken> accessTokenRepository;

	@Override
	public Observable<AccessToken> authenticate(HttpServletRequest request, String requiredScope) {
		String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
		if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
			String accessToken = authorizationHeader.substring(7);
			return accessTokenRepository.retrieve(accessToken).map(maybeToken -> {
				AccessToken t = maybeToken.orElseThrow(ErrorInfos::unauthorized);
				if (!hasScope(t.getScope(), requiredScope)) {
					throw ErrorInfos.unauthorized();
				}
				return t;
			});
		}
		return Observable.error(ErrorInfos.unauthorized());
	}

	private boolean hasScope(String scope, String requiredScope) {
		for (String s : scope.split(" ")) {
			if (requiredScope.equals(s)) {
				return true;
			}
		}
		return false;
	}

}
