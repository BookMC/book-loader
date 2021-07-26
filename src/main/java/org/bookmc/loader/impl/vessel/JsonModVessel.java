package org.bookmc.loader.impl.vessel;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.bookmc.loader.api.vessel.ModVessel;

import java.io.File;

public class JsonModVessel implements ModVessel {
    private final JsonObject object;
    private final File file;

    public JsonModVessel(JsonObject object, File file) {
        this.object = object;

        if (!object.has("entrypoint")) {
            throw new IllegalStateException("Nope! You cannot not load a mod without an entrypoint!");
        }

        this.file = file;
    }


    @Override
    public String getName() {
        return object.get("name").getAsString();
    }

    @Override
    public String getId() {
        return object.get("id").getAsString();
    }

    @Override
    public String getDescription() {
        return object.has("description") ? object.get("description").getAsString() : null;
    }

    @Override
    public String getConfig() {
        return object.has("config") ? object.get("config").getAsString() : null;
    }

    @Override
    public String getAuthor() {
        return object.has("author") ? object.get("author").getAsString() : "MysteriousDev";
    }

    @Override
    public String[] getAuthors() {
        return object.has("authors") ? toString(object.get("authors").getAsJsonArray()) : new String[]{getAuthor()};
    }

    @Override
    public String getVersion() {
        return object.get("version").getAsString();
    }

    @Override
    public String getEntrypoint() {
        return object.get("entrypoint").getAsString();
    }

    @Override
    public File getFile() {
        return file;
    }

    @Override
    public String getMixinEntrypoint() {
        return object.has("mixin_entrypoint") ? object.get("mixin_entrypoint").getAsString() : null;
    }

    @Override
    public String[] getDependencies() {
        return object.has("dependencies") ? toString(object.get("dependencies").getAsJsonArray()) : new String[0];
    }

    @Override
    public boolean isInternallyEnabled() {
        return true;
    }

    @Override
    public void setInternallyEnabled(boolean enabled) {
        // TODO: Make this work
        throw new UnsupportedOperationException("This operation has not been implemented");
    }

    @Override
    public boolean isCompatibilityLayer() {
        return object.has("compat_layer") && object.get("compat_layer").getAsBoolean();
    }

    private String[] toString(JsonArray array) {
        int size = array.size();
        String[] strings = new String[size];

        for (int i = 0; i < size; i++) {
            strings[i] = array.get(i).getAsString();
        }

        return strings;
    }
}
