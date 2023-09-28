package study.microcoffee.order.common.logging;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jetty.client.Request;
import org.eclipse.jetty.client.Response;
import org.eclipse.jetty.http.HttpField;
import org.eclipse.jetty.http.HttpFields;
import org.eclipse.jetty.http.HttpHeader;

import lombok.extern.slf4j.Slf4j;

/**
 * Jetty HttpClient log enhancer for HTTP request and response logging.
 * <p>
 * Inspired by Baeldung's article <a href="https://www.baeldung.com/spring-log-webclient-calls">Logging Spring WebClient Calls</a>.
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
 *
 */
@Slf4j
public class JettyHttpClientLogEnhancer {

    private boolean formattedLogging;

    private String lineTerminator;

    public JettyHttpClientLogEnhancer(boolean formattedLogging) {
        this.formattedLogging = formattedLogging;

        lineTerminator = (formattedLogging) ? "\n" : " ";
    }

    public Request enhance(Request request) {
        if (shouldLog()) {
            int logSequence = LoggingSequence.getNextSequence();

            logRequest(request, logSequence);
            logResponse(request, logSequence);
        }

        return request;
    }

    //
    // Request
    //

    /**
     * Logs the request data consisting of HTTP method and URI, HTTP headers and body data (if any).
     */
    private void logRequest(Request request, int logSequence) {
        StringBuilder builder = new StringBuilder();
        request.onRequestBegin(req -> formatMethodAndRequestURL(builder, req));
        request.onRequestHeaders(req -> formatHeaders(builder, req.getHeaders()));
        request.onRequestContent((req, body) -> formatBody(builder, body, getCharset(req.getHeaders())));
        request.onRequestSuccess(req -> logSuccess(builder, logSequence));
        request.onRequestFailure((req, e) -> logFailure(builder, logSequence, e));
    }

    private void formatMethodAndRequestURL(StringBuilder builder, Request request) {
        builder.append(request.getMethod());
        builder.append(' ');
        builder.append(request.getURI());
        builder.append(lineTerminator);
    }

    //
    // Response
    //

    /**
     * Logs the response data consisting of HTTP status (code and text), HTTP headers and body data (if any).
     */
    private void logResponse(Request request, int logSequence) {
        StringBuilder builder = new StringBuilder();
        request.onResponseBegin(resp -> formatStatus(builder, resp));
        request.onResponseHeaders(resp -> formatHeaders(builder, resp.getHeaders()));
        request.onResponseContent((resp, body) -> formatBody(builder, body, getCharset(resp.getHeaders())));
        request.onResponseSuccess(resp -> logSuccess(builder, logSequence));
        request.onResponseFailure((resp, e) -> logFailure(builder, logSequence, e));
    }

    private void formatStatus(StringBuilder builder, Response response) {
        builder.append(response.getVersion());
        builder.append(' ');
        builder.append(response.getStatus());
        if (response.getReason() != null) {
            builder.append(' ');
            builder.append(response.getReason());
        }
        builder.append(lineTerminator);
    }

    //
    // Common
    //

    private void formatHeaders(StringBuilder builder, HttpFields headers) {
        if (!formattedLogging) {
            builder.append(getHeadersAsList(headers));
            builder.append(' ');
            return;
        }

        for (HttpField header : headers) {
            String headerName = header.getName();
            String[] values = header.getValues();

            builder.append(headerName + ": ");

            for (int i = 0; i < values.length; i++) {
                if (i > 0) {
                    builder.append(", ");
                }
                builder.append(values[i]);
            }
            builder.append(lineTerminator);
        }
    }

    private List<HttpField> getHeadersAsList(HttpFields headers) {
        List<HttpField> result = new ArrayList<>();
        headers.forEach(result::add);
        return result;
    }

    private void formatBody(StringBuilder builder, ByteBuffer body, Charset charset) {
        String bodyAsString = getBodyAsString(body, charset);
        builder.append(bodyAsString);
    }

    private String getBodyAsString(ByteBuffer buffer, Charset charset) {
        byte[] bytes;

        if (buffer.hasArray()) {
            bytes = new byte[buffer.capacity()];
            System.arraycopy(buffer.array(), 0, bytes, 0, buffer.capacity());
        } else {
            bytes = new byte[buffer.remaining()];
            buffer.get(bytes, 0, bytes.length);
        }

        return new String(bytes, charset);
    }

    /**
     * Returns the charset from a Content-Type header if such a header exist. If nothing is found, UTF-8 is returned as the default
     * value.
     */
    private Charset getCharset(HttpFields headers) {
        Charset charset = StandardCharsets.UTF_8;

        String contentType = headers.get(HttpHeader.CONTENT_TYPE);
        if (contentType != null) {
            String[] tokens = contentType.toLowerCase().split("charset=");
            if (tokens.length == 2) {
                String encoding = tokens[1].replaceAll("[;\"]", "");
                charset = Charset.forName(encoding);
            }
        }

        return charset;
    }

    private boolean shouldLog() {
        return log.isDebugEnabled();
    }

    private void logSuccess(StringBuilder builder, int logSequence) {
        log.debug("[{}]{}{}", logSequence, lineTerminator, builder.toString());
    }

    private void logFailure(StringBuilder builder, int logSequence, Throwable e) {
        if (builder.length() > 0) {
            log.debug("[{}]{}{}", logSequence, lineTerminator, builder.toString());
        }
        log.error("[" + logSequence + "]", e);
    }
}
