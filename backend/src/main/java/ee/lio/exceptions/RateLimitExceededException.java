package ee.lio.exceptions;

public class RateLimitExceededException extends RuntimeException {
    public RateLimitExceededException() {
        super("Too many requests — please try again in a minute.");
    }
}

