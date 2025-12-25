package ee.lio;

import ee.lio.dto.response.ApiResponse;
import ee.lio.exceptions.DataNotValidatedException;
import ee.lio.exceptions.ExistingUsernameException;
import ee.lio.exceptions.ForbiddenException;
import ee.lio.exceptions.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DataNotValidatedException.class)
    public ResponseEntity<ApiResponse> handleValidationException(DataNotValidatedException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse("Validation failed",
                ex.getMessage()));
    }

    @ExceptionHandler(ExistingUsernameException.class)
    public ResponseEntity<ApiResponse> handleExistingUsername(ExistingUsernameException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ApiResponse("Username already exists",
                        ex.getMessage()));
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse> handleResourceNotFoundException(ResourceNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ApiResponse("Not found",
                        ex.getMessage()));
    }

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<ApiResponse> handleForbidden(ForbiddenException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(new ApiResponse("Action forbidden",
                        ex.getMessage()));

    }
}
