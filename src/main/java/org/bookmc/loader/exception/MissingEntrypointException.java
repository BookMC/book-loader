package org.bookmc.loader.exception;

public class MissingEntrypointException extends IllegalStateException {
    public MissingEntrypointException(String message) {
        super(message);
    }
}
