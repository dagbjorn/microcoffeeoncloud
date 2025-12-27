package study.microcoffee.order;

import java.nio.charset.StandardCharsets;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.MockMvcBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.test.web.servlet.setup.ConfigurableMockMvcBuilder;
import org.springframework.web.filter.CharacterEncodingFilter;

/**
 * Test configuration for setting the character encoding in servlet requests and responses to UTF-8. Uses a MockMvcBuilderCustomizer
 * to add the filter to the MockMvc object. (MockMvc uses ISO-8859-1 by default.)
 * <p>
 * Import this TestConfig class at class level in the controller unit test.
 *
 * <pre>
 * ..
 * &#64;Import(CharacterEncodingFilterTestConfig.class)
 * public class OrderControllerTest {
 * </pre>
 */
@TestConfiguration
public class CharacterEncodingFilterTestConfig {

    @Bean
    public MockMvcBuilderCustomizer characterEncodingFilterMockMvcCustomizer() {
        return new MockMvcBuilderCustomizer() {

            @Override
            public void customize(ConfigurableMockMvcBuilder<?> builder) {
                CharacterEncodingFilter filter = new CharacterEncodingFilter();
                filter.setEncoding(StandardCharsets.UTF_8.name());
                filter.setForceEncoding(true);
                builder.addFilters(filter);
            }
        };
    }
}
