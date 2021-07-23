package org.bookmc.loader.api.exception;

public class MissingEntrypointException extends IllegalStateException {
    public MissingEntrypointException(String message) {
        super(message);
    }
}
