package study.microcoffee.gui.filter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ConcurrentMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Implementation of a ResourceFilter where the configuration source is based on properties in a concurrent map.
 */
public class ConcurrentMapResourceFilter implements ResourceFilter {

    private ConcurrentMap<String, String> propertyMap;

    /**
     * Creates a ConcurrentMapResourceFilter object initialized with the provided concurrent map of properties.
     *
     * @param propertyMap
     *            the map of properties.
     */
    public ConcurrentMapResourceFilter(ConcurrentMap<String, String> propertyMap) {
        this.propertyMap = propertyMap;
    }

    @Override
    public void filterText(InputStream input, Writer writer) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(input, StandardCharsets.UTF_8));
        String line;

        while ((line = reader.readLine()) != null) {
            line = filterLine(line);
            writer.append(line);
            writer.append('\n');
        }
        writer.flush();
    }

    private String filterLine(String line) {
        Pattern pattern = Pattern.compile("\\$\\{(.+?)\\}");
        Matcher matcher = pattern.matcher(line);

        while (matcher.find()) {
            String propName = matcher.group(1);

            if (propertyMap.containsKey(propName)) {
                line = line.replace(matcher.group(0), propertyMap.get(propName));
            }
        }

        return line;
    }
}
