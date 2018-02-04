
package study.microcoffee.web.filter;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;

import org.junit.Test;
import org.springframework.mock.env.MockEnvironment;

/**
 * Unit tests of {@link SpringEnvironmentResourceFilter}.
 */
public class SpringEnvironmentResourceFilterTest {

    @Test
    public void filterTextWhenNoEnvironmentPropsShouldReturnUnmodifiedText() throws IOException {
        MockEnvironment mockEnv = new MockEnvironment(); // empty

        String text = "    // REST services (https)\n" //
            + "    window.__env.locationServiceUrl = 'https://192.168.99.100:8444';\n" //
            + "    window.__env.menuServiceUrl = 'https://192.168.99.100:8445';\n" //
            + "\n";

        InputStream input = new ByteArrayInputStream(text.getBytes(StandardCharsets.UTF_8));
        StringWriter writer = new StringWriter();

        ResourceFilter filter = new SpringEnvironmentResourceFilter(mockEnv);
        filter.filterText(input, writer);

        assertThat(text).isEqualTo(writer.toString());
    }

    @Test
    public void filterTextWhenEnvironmentPropsShouldReturnTextWithReplacedEnvVars() throws IOException {
        MockEnvironment mockEnv = new MockEnvironment();
        mockEnv.setProperty("app.location.url.https", "https://192.168.99.100:8444");
        mockEnv.setProperty("app.menu.url.https", "https://192.168.99.100:8445");

        String text = "    // REST services (https)\n" //
            + "    window.__env.locationServiceUrl = '${app.location.url.https}';\n" //
            + "    window.__env.menuServiceUrl = '${app.menu.url.https}';\n" //
            + "\n";

        InputStream input = new ByteArrayInputStream(text.getBytes(StandardCharsets.UTF_8));
        StringWriter writer = new StringWriter();

        ResourceFilter filter = new SpringEnvironmentResourceFilter(mockEnv);
        filter.filterText(input, writer);

        String result = writer.toString();
        System.out.println(result);

        assertThat(text).isNotEqualTo(result);
        assertThat(result).contains("https://192.168.99.100:8444", "https://192.168.99.100:8445");
    }
}
