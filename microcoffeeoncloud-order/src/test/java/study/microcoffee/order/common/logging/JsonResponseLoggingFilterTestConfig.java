package study.microcoffee.order.common.logging;

import org.springframework.boot.test.autoconfigure.web.servlet.MockMvcBuilderCustomizer;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.web.servlet.setup.ConfigurableMockMvcBuilder;

/**
 * Test configuration for testing {@link JsonResponseLoggingFilter} as part of a controller unit test. Uses a
 * MockMvcBuilderCustomizer to add the filter to the MockMvc object.
 * <p>
 * Import this TestConfig class at class level in the controller unit test.
 *
 * <pre>
 * ..
 * &#64;Import(JsonResponseLoggingFilterTestConfig.class)
 * public class OrderRestServiceTest {
 * </pre>
 */
@TestConfiguration
public class JsonResponseLoggingFilterTestConfig {

    @Bean
    public MockMvcBuilderCustomizer filterMockMvcCustomizer() {
        return new MockMvcBuilderCustomizer() {

            @Override
            public void customize(ConfigurableMockMvcBuilder<?> builder) {
                JsonResponseLoggingFilter filter = new JsonResponseLoggingFilter();
                filter.setFormattedLogging(true);
                builder.addFilters(filter);
            }
        };
    }
}
