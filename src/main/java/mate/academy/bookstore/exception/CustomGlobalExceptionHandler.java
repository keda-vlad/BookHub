package mate.academy.bookstore.exception;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import lombok.Data;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class CustomGlobalExceptionHandler extends ResponseEntityExceptionHandler {
    @Override
    public ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request
    ) {
        List<String> errors = ex.getBindingResult().getAllErrors().stream()
                .map(this::getErrorMessage)
                .toList();
        ErrorBody errorBody = ErrorBody.of(HttpStatus.BAD_REQUEST, errors);
        return new ResponseEntity<>(errorBody, headers, status);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Object> handleEntityNotFoundException(
            EntityNotFoundException ex
    ) {
        ErrorBody errorBody = ErrorBody.of(HttpStatus.CONFLICT, ex.getMessage());
        return new ResponseEntity<>(errorBody, errorBody.getStatus());
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Object> handleDataIntegrityViolationException(
            DataIntegrityViolationException ex
    ) {
        ErrorBody errorBody = ErrorBody.of(HttpStatus.CONFLICT, ex.getMessage());
        return new ResponseEntity<>(errorBody, errorBody.getStatus());
    }

    @ExceptionHandler(SpecificationNotFoundException.class)
    public ResponseEntity<Object> handleSpecificationNotFoundException(
            SpecificationNotFoundException ex
    ) {
        ErrorBody errorBody = ErrorBody.of(HttpStatus.CONFLICT, ex.getMessage());
        return new ResponseEntity<>(errorBody, errorBody.getStatus());
    }

    @ExceptionHandler(RegistrationException.class)
    public ResponseEntity<Object> handleSpecificationNotFoundException(
            RegistrationException ex
    ) {
        ErrorBody errorBody = ErrorBody.of(HttpStatus.CONFLICT, ex.getMessage());
        return new ResponseEntity<>(errorBody, errorBody.getStatus());
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<Object> handleSpecificationNotFoundException(
            NoSuchElementException ex
    ) {
        ErrorBody errorBody = ErrorBody.of(HttpStatus.CONFLICT, ex.getMessage());
        return new ResponseEntity<>(errorBody, errorBody.getStatus());
    }

    @ExceptionHandler(OrderException.class)
    public ResponseEntity<Object> handleSpecificationNotFoundException(
            OrderException ex
    ) {
        ErrorBody errorBody = ErrorBody.of(HttpStatus.BAD_REQUEST, ex.getMessage());
        return new ResponseEntity<>(errorBody, errorBody.getStatus());
    }

    private String getErrorMessage(ObjectError objectError) {
        if (objectError instanceof FieldError) {
            String field = ((FieldError) objectError).getField();
            String message = objectError.getDefaultMessage();
            return field + " " + message;
        }
        return objectError.getDefaultMessage();
    }

    @Data
    private static class ErrorBody {
        private final LocalDateTime timestamp = LocalDateTime.now();
        private HttpStatus status;
        private Object errors;

        public static ErrorBody of(HttpStatus status, Object errors) {
            ErrorBody errorBody = new ErrorBody();
            errorBody.status = status;
            errorBody.errors = errors;
            return errorBody;
        }
    }
}
