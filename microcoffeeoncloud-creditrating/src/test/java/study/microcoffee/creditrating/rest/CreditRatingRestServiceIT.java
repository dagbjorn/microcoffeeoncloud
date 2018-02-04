package study.microcoffee.creditrating.rest;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import study.microcoffee.creditrating.domain.CreditRating;

/**
 * Integration tests of {@link CreditRatingRestService}.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("devlocal")
@TestPropertySource("/application-test.properties")
public class CreditRatingRestServiceIT {

    private static final String SERVICE_PATH = "/coffeeshop/creditrating/{customerId}";

    private static final String CUSTOMER_ID = "Dagbjørn";

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void getCreditRatingShouldReturnRating() throws Exception {
        ResponseEntity<CreditRating> response = restTemplate.getForEntity(SERVICE_PATH, CreditRating.class, CUSTOMER_ID);

        System.out.println(response);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getCreditRating()).isEqualTo(70);
    }
}
