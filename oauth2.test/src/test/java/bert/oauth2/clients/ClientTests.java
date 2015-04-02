package bert.oauth2.clients;

import static com.jayway.restassured.RestAssured.expect;
import static com.jayway.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

import java.util.Arrays;

import oauth2.model.AccessToken;

import org.junit.Test;

import com.google.common.collect.ImmutableMap;
import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.response.Response;
import com.jayway.restassured.specification.RequestSpecification;

public class ClientTests {

	private static final String MASTER_CLIENT_ID = "140d3ce4-c6b2-42cd-8a6c-ba656e5bd467";
	private static final String MASTER_CLIENT_SECRET = "bert";

	@Test
	public void getAllClients() {
		givenAccessTokenWithScope("admin:manage_clients").when().get("/clients").then().statusCode(200);
	}

	private RequestSpecification givenAccessTokenWithScope(String scope) {
		return given().header("Authorization", "Bearer " + getAccessToken(scope));
	}

	private String getAccessToken(String scopes) {
		Response response = given().contentType(ContentType.URLENC).formParam("grant_type", "client_credentials")
		    .formParam("scope", scopes).auth().preemptive().basic(MASTER_CLIENT_ID, MASTER_CLIENT_SECRET)
		    .post("/oauth/token");
		return response.as(AccessToken.class).access_token;
	}

	@Test
	public void getSingleClient() {
		givenAccessTokenWithScope("admin:manage_clients").expect().statusCode(200)
		    .body("name", equalTo("Master client"), "grantTypes[0]", equalTo("client_credentials")).when()
		    .get("/clients/" + MASTER_CLIENT_ID);
	}

	@Test
	public void tryToGetSingleClientWithWrongScope() {
		givenAccessTokenWithScope("something").expect().statusCode(401)
		    .body("error", equalTo("access_denied"), "code", equalTo(8)).when().get("/clients/" + MASTER_CLIENT_ID);
	}

	@Test
	public void tryToGetSingleClientWithoutToken() {
		expect().statusCode(401).body("error", equalTo("access_denied"), "code", equalTo(8)).when()
		    .get("/clients/" + MASTER_CLIENT_ID);
	}

	@Test
	public void getNonExistingClient() {
		givenAccessTokenWithScope("admin:manage_clients").expect().statusCode(404)
		    .body("error", equalTo("invalid_client"), "code", equalTo(7)).when().get("/clients/xxx-yyy-zzz");
	}

	@Test
	public void createClient() {
		givenAccessTokenWithScope("admin:manage_clients").contentType(ContentType.JSON)
		    .body(ImmutableMap.of("name", "test", "timeToLive", 500, "grantTypes", Arrays.asList("client_credentials")))
		    .when().post("/clients").then().statusCode(201).and()
		    .body("name", equalTo("test"), "grantTypes[0]", equalTo("client_credentials"));
	}

}
