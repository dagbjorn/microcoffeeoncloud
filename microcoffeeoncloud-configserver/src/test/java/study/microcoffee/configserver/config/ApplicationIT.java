package study.microcoffee.configserver.config;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@TestPropertySource({ "/application.properties", "/application-devlocal.properties" })
public class ApplicationIT {

    @Test
    public void applicationContextShouldLoad() {
    }

    @Configuration
    @Import(Application.class)
    static class Config {
    }
}
