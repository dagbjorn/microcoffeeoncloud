package study.microcoffee.location.api;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.URL;
import java.nio.file.Paths;
import java.util.ArrayList;
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
import de.codecentric.hikaku.converters.openapi.OpenApiConverter;
import de.codecentric.hikaku.converters.spring.SpringConverter;
import de.codecentric.hikaku.endpoints.Endpoint;
import de.codecentric.hikaku.endpoints.HttpMethod;
import de.codecentric.hikaku.reporters.CommandLineReporter;
import de.codecentric.hikaku.reporters.Reporter;
import kotlin.jvm.functions.Function1;

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
        URL apiSpec = getClass().getClassLoader().getResource(API_SPEC_FILE);
        OpenApiConverter openApiConverter = new OpenApiConverter(Paths.get(apiSpec.toURI()));
        SpringConverter springConverter = new SpringConverter(applicationContext);

        List<Reporter> reporters = new ArrayList<>();
        reporters.add(new CommandLineReporter());

        List<Function1<Endpoint, Boolean>> ignorePaths = new ArrayList<>();
        ignorePaths.add(SpringConverter.IGNORE_ERROR_ENDPOINT);
        ignorePaths.add((endpoint) -> {
            return endpoint.getPath().equals("/api/coffeeshop/nearest/{latitude}/{longitude}/{maxdistance}")
                && endpoint.getHttpMethod().equals(HttpMethod.HEAD);
        });
        ignorePaths.add((endpoint) -> {
            return endpoint.getPath().equals("/api/coffeeshop/nearest/{latitude}/{longitude}/{maxdistance}")
                && endpoint.getHttpMethod().equals(HttpMethod.OPTIONS);
        });
        ignorePaths.add((endpoint) -> {
            return endpoint.getPath().equals("/internal/isalive");
        });
        ignorePaths.add((endpoint) -> {
            return endpoint.getPath().equals("/internal/isready");
        });
        ignorePaths.add((endpoint) -> {
            return endpoint.getPath().equals("/swagger-resources");
        });
        ignorePaths.add((endpoint) -> {
            return endpoint.getPath().equals("/swagger-resources/configuration/security");
        });
        ignorePaths.add((endpoint) -> {
            return endpoint.getPath().equals("/swagger-resources/configuration/ui");
        });

        HikakuConfig hikakuConfig = new HikakuConfig(reporters, ignorePaths);

        new Hikaku(openApiConverter, springConverter, hikakuConfig).match();
    }
}
