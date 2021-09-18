package org.bookmc.loader.api.mod.metadata;

import org.bookmc.loader.api.environment.GameEnvironment;
import org.bookmc.loader.api.mod.ModContainer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface ModMetadata {
    int getSchemaVersion();
    @Nonnull
    String getName();
    @Nonnull
    String getId();
    @Nonnull
    ModAuthor[] getModAuthors();
    @Nonnull
    ModVersion getVersion();
    @Nullable
    ModResource getIcon(ModContainer container);
    @Nullable
    String getLicense();
    @Nonnull
    GameEnvironment getEnvironment();
    @Nonnull
    ModReliance[] getDependencies();
    @Nonnull
    ModReliance[] getSuggestions();
    @Nonnull
    ModEntrypoint[] getEntrypoints();
}
