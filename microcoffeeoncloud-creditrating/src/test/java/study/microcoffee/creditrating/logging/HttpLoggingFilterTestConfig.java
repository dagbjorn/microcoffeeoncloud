package study.microcoffee.creditrating.logging;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.MockMvcBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.test.web.servlet.setup.ConfigurableMockMvcBuilder;

/**
 * Test configuration for testing {@link HttpLoggingFilter} as part of a controller unit test. Uses a MockMvcBuilderCustomizer to
 * add the filter to the MockMvc object.
 * <p>
 * Import this TestConfig class at class level in the controller unit test.
 *
 * <pre>
 * ..
 * &#64;Import(HttpLoggingFilterTestConfig.class)
 * public class CreditRatingControllerTest {
 * </pre>
 */
@TestConfiguration
public class HttpLoggingFilterTestConfig {

    @Bean
    public MockMvcBuilderCustomizer httpLoggingFilterMockMvcCustomizer() {
        return new MockMvcBuilderCustomizer() {

            @Override
            public void customize(ConfigurableMockMvcBuilder<?> builder) {
                HttpLoggingFilter filter = new HttpLoggingFilter();
                filter.setFormattedLogging(false);
                builder.addFilters(filter);
            }
        };
    }
}
