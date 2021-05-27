package study.microcoffee.location;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

/**
 * Test of loading of application context.
 */
@SpringBootTest
@TestPropertySource("/application-test.properties")
class ApplicationTest {

    @Test
    void applicationContextShouldLoad() { // NOSONAR Allow no asserts

    }
}
