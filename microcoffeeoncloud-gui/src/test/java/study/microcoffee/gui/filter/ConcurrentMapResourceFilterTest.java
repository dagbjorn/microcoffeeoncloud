package study.microcoffee.gui.filter;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.junit.Test;

/**
 * Unit tests of {@link ConcurrentMapResourceFilter}.
 */
public class ConcurrentMapResourceFilterTest {

    @Test
    public void filterTextWhenNoPropertiesShouldReturnUnmodifiedText() throws IOException {
        ConcurrentMap<String, String> propertyMap = new ConcurrentHashMap<>();

        String text = "    // REST services (https)\n" //
            + "    window.__env.locationServiceUrl = 'https://192.168.99.100:8444';\n" //
            + "    window.__env.menuServiceUrl = 'https://192.168.99.100:8445';\n" //
            + "\n";

        InputStream input = new ByteArrayInputStream(text.getBytes(StandardCharsets.UTF_8));
        StringWriter writer = new StringWriter();

        ResourceFilter filter = new ConcurrentMapResourceFilter(propertyMap);
        filter.filterText(input, writer);

        assertThat(text).isEqualTo(writer.toString());
    }

    @Test
    public void filterTextWhenPropertiesExistsShouldReturnTextWithReplacedEnvVars() throws IOException {
        ConcurrentMap<String, String> propertyMap = new ConcurrentHashMap<>();
        propertyMap.put("MICROCOFFEE_LOCATION_SERVICE_HOST", "192.168.99.100");
        propertyMap.put("MICROCOFFEE_LOCATION_SERVICE_PORT", "8444");
        propertyMap.put("MICROCOFFEE_MENU_SERVICE_HOST", "192.168.99.100");
        propertyMap.put("MICROCOFFEE_MENU_SERVICE_PORT", "8445");

        String text = "    // REST services (https)\n" //
            + "    window.__env.locationServiceUrl = 'https://${MICROCOFFEE_LOCATION_SERVICE_HOST}:${MICROCOFFEE_LOCATION_SERVICE_PORT}';\n" //
            + "    window.__env.menuServiceUrl = 'https://${MICROCOFFEE_MENU_SERVICE_HOST}:${MICROCOFFEE_MENU_SERVICE_PORT}';\n" //
            + "\n";

        InputStream input = new ByteArrayInputStream(text.getBytes(StandardCharsets.UTF_8));
        StringWriter writer = new StringWriter();

        ResourceFilter filter = new ConcurrentMapResourceFilter(propertyMap);
        filter.filterText(input, writer);

        String result = writer.toString();
        System.out.println(result);

        assertThat(text).isNotEqualTo(result);
        assertThat(result).contains("https://192.168.99.100:8444", "https://192.168.99.100:8445");
    }
}
