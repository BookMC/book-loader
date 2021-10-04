package org.bookmc.loader.impl.loader.resolver;

import org.bookmc.loader.api.classloader.AppendableURLClassLoader;
import org.bookmc.loader.api.environment.GameEnvironment;
import org.bookmc.loader.api.loader.BookLoaderBase;
import org.bookmc.loader.api.mod.ModCandidate;
import org.bookmc.loader.api.mod.ModContainer;
import org.bookmc.loader.api.mod.metadata.*;
import org.bookmc.loader.api.mod.resolution.ModResolver;
import org.bookmc.loader.api.mod.state.ModState;
import org.bookmc.loader.impl.loader.BookLoaderImpl;
import org.bookmc.loader.impl.loader.version.ModSemverVersion;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class FakeContainerResolver implements ModResolver {
    @Override
    public ModCandidate[] resolveMods() {
        return new ModCandidate[]{new BookLoaderCandidate()};
    }

    private static class BookLoaderCandidate implements ModCandidate {
        @Override
        public boolean validate() {
            return true;
        }

        @Override
        public void loadContainers0(AppendableURLClassLoader classLoader) {

        }

        @Override
        public ModContainer[] getContainers() {
            return new ModContainer[]{new BookLoaderContainer()};
        }

        private static class BookLoaderContainer implements ModContainer {
            @Override
            public ModMetadata getMetadata() {
                return new ModMetadata() {
                    @Override
                    public int getSchemaVersion() {
                        return 0;
                    }

                    @Nonnull
                    @Override
                    public String getName() {
                        return "book-loader";
                    }

                    @Override
                    public String getDescription() {
                        return "A core component by BooKMC to load mods ontop most environments";
                    }

                    @Nonnull
                    @Override
                    public String getId() {
                        return "book-loader";
                    }

                    @Nonnull
                    @Override
                    public ModAuthor[] getModAuthors() {
                        return new ModAuthor[0];
                    }

                    @Nonnull
                    @Override
                    public ModVersion getVersion() {
                        return new ModSemverVersion("%LOADER_VERSION%");
                    }

                    @Nullable
                    @Override
                    public ModResource getIcon(ModContainer container) {
                        return container.createModResource("/assets/book-loader/logo_64.png");
                    }

                    @Override
                    public String getLicense() {
                        return "%LICENSE%";
                    }

                    @Nonnull
                    @Override
                    public GameEnvironment getEnvironment() {
                        return GameEnvironment.ANY;
                    }

                    @Nonnull
                    @Override
                    public ModReliance[] getDependencies() {
                        return new ModReliance[0];
                    }

                    @Nonnull
                    @Override
                    public ModReliance[] getSuggestions() {
                        return new ModReliance[0];
                    }

                    @Nonnull
                    @Override
                    public ModEntrypoint[] getEntrypoints() {
                        return new ModEntrypoint[0];
                    }
                };
            }

            @Override
            public ModState getModState() {
                return ModState.STARTED;
            }

            @Override
            public void setModState(ModState state) {
                throw new UnsupportedOperationException();
            }

            @Override
            public AppendableURLClassLoader getClassLoader() {
                return BookLoaderBase.INSTANCE.getGlobalClassLoader();
            }

            @Override
            public void setClassLoader(AppendableURLClassLoader classLoader) {

            }

            @Override
            public ModResource createModResource(String name) {
                return () -> BookLoaderBase.INSTANCE.getGlobalClassLoader().getResourceAsStream(name);
            }
        }
    }
}
