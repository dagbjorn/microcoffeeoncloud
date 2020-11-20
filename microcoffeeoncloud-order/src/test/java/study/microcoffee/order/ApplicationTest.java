package study.microcoffee.order;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

/**
 * Test of loading of application context.
 */
@SpringBootTest
@TestPropertySource("/application-test.properties")
public class ApplicationTest {

    @Test
    public void applicationContextShouldLoad() {

    }
}
