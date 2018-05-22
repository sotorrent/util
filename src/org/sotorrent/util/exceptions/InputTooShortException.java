package org.sotorrent.util.exceptions;

public class InputTooShortException extends IllegalArgumentException {
    public InputTooShortException() { }

    public InputTooShortException(String message) {
        super(message);
    }
}
