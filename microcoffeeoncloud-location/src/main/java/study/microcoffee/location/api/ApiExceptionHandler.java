package study.microcoffee.location.api;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

/**
 * Global exception handler.
 */
@RestControllerAdvice("study.microcoffee.location.api")
public class ApiExceptionHandler {

    private Logger logger = LoggerFactory.getLogger(ApiExceptionHandler.class);

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    ResponseEntity<String> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e,
        HttpServletResponse response) {
        StringBuilder builder = new StringBuilder("Invalid parameter value received: ");
        builder.append(e.getName() + "=" + e.getValue());
        builder.append(", detailedMessage=" + e.getMessage());

        return ResponseEntity //
            .badRequest() //
            .contentType(MediaType.TEXT_PLAIN) //
            .body(builder.toString());
    }

    @ExceptionHandler(Exception.class)
    ResponseEntity<String> handleException(Exception e, HttpServletResponse response) {
        logger.error(e.getMessage(), e);

        return ResponseEntity //
            .status(HttpStatus.INTERNAL_SERVER_ERROR) //
            .contentType(MediaType.TEXT_PLAIN) //
            .body(e.getMessage());
    }
}
