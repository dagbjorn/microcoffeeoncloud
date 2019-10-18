package study.microcoffee.location.api;

import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

/**
 * Global exception handler.
 */
@RestControllerAdvice("study.microcoffee.location.api")
public class ApiExceptionHandler {

    private Logger logger = LoggerFactory.getLogger(ApiExceptionHandler.class);

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    ResponseEntity<String> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e, WebRequest request) {
        StringBuilder builder = new StringBuilder("Invalid parameter value received: ");
        builder.append(e.getName() + "=" + e.getValue());
        builder.append(", detailedMessage=" + e.getMessage());

        return buildResponseEntity(request, HttpStatus.BAD_REQUEST, builder.toString());
    }

    @ExceptionHandler(Exception.class)
    ResponseEntity<String> handleException(Exception e, WebRequest request, HttpServletResponse response) {
        logger.error(e.getMessage(), e);

        return buildResponseEntity(request, HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
    }

    private ResponseEntity<String> buildResponseEntity(WebRequest request, HttpStatus status, String message) {
        MediaType contentType = resolveContentType(request);

        if (contentType != null) {
            return ResponseEntity.status(status).contentType(contentType).body(buildBody(contentType, message));
        } else {
            return ResponseEntity.status(status).body(buildBody(contentType, message));
        }
    }

    private MediaType resolveContentType(WebRequest request) {
        List<String> acceptHeaders = Arrays.asList(request.getHeaderValues(HttpHeaders.ACCEPT));
        List<MediaType> acceptMediaTypes = MediaType.parseMediaTypes(acceptHeaders);

        if (acceptMediaTypes.stream().anyMatch(m -> m.isCompatibleWith(MediaType.APPLICATION_JSON))) {
            return MediaType.APPLICATION_JSON;
        } else if (acceptMediaTypes.stream().anyMatch(m -> m.isCompatibleWith(MediaType.TEXT_PLAIN))) {
            return MediaType.TEXT_PLAIN;
        } else {
            return null;
        }
    }

    private String buildBody(MediaType contentType, String message) {
        if (MediaType.APPLICATION_JSON.equals(contentType)) {
            return "{ \"error\": \"" + message + "\" }";
        } else if (MediaType.TEXT_PLAIN.equals(contentType)) {
            return message;
        } else {
            return null;
        }
    }
}
