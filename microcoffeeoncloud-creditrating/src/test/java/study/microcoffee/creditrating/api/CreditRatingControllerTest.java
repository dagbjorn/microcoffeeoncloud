package study.microcoffee.creditrating.api;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.cloud.autoconfigure.RefreshAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import study.microcoffee.creditrating.behavior.ServiceBehaviorFactory;
import study.microcoffee.creditrating.domain.CreditRating;
import study.microcoffee.creditrating.logging.HttpLoggingFilterTestConfig;

/**
 * Unit tests of {@link CreditRatingController}.
 */
@WebMvcTest(CreditRatingController.class)
@ImportAutoConfiguration(RefreshAutoConfiguration.class)
@TestPropertySource("/application-test.properties")
@Import(HttpLoggingFilterTestConfig.class)
public class CreditRatingControllerTest {

    private static final String SERVICE_PATH = "/api/coffeeshop/creditrating/{customerId}";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void getCreditRatingShouldReturnRating() throws Exception {
        final String expectedContent = objectMapper.writeValueAsString(new CreditRating(70));

        mockMvc.perform(get(SERVICE_PATH, 123).accept(MediaType.APPLICATION_JSON_VALUE)) //
            .andExpect(status().isOk()) //
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON)) //
            .andExpect(content().json(expectedContent));
    }

    @TestConfiguration
    static class Config {

        @Bean
        public ServiceBehaviorFactory serviceBehaviorFactory() {
            return new ServiceBehaviorFactory();
        }
    }
}
