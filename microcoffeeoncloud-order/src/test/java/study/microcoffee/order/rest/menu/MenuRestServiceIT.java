package study.microcoffee.order.rest.menu;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import study.microcoffee.order.test.config.MongoTestConfig;
import study.microcoffee.order.test.utils.KeystoreUtils;

/**
 * Integration tests of {@link MenuRestService}.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource("/application-test.properties")
@Import(MongoTestConfig.class)
public class MenuRestServiceIT {

    private static final String SERVICE_PATH = "/coffeeshop/menu";

    @Autowired
    private TestRestTemplate restTemplate;

    @BeforeClass
    public static void initOnce() throws Exception {
        KeystoreUtils.configureTruststore();
    }

    @AfterClass
    public static void destroyOnce() throws Exception {
        KeystoreUtils.clearTruststore();
    }

    @Test
    public void getCoffeeMenuShouldReturnCoffeeMenu() {
        ResponseEntity<String> response = restTemplate.getForEntity(SERVICE_PATH, String.class);

        System.out.println(response);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains("Americano");
        assertThat(response.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON_UTF8);
    }
}
