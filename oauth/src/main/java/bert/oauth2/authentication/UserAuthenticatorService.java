package bert.oauth2.authentication;

import org.springframework.stereotype.Service;

import rx.Observable;
import bert.oauth2.accounts.Account;

@Service
public class UserAuthenticatorService implements UserAuthenticator {

	@Override
	public Observable<Account> authenticate(String userName, String password) {
		Account account = new Account();
		account.setUserName(userName);
		return Observable.from(account);
	}

}
