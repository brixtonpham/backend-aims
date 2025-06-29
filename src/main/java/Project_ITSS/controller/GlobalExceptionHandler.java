package Project_ITSS.controller;

import Project_ITSS.exception.PlaceOrderException;
import Project_ITSS.common.validation.ValidationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.InvalidTypeIdException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<String> handleValidationException(ValidationException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler(PlaceOrderException.class)
    public ResponseEntity<String> handlePlaceOrderException(PlaceOrderException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler({HttpMessageNotReadableException.class, JsonParseException.class, JsonMappingException.class, InvalidTypeIdException.class})
    public ResponseEntity<String> handleJsonParsingException(Exception ex, HttpServletRequest request) {
        String requestPath = request.getRequestURI();
        
        // Handle null content for specific endpoints with appropriate validation messages
        if (ex.getMessage() != null && ex.getMessage().toLowerCase().contains("null")) {
            switch (requestPath) {
                case "/placeorder":
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Cart cannot be null");
                case "/deliveryinfo":
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Delivery information cannot be null");
                case "/recalculate":
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Fee information cannot be null");
                case "/finish-order":
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Order information cannot be null");
            }
        }
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid JSON format");
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleRuntimeException(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<String> handleMissingParam(MissingServletRequestParameterException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Missing required parameter: " + ex.getParameterName());
    }
}
