package org.bookmc.loader.impl.candidate;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.bookmc.loader.api.candidate.ModCandidate;
import org.bookmc.loader.api.classloader.IQuiltClassLoader;
import org.bookmc.loader.api.vessel.ModVessel;
import org.bookmc.loader.impl.launch.Launcher;
import org.bookmc.loader.impl.vessel.JsonModVessel;
import org.bookmc.loader.shared.Constants;

import java.io.*;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

/**
 * A custom candidate which takes a folder which has been classed as a candidate
 * and if it contains the requirements to be classed as a Book mod allows it to continue
 * it's journey by keeping it in the candidates position instead of moving it to rejection
 *
 * @author ChachyDev
 */
public class DirectoryModCandidate implements ModCandidate {
    private final List<ModVessel> vessels = new ArrayList<>();
    private final JsonParser parser = new JsonParser();

    private final File file;

    public DirectoryModCandidate(File file) {
        this.file = file;
    }

    @Override
    public ModVessel[] getVessels() {
        if (vessels.isEmpty()) {
            throw new IllegalStateException("Failed to parse any vessels");
        }

        return vessels.toArray(new ModVessel[0]);
    }

    @Override
    public boolean isResolvable() {
        File[] files = file.listFiles();

        if (files == null) return false;

        for (File file : files) {
            if (file.getName().equals(Constants.LOADER_JSON_FILE)) {
                try (InputStream fis = new FileInputStream(file)) {
                    try (InputStreamReader reader = new InputStreamReader(fis)) {
                        JsonElement json = parser.parse(reader);

                        if (json.isJsonObject()) {
                            vessels.add(new JsonModVessel(json.getAsJsonObject(), file, Launcher.getQuiltClassLoader()));
                            return true;
                        } else if (json.isJsonArray()) {
                            JsonArray mods = parser.parse(reader).getAsJsonArray();
                            for (int i = 0; i < mods.size(); i++) {
                                JsonObject mod = mods.get(i).getAsJsonObject();
                                vessels.add(new JsonModVessel(mod, file, Launcher.getQuiltClassLoader()));
                            }
                            return true;
                        }

                        return false;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return false;
    }

    @Override
    public void addToClasspath(IQuiltClassLoader classLoader) {
        try {
            classLoader.addURL(file.toURI().toURL());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }
}
