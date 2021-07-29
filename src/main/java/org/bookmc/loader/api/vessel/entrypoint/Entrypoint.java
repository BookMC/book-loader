package org.bookmc.loader.api.vessel.entrypoint;

public class Entrypoint {
    private final String owner;
    private final String method;

    public Entrypoint(String owner, String method) {
        this.owner = owner;
        this.method = method;
    }

    public String getOwner() {
        return owner;
    }

    public String getMethod() {
        return method;
    }
}
