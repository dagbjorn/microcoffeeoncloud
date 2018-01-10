package study.microcoffee.gui.filter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Implementation of a ResourceFilter where the configuration source is based on environment variables.
 */
public class EnvironmentResourceFilter implements ResourceFilter {

    private Map<String, String> envVars;

    /**
     * Creates an EnvironmentResourceFilter object initialized with the system environment.
     */
    public EnvironmentResourceFilter() {
        this(System.getenv());
    }

    /**
     * Creates an EnvironmentResourceFilter object initialized with the provided map of environment variables.
     * <p>
     * Mainly intended for testing.
     *
     * @param envVars
     *            the map of environment variables.
     */
    public EnvironmentResourceFilter(Map<String, String> envVars) {
        this.envVars = envVars;
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
            String envVarName = matcher.group(1);

            if (envVars.containsKey(envVarName)) {
                line = line.replace(matcher.group(0), envVars.get(envVarName));
            }
        }

        return line;
    }
}
