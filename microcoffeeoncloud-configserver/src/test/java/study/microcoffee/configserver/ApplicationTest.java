package study.microcoffee.configserver;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Test of loading of application context.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("devlocal")
public class ApplicationTest {

    @Test
    public void applicationContextShouldLoad() {
    }
}
