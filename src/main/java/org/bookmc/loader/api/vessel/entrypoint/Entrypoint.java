package org.bookmc.loader.api.vessel.entrypoint;

public record Entrypoint(String owner, String method) {
    public String getOwner() {
        return owner;
    }
    public String getMethod() {
        return method;
    }
}
