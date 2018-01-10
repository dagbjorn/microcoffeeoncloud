package study.microcoffee.creditrating.rest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cloud.autoconfigure.RefreshAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import study.microcoffee.creditrating.behavior.ServiceBehaviorFactory;
import study.microcoffee.creditrating.domain.CreditRating;

/**
 * Unit tests of {@link CreditRatingRestService}.
 */
@RunWith(SpringRunner.class)
@WebMvcTest
@ImportAutoConfiguration(RefreshAutoConfiguration.class)
@TestPropertySource(properties = { "logging.level.study.microcoffee=DEBUG" })
public class CreditRatingRestServiceTest {

    private static final String SERVICE_PATH = "/coffeeshop/creditrating/{customerId}";

    @MockBean
    private ServiceBehaviorFactory serviceBehaviorFactory;

    @Autowired
    private MockMvc mockMvc;

    private ObjectMapper objectMapper = new ObjectMapper();

    @Ignore("Throws SpelEvaluationException: EL1021E: A problem occurred whilst attempting to access the property 'serviceBehaviorFactory': 'EvaluationContext is required'")
    @Test
    public void getCreditRatingShouldReturnRating() throws Exception {
        final String expectedContent = objectMapper.writeValueAsString(new CreditRating(70));

        mockMvc.perform(get(SERVICE_PATH, 123).accept(MediaType.APPLICATION_JSON_UTF8_VALUE)) //
            .andExpect(status().isOk()) //
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8)) //
            .andExpect(content().json(expectedContent));
    }

    @Configuration
    @Import({ CreditRatingRestService.class })
    static class Config {
    }
}
