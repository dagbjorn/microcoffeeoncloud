package study.microcoffee.creditrating;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;

/**
 * Test loading of application context.
 */
@SpringBootTest
@TestPropertySource("/application-test.properties")
@Import(SecurityTestConfig.class)
class ApplicationTest {

    @Test
    void applicationContextShouldLoad() { // NOSONAR Allow no asserts

    }
}
