package study.microcoffee.gui.filter;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;

import org.junit.Test;
import org.springframework.mock.env.MockEnvironment;

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
        mockEnv.setProperty("location.endpointurl.https", "https://192.168.99.100:8444");
        mockEnv.setProperty("menu.endpointurl.https", "https://192.168.99.100:8445");

        String text = "    // REST services (https)\n" //
            + "    window.__env.locationServiceUrl = '${location.endpointurl.https}';\n" //
            + "    window.__env.menuServiceUrl = '${menu.endpointurl.https}';\n" //
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
