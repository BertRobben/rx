package bert.oauth2.clients;

import java.util.Arrays;
import java.util.UUID;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import bert.oauth2.GrantTypes;
import bert.oauth2.repository.Repository;

@Component
public class StandardClients {

	@Autowired
	private Repository<Client> repo;

	@PostConstruct
	public void createStandardClients() {
		Client masterClient = masterClient();
		repo.store(masterClient.getId(), masterClient, masterClient.getTimeToLive());
	}

	private Client masterClient() {
		Client result = new Client();
		result.setId("140d3ce4-c6b2-42cd-8a6c-ba656e5bd467");
		result.setName("Master client");
		result.setSecret("bert");
		result.setGrantTypes(Arrays.asList(GrantTypes.CLIENT_CREDENTIALS));
		return result;
	}

	public static void main(String[] args) {
		System.out.println(UUID.randomUUID().toString());
	}
}
