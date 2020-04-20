package group12.rest.url;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;

@QuarkusTest
public class URLResourceTest {

    @Test
    public void testHelloEndpoint() {
        given()
          .when().get("/shorten")
          .then()
             .statusCode(200)
             .body(is("hello"));
    }

}