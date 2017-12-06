package de.unitrier.st.util;

public class InputTooShortException extends IllegalArgumentException {
    public InputTooShortException() { }

    public InputTooShortException(String message) {
        super(message);
    }
}
