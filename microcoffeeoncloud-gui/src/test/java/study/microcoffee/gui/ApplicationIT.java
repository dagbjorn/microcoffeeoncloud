package study.microcoffee.gui;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Test of loading of application context.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource("/application-test.properties")
public class ApplicationIT {

    @Test
    public void applicationContextShouldLoad() {

    }
}
