package org.bookmc.loader.exception;

public class MissingDependencyException extends IllegalStateException {
    public MissingDependencyException(String dependency) {
        super(String.format("The dependency \"%s\" could not be found! Please make sure it is located in the mods folder", dependency));
    }
}
