package study.microcoffee.order.common.logging;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

/**
 * Spring ClientHttpRequestInterceptor for HTTP client request and response logging.
 * <p>
 * Logged request data:
 * <ul>
 * <li>HTTP method and URI</li>
 * <li>HTTP headers</li>
 * <li>HTTP body data</li>
 * </ul>
 * Logged response data:
 * <ul>
 * <li>HTTP status code and text</li>
 * <li>HTTP headers</li>
 * <li>HTTP body data</li>
 * </ul>
 */
public class HttpLoggingInterceptor implements ClientHttpRequestInterceptor {

    private final Logger logger = LoggerFactory.getLogger(HttpLoggingInterceptor.class);

    private boolean formattedLogging;

    private String lineTerminator;

    public HttpLoggingInterceptor(boolean formattedLogging) {
        this.formattedLogging = formattedLogging;

        lineTerminator = (formattedLogging) ? "\n" : " ";
    }

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        int logSequence = LoggingSequence.getNextSequence();

        if (shouldLog()) {
            String formattedRequest = formatRequest(request, body);
            logger.debug("[{}]{}{}", logSequence, lineTerminator, formattedRequest);
        }

        ClientHttpResponse response = execution.execute(request, body);
        if (shouldLog()) {
            String formattedResponse = formatResponse(response);
            logger.debug("[{}]{}{}", logSequence, lineTerminator, formattedResponse);
        }

        return response;
    }

    /**
     * Formats the request data consisting of HTTP method and URI, HTTP headers and body data (if any).
     */
    private String formatRequest(HttpRequest request, byte[] body) {
        StringBuilder builder = new StringBuilder();
        builder.append(request.getMethod() + " " + request.getURI() + lineTerminator);

        HttpHeaders headers = request.getHeaders();
        formatHeaders(builder, headers);

        if (body != null) {
            String bodyAsString = new String(body, StandardCharsets.UTF_8);
            formatBody(builder, bodyAsString);
        }

        return builder.toString();
    }

    /**
     * Formats the response data consisting of HTTP status (code and text), HTTP headers and body data (if any).
     */
    private String formatResponse(ClientHttpResponse response) throws IOException {
        StringBuilder builder = new StringBuilder();
        builder.append("HTTP " + response.getStatusCode() + " " + response.getStatusText() + lineTerminator);

        HttpHeaders headers = response.getHeaders();
        formatHeaders(builder, headers);

        String bodyAsString = readInputStreamAsString(response.getBody());
        formatBody(builder, bodyAsString);

        return builder.toString();
    }

    private void formatHeaders(StringBuilder builder, HttpHeaders headers) {
        if (!formattedLogging) {
            builder.append(headers);
            builder.append(' ');
            return;
        }

        for (Entry<String, List<String>> header : headers.entrySet()) {
            String headerName = header.getKey();
            List<String> values = header.getValue();

            builder.append(headerName + ": ");

            for (int i = 0; i < values.size(); i++) {
                if (i > 0) {
                    builder.append(", ");
                }
                builder.append(values.get(i));
            }
            builder.append(lineTerminator);
        }
    }

    private void formatBody(StringBuilder builder, String body) {
        if (body.length() > 0) {
            builder.append(body);
            builder.append(lineTerminator);
        }
    }

    /**
     * Reads data from an InputStream and returns the data on string format.
     */
    private String readInputStreamAsString(InputStream input) throws IOException {
        StringBuilder textBuilder = new StringBuilder();
        try (Reader reader = new BufferedReader(new InputStreamReader(input, StandardCharsets.UTF_8))) {
            int c = 0;
            while ((c = reader.read()) != -1) {
                textBuilder.append((char) c);
            }
        }

        return textBuilder.toString();
    }

    private boolean shouldLog() {
        return logger.isDebugEnabled();
    }
}
