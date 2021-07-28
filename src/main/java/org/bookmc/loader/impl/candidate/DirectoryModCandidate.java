package org.bookmc.loader.impl.candidate;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.launchwrapper.LaunchClassLoader;
import org.bookmc.loader.api.candidate.ModCandidate;
import org.bookmc.loader.api.vessel.ModVessel;
import org.bookmc.loader.impl.vessel.JsonModVessel;
import org.bookmc.loader.shared.Constants;

import java.io.*;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

public class DirectoryModCandidate implements ModCandidate {
    private final List<ModVessel> vesselList = new ArrayList<>();
    private final JsonParser parser = new JsonParser();

    private final File file;

    public DirectoryModCandidate(File file) {
        this.file = file;
    }

    @Override
    public ModVessel[] getVessels() {
        if (vesselList.isEmpty()) {
            throw new IllegalStateException("Failed to parse any vessels");
        }

        return vesselList.toArray(new ModVessel[0]);
    }

    @Override
    public boolean isAcceptable() {
        File[] files = file.listFiles();

        if (files == null) return false;

        for (File file : files) {
            if (file.getName().equals(Constants.LOADER_JSON_FILE)) {
                try (InputStream fis = new FileInputStream(file)) {
                    try (InputStreamReader reader = new InputStreamReader(fis)) {
                        JsonArray mods = parser.parse(reader).getAsJsonArray();

                        for (int i = 0; i < mods.size(); i++) {
                            JsonObject mod = mods.get(i).getAsJsonObject();
                            vesselList.add(new JsonModVessel(mod, file));
                        }
                        return true;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return false;
    }

    @Override
    public void addToClasspath(LaunchClassLoader classLoader) {
        try {
            classLoader.addURL(file.toURI().toURL());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }
}
