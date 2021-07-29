package org.bookmc.loader.api.vessel.dependency;

public class ModDependency {
    private final String id;
    private final String version;

    public ModDependency(String id, String version) {
        this.id = id;
        this.version = version;
    }

    public String getId() {
        return id;
    }

    public String getVersion() {
        return version;
    }
}
