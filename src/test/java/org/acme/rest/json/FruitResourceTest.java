package org.acme.rest.json;

import io.quarkus.test.junit.QuarkusTest;
import org.acme.rest.domain.Fruit;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.junit.jupiter.api.Test;
import org.keycloak.representations.AccessTokenResponse;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.ws.rs.core.MediaType;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.matchesPattern;

@QuarkusTest
public class FruitResourceTest {

    private static String REALM = "test";
    @ConfigProperty(name = "quarkus.oidc.auth-server-url")
    String serverUrl;

    @Test
    public void testAdd() {
        Fruit fruit = new Fruit("test fruit", "test description");
        Jsonb jsonb = JsonbBuilder.create();
        given()
                .body(jsonb.toJson(fruit))
                .auth().oauth2(getAccessToken("test"))
                .header("Content-Type", MediaType.APPLICATION_JSON)
                .when()
                .post("/fruits")
                .then()
                .statusCode(200)
                .body(matchesPattern(".*test.*"));
    }

    private String getAccessToken(String userName) {
        return given()
                .param("grant_type", "password")
                .param("username", userName)
                .param("password", "test")
                .param("client_id", "frontend")
                .when()
                .post(serverUrl + "/protocol/openid-connect/token")
                .as(AccessTokenResponse.class).getToken();
    }
}
