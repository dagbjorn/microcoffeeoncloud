package study.microcoffee.gateway.health;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

/**
 * Unit tests of {@link HealthCheckController}.
 */
public class HealthCheckControllerTest {

    private WebTestClient client = WebTestClient.bindToController(new HealthCheckController()).build();

    @Test
    public void isReadyShouldSucceed() throws Exception {
        client.get().uri("/internal/isready") //
            .accept(MediaType.ALL) //
            .exchange() //
            .expectStatus().isOk();
    }

    @Test
    public void isAliveShouldSucceed() throws Exception {
        client.get().uri("/internal/isalive") //
            .accept(MediaType.ALL) //
            .exchange() //
            .expectStatus().isOk();
    }
}
