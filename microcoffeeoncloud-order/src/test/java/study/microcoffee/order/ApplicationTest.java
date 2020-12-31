package study.microcoffee.order;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;

import study.microcoffee.order.test.DiscoveryTestConfig;

/**
 * Test of loading of application context.
 */
@SpringBootTest
@TestPropertySource("/application-test.properties")
@Import(DiscoveryTestConfig.class)
public class ApplicationTest {

    @Test
    public void applicationContextShouldLoad() {

    }
}
