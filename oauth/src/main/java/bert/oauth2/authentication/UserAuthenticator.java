package bert.oauth2.authentication;

import rx.Observable;
import bert.oauth2.accounts.Account;

public interface UserAuthenticator {

	Observable<Account> authenticate(String userName, String password);

}
