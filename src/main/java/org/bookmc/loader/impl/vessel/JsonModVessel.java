package org.bookmc.loader.impl.vessel;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.bookmc.loader.api.adapter.java.JavaLanguageAdapter;
import org.bookmc.loader.api.classloader.IQuiltClassLoader;
import org.bookmc.loader.api.vessel.ModVessel;
import org.bookmc.loader.api.vessel.author.Author;
import org.bookmc.loader.api.vessel.dependency.ModDependency;
import org.bookmc.loader.api.vessel.entrypoint.Entrypoint;
import org.bookmc.loader.api.vessel.entrypoint.MixinEntrypoint;
import org.bookmc.loader.api.vessel.environment.Environment;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class JsonModVessel implements ModVessel {
    private final JsonObject object;
    private final File file;
    private IQuiltClassLoader classLoader;

    public JsonModVessel(JsonObject object, File file, URLClassLoader classLoader) {
        this.object = object;
        this.file = file;
    }

    public JsonModVessel(JsonObject object, File file) {
        this.object = object;
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
    public Author[] getAuthors() {
        List<Author> authors = new ArrayList<>();

        if (!object.has("authors") || !object.get("authors").isJsonObject()) return new Author[0];

        JsonObject obj = object.getAsJsonObject("authors");
        for (Map.Entry<String, JsonElement> author : obj.entrySet()) {
            String name = author.getKey();
            JsonObject authorObj = author.getValue().getAsJsonObject();
            authors.add(new Author(
                name,
                authorObj.has("github") ? authorObj.get("github").getAsString() : null,
                authorObj.has("email") ? authorObj.get("email").getAsString() : null
            ));
        }

        return authors.toArray(new Author[0]);
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
    public String getLicense() {
        return object.has("license") ? object.get("license").getAsString() : null;
    }

    @Override
    public Entrypoint[] getEntrypoints() {
        if (!object.has("entrypoint") || !object.get("entrypoint").isJsonObject()) return new Entrypoint[0];
        List<Entrypoint> entrypoints = new ArrayList<>();

        JsonObject entrypoint = object.getAsJsonObject("entrypoint");

        if (entrypoint.has("main")) {
            String[] owners = toString(entrypoint.get("main").getAsJsonArray());
            for (String owner : owners) {
                String[] split = owner.split("::");
                if (split.length == 1) {
                    entrypoints.add(new Entrypoint(owner, "main"));
                } else {
                    entrypoints.add(new Entrypoint(owner, split[2]));
                }
            }
        }

        return entrypoints.toArray(new Entrypoint[0]);
    }

    @Override
    public Environment getEnvironment() {
        return object.has("environment") ? Environment.fromString(object.get("environment").getAsString()) : Environment.UNKNOWN;
    }

    @Override
    public File getFile() {
        return file;
    }

    @Override
    public MixinEntrypoint[] getMixinEntrypoints() {
        if (!object.has("mixin") || !object.get("mixin").isJsonObject()) return new MixinEntrypoint[0];
        List<MixinEntrypoint> entrypoints = new ArrayList<>();

        JsonObject mixinObj = object.getAsJsonObject("mixin");

        if (mixinObj.has("client") && mixinObj.get("client").isJsonArray()) {
            String[] entries = toString(mixinObj.get("client").getAsJsonArray());
            for (String entry : entries) {
                entrypoints.add(new MixinEntrypoint(entry, Environment.CLIENT));
            }
        }

        if (mixinObj.has("server") && mixinObj.get("server").isJsonArray()) {
            String[] entries = toString(mixinObj.get("server").getAsJsonArray());
            for (String entry : entries) {
                entrypoints.add(new MixinEntrypoint(entry, Environment.SERVER));
            }
        }

        if (mixinObj.has("*") && mixinObj.get("*").isJsonArray()) {
            String[] entries = toString(mixinObj.get("*").getAsJsonArray());
            for (String entry : entries) {
                entrypoints.add(new MixinEntrypoint(entry, Environment.ANY));
            }
        }

        return entrypoints.toArray(new MixinEntrypoint[0]);
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
    public IQuiltClassLoader getAbstractedClassLoader() {
        return classLoader;
    }

    @Override
    public void setClassLoader(IQuiltClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    @Override
    public String getLanguageAdapter() {
        return object.has("adapter") ? object.get("adapater").getAsString() : JavaLanguageAdapter.class.getName();
    }

    @Override
    public String[] getTransformers() {
        if (!object.has("transformers") || !object.has("asm")) return new String[0];
        return toString(object.has("transformers") ? object.getAsJsonArray("transformers") : object.getAsJsonArray("asm"));
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
