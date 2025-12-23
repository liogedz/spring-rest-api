package ee.lio.exceptions;

public class DataNotValidatedException extends RuntimeException {
    public DataNotValidatedException(String message) {
        super(message);
    }
}
