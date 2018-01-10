package study.microcoffee.gui.filter;

import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;

/**
 * Interface to a resource filter for filtering environment-specific configuration variables in resource files (typically
 * JavaScript) returned by the web container.
 */
public interface ResourceFilter {

    /**
     * Filters an input stream for possible references to (environment-specific) configuration variables on the format
     * <code>${CONFIG_PARAMETER}</code>. The source of the actual configuration variables is typically environment variables or
     * system properties.
     * <p>
     * If no configuration variables are found in the input text, the text is returned unmodified. The same applies if no
     * corresponding configuration values are found in the configuration source.
     *
     * @param input
     *            input text with possible references to environment-specific configuration variables.
     * @param writer
     *            output text where any configuration variables are replaced by actual values read from the configuration source.
     * @throws IOException
     *             if an I/O error occurs.
     */
    void filterText(InputStream input, Writer writer) throws IOException;
}
