package study.microcoffee.creditrating.logging;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.filter.AbstractRequestLoggingFilter;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;
import org.springframework.web.util.WebUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

/**
 * Simple logging filter for HTTP server request and response logging.
 * <p>
 * If <code>formatted.logging</code> is true, the log data is pretty-printed. The body is only pretty-printed if the content type is
 * <code>application/json</code>.
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
 * <p>
 * Note that the filter will only log the part of the request payload which has actually been read, not necessarily the entire body
 * of the request. In practice, the BEFORE record will never log the payload, whereas the AFTER record will contain the entire
 * payload.
 * <p>
 * This logging filter is inspired by Spring {@link AbstractRequestLoggingFilter}.
 */
public class HttpLoggingFilter extends OncePerRequestFilter {

    private final Logger log = LoggerFactory.getLogger(HttpLoggingFilter.class);

    private ObjectMapper objectMapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);

    private boolean formattedLogging;

    private String lineTerminator;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
        throws ServletException, IOException {

        int logSequence = LoggingSequence.getNextSequence();

        HttpServletRequest requestToUse = request;
        if (!(request instanceof ContentCachingRequestWrapper)) {
            requestToUse = new ContentCachingRequestWrapper(request);
        }

        HttpServletResponse responseToUse = response;
        if (!(response instanceof ContentCachingResponseWrapper)) {
            responseToUse = new ContentCachingResponseWrapper(response);
        }

        if (shouldLog()) {
            String formattedRequest = formatRequest(requestToUse);
            log.debug("[{}] [{}]{}{}", logSequence, "BEFORE", lineTerminator, formattedRequest);
        }

        try {
            filterChain.doFilter(requestToUse, responseToUse);
        } finally {
            if (shouldLog()) {
                String formattedRequest = formatRequest(requestToUse);
                log.debug("[{}] [{}]{}{}", logSequence, "AFTER", lineTerminator, formattedRequest);

                String formattedResponse = formatResponse(responseToUse);
                log.debug("[{}]{}{}", logSequence, lineTerminator, formattedResponse);
            }
        }
    }

    //
    // Format request
    //

    private String formatRequest(HttpServletRequest request) {
        StringBuilder builder = new StringBuilder();
        formatMethodAndRequestURL(builder, request);
        formatHeaders(builder, getHeaders(request));
        formatBody(builder, request);

        return builder.toString();
    }

    private void formatMethodAndRequestURL(StringBuilder builder, HttpServletRequest request) {
        builder.append(request.getMethod()).append(' ').append(request.getRequestURI());

        String queryString = request.getQueryString();
        if (queryString != null) {
            builder.append('?').append(queryString);
        }

        builder.append(lineTerminator);
    }

    private HttpHeaders getHeaders(HttpServletRequest request) {
        HttpHeaders headers = new HttpHeaders();

        for (String headerName : Collections.list(request.getHeaderNames())) {
            headers.addAll(headerName, Collections.list(request.getHeaders(headerName)));
        }
        return headers;
    }

    private void formatBody(StringBuilder builder, HttpServletRequest request) {
        ContentCachingRequestWrapper wrappedRequest = WebUtils.getNativeRequest(request, ContentCachingRequestWrapper.class);
        if (wrappedRequest == null) {
            return;
        }

        byte[] buf = wrappedRequest.getContentAsByteArray();
        if (buf.length > 0) {
            String payload;

            try {
                payload = new String(buf, 0, buf.length, wrappedRequest.getCharacterEncoding());

                if (isFormattedLogging() && wrappedRequest.getContentType().startsWith(MediaType.APPLICATION_JSON_VALUE)) {
                    Object object = objectMapper.readValue(payload, Object.class);
                    payload = lineTerminator + objectMapper.writeValueAsString(object);
                }
            } catch (IOException ex) {
                payload = "[unknown]";
            }

            builder.append(payload);
        }
    }

    //
    // Format response
    //

    private String formatResponse(HttpServletResponse response) {
        StringBuilder builder = new StringBuilder();
        formatStatus(builder, response);
        formatHeaders(builder, getHeaders(response));
        formatBody(builder, response);

        return builder.toString();
    }

    private void formatStatus(StringBuilder builder, HttpServletResponse response) {
        HttpStatus httpStatus = HttpStatus.valueOf(response.getStatus());
        builder.append("HTTP ").append(httpStatus.value()).append(' ').append(httpStatus.getReasonPhrase()).append(lineTerminator);
    }

    private HttpHeaders getHeaders(HttpServletResponse response) {
        HttpHeaders headers = new HttpHeaders();

        for (String headerName : response.getHeaderNames()) {
            headers.addAll(headerName, new ArrayList<>(response.getHeaders(headerName)));
        }
        return headers;
    }

    private void formatBody(StringBuilder builder, HttpServletResponse response) {
        ContentCachingResponseWrapper wrappedResponse = WebUtils.getNativeResponse(response, ContentCachingResponseWrapper.class);
        if (wrappedResponse == null) {
            return;
        }

        byte[] buf = wrappedResponse.getContentAsByteArray();
        if (buf.length > 0) {
            String payload;

            try {
                payload = new String(buf, 0, buf.length, wrappedResponse.getCharacterEncoding());

                if (isFormattedLogging() && wrappedResponse.getContentType().startsWith(MediaType.APPLICATION_JSON_VALUE)) {
                    Object object = objectMapper.readValue(payload, Object.class);
                    payload = lineTerminator + objectMapper.writeValueAsString(object);
                }

                // Don't forget this; otherwise there will be no response data to return to the consumer.
                wrappedResponse.copyBodyToResponse();
            } catch (IOException ex) {
                payload = "[unknown]";
            }

            builder.append(payload);
        }
    }

    //
    // Common stuff
    //

    private void formatHeaders(StringBuilder builder, HttpHeaders headers) {
        if (!formattedLogging) {
            builder.append(headers).append(' ');
            return;
        }

        for (Entry<String, List<String>> header : headers.entrySet()) {
            String headerName = header.getKey();
            List<String> values = header.getValue();

            builder.append(headerName).append(": ");

            for (int i = 0; i < values.size(); i++) {
                if (i > 0) {
                    builder.append(", ");
                }
                builder.append(values.get(i));
            }
            builder.append(lineTerminator);
        }
    }

    private boolean shouldLog() {
        return log.isDebugEnabled();
    }

    public boolean isFormattedLogging() {
        return formattedLogging;
    }

    public void setFormattedLogging(boolean formattedLogging) {
        this.formattedLogging = formattedLogging;

        lineTerminator = (formattedLogging) ? "\n" : " ";
    }
}
