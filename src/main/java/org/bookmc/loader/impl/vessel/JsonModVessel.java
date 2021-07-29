package org.bookmc.loader.impl.vessel;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.bookmc.loader.api.vessel.ModVessel;
import org.bookmc.loader.api.vessel.dependency.ModDependency;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
    public String[] getAuthors() {
        return object.has("authors") ? toString(object.get("authors").getAsJsonArray()) : new String[0];
    }

    @Override
    public String getVersion() {
        return object.get("version").getAsString();
    }

    @Override
    public String getIcon() {
        return object.has("icon") ? object.get("icon").getAsString() : null;
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
    public ModDependency[] getDependsOn() {
        return getDependsOn("depends");
    }

    @Override
    public ModDependency[] getSuggestions() {
        return getDependsOn("suggests");
    }

    @Override
    public URL[] getExternalDependencies() {
        List<URL> urls = new ArrayList<>();

        if (!object.has("external")) return new URL[0];
        if (!object.isJsonObject())
            throw new IllegalStateException("The external dependencies block must be a json object");

        for (Map.Entry<String, JsonElement> key : object.get("external").getAsJsonObject().entrySet()) {
            JsonElement element = key.getValue();
            if (element.isJsonPrimitive()) {
                try {
                    urls.add(new URL(element.getAsString()));
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
            }
        }

        return urls.toArray(new URL[0]);
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

    private ModDependency[] getDependsOn(String memberName) {
        List<ModDependency> dependencies = new ArrayList<>();

        if (!object.has(memberName)) return new ModDependency[0];
        if (!object.isJsonObject()) throw new IllegalStateException(memberName + " must be specified in a json object");

        for (Map.Entry<String, JsonElement> key : object.get(memberName).getAsJsonObject().entrySet()) {
            String id = key.getKey();

            JsonElement element = key.getValue();
            if (element.isJsonPrimitive()) {
                dependencies.add(new ModDependency(id, element.getAsString()));
            }
        }

        return dependencies.toArray(new ModDependency[0]);
    }
}
