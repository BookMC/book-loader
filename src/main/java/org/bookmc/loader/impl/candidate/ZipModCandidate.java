package org.bookmc.loader.impl.candidate;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.bookmc.loader.api.candidate.ModCandidate;
import org.bookmc.loader.api.classloader.ClassLoaderURLAppender;
import org.bookmc.loader.api.vessel.ModVessel;
import org.bookmc.loader.impl.vessel.JsonModVessel;
import org.bookmc.loader.shared.Constants;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class ZipModCandidate implements ModCandidate {
    private final File file;

    private final List<ModVessel> vessels = new ArrayList<>();
    private final JsonParser parser = new JsonParser();

    public ZipModCandidate(File file) {
        this.file = file;
    }

    @Override
    public ModVessel[] getVessels() {
        if (vessels.isEmpty()) {
            throw new IllegalStateException("Could not read metadata from the mod therefore discarding it.");
        }

        return vessels.toArray(new ModVessel[0]);
    }

    @Override
    public boolean isAcceptable() {
        try {
            ZipFile zipFile = new ZipFile(file);
            Enumeration<? extends ZipEntry> entries = zipFile.entries();

            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();

                if (entry.getName().equals(Constants.LOADER_JSON_FILE)) {
                    try (InputStream inputStream = zipFile.getInputStream(entry)) {
                        try (InputStreamReader inputStreamReader = new InputStreamReader(inputStream)) {
                            JsonArray mods = parser.parse(inputStreamReader).getAsJsonArray();

                            for (int i = 0; i < mods.size(); i++) {
                                JsonObject mod = mods.get(i).getAsJsonObject();
                                vessels.add(new JsonModVessel(mod, file));
                            }
                        }
                    }
                    return true;
                }
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }

        return false;
    }

    @Override
    public void addToClasspath(ClassLoaderURLAppender appender) {
        try {
            appender.add(file.toURI().toURL());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }
}
