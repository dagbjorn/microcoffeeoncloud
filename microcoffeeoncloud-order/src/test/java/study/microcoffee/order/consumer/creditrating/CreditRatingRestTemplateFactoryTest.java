package study.microcoffee.order.consumer.creditrating;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.springframework.web.client.RestTemplate;

/**
 * Unit tests of {@link CreditRatingRestTemplateFactory}.
 */
public class CreditRatingRestTemplateFactoryTest {

    private CreditRatingRestTemplateFactory restTemplateFactory = new CreditRatingRestTemplateFactory();

    @Test
    public void createRestTemplateShouldCreateFactory() {
        RestTemplate restTemplate = restTemplateFactory.createRestTemplate();

        assertThat(restTemplate).isNotNull();
    }
}
