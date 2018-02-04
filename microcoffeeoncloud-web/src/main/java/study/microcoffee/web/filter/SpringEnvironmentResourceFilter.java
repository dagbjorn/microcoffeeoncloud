package study.microcoffee.web.filter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.core.env.Environment;

/**
 * Implementation of a ResourceFilter where the configuration source is based on properties in the Spring environment.
 */
public class SpringEnvironmentResourceFilter implements ResourceFilter {

    private Environment environment;

    /**
     * Creates a SpringEnvironmentResourceFilter object initialized with the provided Spring environment.
     *
     * @param environment
     *            the Spring environment.
     */
    public SpringEnvironmentResourceFilter(Environment environment) {
        this.environment = environment;
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

            if (environment.getProperty(propName) != null) {
                line = line.replace(matcher.group(0), environment.getProperty(propName));
            }
        }

        return line;
    }
}
