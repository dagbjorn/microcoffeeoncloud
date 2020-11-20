package study.microcoffee.configserver;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * Test of loading of application context.
 */
@SpringBootTest
@ActiveProfiles("devlocal")
public class ApplicationTest {

    @Test
    public void applicationContextShouldLoad() {
    }
}
