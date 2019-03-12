package study.microcoffee.location.api;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.URL;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit4.SpringRunner;

import de.codecentric.hikaku.Hikaku;
import de.codecentric.hikaku.HikakuConfig;
import de.codecentric.hikaku.converter.openapi.OpenApiConverter;
import de.codecentric.hikaku.converter.spring.SpringConverter;

/**
 * Tests if the implementation of the Location API meets its specification.
 *
 * @see <a href="https://github.com/codecentric/hikaku">https://github.com/codecentric/hikaku</a>
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class LocationSpecificationTest {

    private static final String API_SPEC_FILE = "static/swagger/location-apispec-3.0.yml";

    @Autowired
    private ApplicationContext applicationContext;

    @Before
    public void setUp() {
        /*
         * Make sure that the test is run using the same file encoding as used by the API spec file. See maven-surefire-plugin
         * config in pom.
         */
        assertThat(System.getProperty("file.encoding")).isEqualTo("UTF-8");
    }

    @Test
    public void implementationShouldMatchApiSpecification() throws Exception {
        URL apiSpec = LocationSpecificationTest.class.getClassLoader().getResource(API_SPEC_FILE);
        OpenApiConverter openApiConverter = OpenApiConverter.usingPath(Paths.get(apiSpec.toURI()));
        SpringConverter springConverter = new SpringConverter(applicationContext);

        List<String> ignorePaths = new ArrayList<String>();
        ignorePaths.add(SpringConverter.IGNORE_ERROR_ENDPOINT);
        ignorePaths.add("/internal/isalive");
        ignorePaths.add("/internal/isready");
        ignorePaths.add("/swagger-resources");
        ignorePaths.add("/swagger-resources/configuration/security");
        ignorePaths.add("/swagger-resources/configuration/ui");

        HikakuConfig hikakuConfig = new HikakuConfig(new HashSet<>(ignorePaths), true, true);

        new Hikaku(openApiConverter, springConverter, hikakuConfig).match();
    }
}
