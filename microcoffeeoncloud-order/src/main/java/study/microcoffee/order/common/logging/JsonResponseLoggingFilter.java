package study.microcoffee.order.common.logging;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingResponseWrapper;
import org.springframework.web.util.WebUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

/**
 * Simple response logging filter that writes the response data to the application log.
 * <p>
 * If the content type is <code>application/json</code>, the response is by default pretty-printed. To disable pretty-printing and
 * rather use compact log format, define the option <code>--formatted.logging=false</code> on the command line when starting the
 * application.
 */
public class JsonResponseLoggingFilter extends OncePerRequestFilter {

    private final Logger logger = LoggerFactory.getLogger(JsonResponseLoggingFilter.class);

    private ObjectMapper objectMapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);

    private boolean formattedLogging;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
        throws ServletException, IOException {

        HttpServletResponse responseToUse = response;
        if (!(response instanceof ContentCachingResponseWrapper)) {
            responseToUse = new ContentCachingResponseWrapper(response);
        }

        try {
            filterChain.doFilter(request, responseToUse);
        } finally {
            if (shouldLog()) {
                String message = getResponseMessage(responseToUse);
                logger.debug(message);
            }
        }
    }

    private String getResponseMessage(HttpServletResponse response) {
        StringBuilder builder = new StringBuilder();

        ContentCachingResponseWrapper wrapper = WebUtils.getNativeResponse(response, ContentCachingResponseWrapper.class);

        if (wrapper != null) {
            byte[] buf = wrapper.getContentAsByteArray();
            if (buf.length > 0) {
                String payload;

                try {
                    payload = new String(buf, 0, buf.length, wrapper.getCharacterEncoding());

                    if (isFormattedLogging() && wrapper.getContentType().startsWith(MediaType.APPLICATION_JSON_VALUE)) {
                        Object object = objectMapper.readValue(payload, Object.class);
                        payload = "\n" + objectMapper.writeValueAsString(object);
                    }

                    // Don't forget this; otherwise there will be no response data to return to the consumer.
                    wrapper.copyBodyToResponse();
                } catch (IOException ex) {
                    payload = "[unknown]";
                }

                builder.append("RESPONSE DATA : ").append(payload);
            }
        }

        return builder.toString();
    }

    private boolean shouldLog() {
        return logger.isDebugEnabled();
    }

    public boolean isFormattedLogging() {
        return formattedLogging;
    }

    public void setFormattedLogging(boolean formattedLogging) {
        this.formattedLogging = formattedLogging;
    }
}
