package dev.thural.quietspace.exception;

public class CustomParameterConstraintException extends RuntimeException {
    public CustomParameterConstraintException() {
        super();
    }

    public CustomParameterConstraintException(String message) {
        super("A parameter constraint error occurred: ".concat(message));
    }
}
