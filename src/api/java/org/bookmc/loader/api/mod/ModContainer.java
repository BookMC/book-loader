package org.bookmc.loader.api.mod;

import org.bookmc.loader.api.classloader.AbstractBookURLClassLoader;
import org.bookmc.loader.api.mod.metadata.ModMetadata;
import org.bookmc.loader.api.mod.metadata.ModResource;
import org.bookmc.loader.api.mod.metadata.v1.ModMetadataV1;
import org.bookmc.loader.api.mod.state.ModState;

public interface ModContainer {
    ModMetadata getMetadata();

    ModState getModState();

    void setModState(ModState state);

    AbstractBookURLClassLoader getClassLoader();

    void setClassLoader(AbstractBookURLClassLoader classLoader);

    ModResource createModResource(String name);

    default ModMetadataV1 getMetadataV1() {
        return (ModMetadataV1) getMetadata();
    }

}
