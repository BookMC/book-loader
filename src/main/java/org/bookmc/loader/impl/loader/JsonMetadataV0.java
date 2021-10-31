package org.bookmc.loader.impl.loader;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.bookmc.loader.api.environment.GameEnvironment;
import org.bookmc.loader.api.mod.ModContainer;
import org.bookmc.loader.api.mod.metadata.*;
import org.bookmc.loader.impl.loader.version.ModSemverVersion;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

public record JsonMetadataV0(JsonObject obj) implements ModMetadata {
    @Override
    public int getSchemaVersion() {
        return 0;
    }

    @Nonnull
    @Override
    public String getName() {
        return obj.get("name").getAsString();
    }

    @Nullable
    @Override
    public String getDescription() {
        return obj.has("description") ? obj.get("description").getAsString() : null;
    }

    @Nonnull
    @Override
    public String getId() {
        return obj.get("id").getAsString();
    }

    @Nonnull
    @Override
    public ModAuthor[] getModAuthors() {
        List<ModAuthor> authorList = new ArrayList<>();
        if (obj.has("authors")) {
            JsonObject authors = obj.getAsJsonObject("authors");
            for (Map.Entry<String, JsonElement> entry : authors.entrySet()) {
                if (entry.getValue().isJsonObject()) {
                    String key = entry.getKey();
                    JsonObject obj = entry.getValue().getAsJsonObject();
                    authorList.add(new ModAuthor() {
                        @Override
                        public String getName() {
                            return key;
                        }

                        @Override
                        public String getGithub() {
                            return obj.has("github") ? obj.get("github").getAsString() : null;
                        }
                    });
                }
            }
        }
        return authorList.toArray(new ModAuthor[0]);
    }

    @Nonnull
    @Override
    public ModVersion getVersion() {
        return new ModSemverVersion(obj.get("version").getAsString());
    }

    @Override
    public ModResource getIcon(ModContainer container) {
        return obj.has("icon") ? container.createModResource(obj.get("icon").getAsString()) : null;
    }

    @Override
    public String getLicense() {
        return obj.has("license") ? obj.get("license").getAsString() : null;
    }

    @Nonnull
    @Override
    public GameEnvironment getEnvironment() {
        String env = obj.has("environment") ? obj.get("environment").getAsString() : null;
        if (env == null) return GameEnvironment.ANY;
        return fromString(env);
    }

    @Nonnull
    @Override
    public ModReliance[] getDependencies() {
        if (obj.has("depends")) {
            List<ModReliance> reliances = new ArrayList<>();
            Set<Map.Entry<String, JsonElement>> suggestions = obj.getAsJsonObject("depends").entrySet();

            for (Map.Entry<String, JsonElement> entry : suggestions) {
                JsonElement element = entry.getValue();
                if (element.isJsonPrimitive()) {
                    String id = entry.getKey();
                    String value = element.getAsString();
                    VersionIndicator indicator = VersionIndicator.fromVersion(value);
                    String version = VersionIndicator.removeVersionIndicator(value);
                    if (value.equals("*")) {
                        reliances.add(new AsteriskModReliance(id, true));
                    } else {
                        reliances.add(new ModDependancyReliance(id, new ModSemverVersion(version), indicator, true));
                    }
                }
            }
            return reliances.toArray(new ModReliance[0]);
        }

        return new ModReliance[0];
    }

    @Nonnull
    @Override
    public ModReliance[] getSuggestions() {
        if (obj.has("suggests")) {
            List<ModReliance> reliances = new ArrayList<>();
            Set<Map.Entry<String, JsonElement>> suggestions = obj.getAsJsonObject("suggests").entrySet();

            for (Map.Entry<String, JsonElement> entry : suggestions) {
                JsonElement element = entry.getValue();
                if (element.isJsonPrimitive()) {
                    String id = entry.getKey();
                    String value = element.getAsString();
                    VersionIndicator indicator = VersionIndicator.fromVersion(value);
                    String version = VersionIndicator.removeVersionIndicator(value);
                    if (value.equals("*")) {
                        reliances.add(new AsteriskModReliance(id, true));
                    } else {
                        reliances.add(new ModDependancyReliance(id, new ModSemverVersion(version), indicator, false));
                    }
                }
            }
            return reliances.toArray(new ModReliance[0]);
        }

        return new ModReliance[0];
    }

    @Nonnull
    @Override
    public ModEntrypoint[] getEntrypoints() {
        List<ModEntrypoint> entrypointList = new ArrayList<>();
        if (obj.has("entrypoint")) {
            JsonObject entrypointObject = obj.getAsJsonObject("entrypoint");
            for (EntrypointType type : EntrypointType.values()) {
                String name = type.name().toLowerCase(Locale.ROOT);
                if (entrypointObject.has(name)) {
                    JsonArray entrypointArray = entrypointObject.getAsJsonArray(name);
                    for (JsonElement element : entrypointArray) {
                        if (element.isJsonPrimitive()) {
                            String clazz = element.getAsString();
                            entrypointList.add(ModEntrypoint.create(type, clazz));
                        }
                    }
                }
            }
        }

        if (obj.has("asm")) {
            JsonArray asmArray = obj.getAsJsonArray("asm");
            for (JsonElement element : asmArray) {
                if (element.isJsonPrimitive()) {
                    entrypointList.add(ModEntrypoint.create(EntrypointType.TRANSFORMER, element.getAsString()));
                }
            }
        }

        if (obj.has("transformers")) {
            JsonArray asmArray = obj.getAsJsonArray("transformers");
            for (JsonElement element : asmArray) {
                if (element.isJsonPrimitive()) {
                    entrypointList.add(ModEntrypoint.create(EntrypointType.TRANSFORMER, element.getAsString()));
                }
            }
        }

        return entrypointList.toArray(new ModEntrypoint[0]);
    }

    @Nonnull
    @Override
    public ModResource[] getJars(ModContainer container) {
        List<ModResource> jarArray = new ArrayList<>();

        if (obj.has("jars")) {

            JsonArray jars = obj.getAsJsonArray("jars");
            for (JsonElement jar : jars) {
                if (jar.isJsonPrimitive()) {
                    jarArray.add(container.createModResource(jar.getAsString()));
                }
            }
        }

        return jarArray.toArray(new ModResource[0]);
    }

    private GameEnvironment fromString(String environment) {
        GameEnvironment env = GameEnvironment.fromString(environment);
        if (env != null) {
            return env;
        }
        throw new IllegalStateException("An unknown environment was given (" + environment + ")");
    }
}
