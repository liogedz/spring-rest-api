package ee.lio;

import ee.lio.dto.response.ErrorResponse;
import ee.lio.exceptions.*;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DataNotValidatedException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(DataNotValidatedException ex) {
        return ResponseEntity.badRequest()
                .body(new ErrorResponse(ex.getMessage(),
                        400,
                        LocalDateTime.now()));
    }

    @ExceptionHandler(ExistingUsernameException.class)
    public ResponseEntity<ErrorResponse> handleExistingUsername(ExistingUsernameException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ErrorResponse(ex.getMessage(),
                        409,
                        LocalDateTime.now()));
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFoundException(ResourceNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse(ex.getMessage(),
                        404,
                        LocalDateTime.now()));
    }

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<ErrorResponse> handleForbidden(ForbiddenException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(new ErrorResponse(ex.getMessage(),
                        403,
                        LocalDateTime.now()));

    }

    @ExceptionHandler(InvalidVerificationCodeException.class)
    public ResponseEntity<ErrorResponse> handleInvalidCode(InvalidVerificationCodeException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorResponse(ex.getMessage(),
                        401,
                        LocalDateTime.now()));
    }

    @ExceptionHandler(InvalidIdentifierException.class)
    public ResponseEntity<ErrorResponse> handleInvalidIdentifier(InvalidIdentifierException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorResponse(ex.getMessage(),
                        401,
                        LocalDateTime.now()));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrityViolation(
            DataIntegrityViolationException ex) {

        String message = "Data integrity violation.";

        if (ex.getMostSpecificCause().getMessage().contains("user_name_unique")) {
            message = "Username already taken.";
        } else if (ex.getMostSpecificCause().getMessage().contains("user_email_unique")) {
            message = "Email already taken.";
        }
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(new ErrorResponse(message,
                        409,
                        LocalDateTime.now()));
    }
}
