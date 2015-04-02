package bert.oauth2.authentication;

import javax.servlet.http.HttpServletRequest;

import rx.Observable;
import bert.oauth2.token.AccessToken;

public interface TokenAuthenticator {

	Observable<AccessToken> authenticate(HttpServletRequest request, String requiredScope);

}
