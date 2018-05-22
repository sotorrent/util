package org.sotorrent.util.exceptions;

public class RateLimitExceededException extends RuntimeException {
    public RateLimitExceededException() { }

    public RateLimitExceededException(String message) {
        super(message);
    }
}