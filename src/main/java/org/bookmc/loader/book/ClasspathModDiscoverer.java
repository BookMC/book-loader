package org.bookmc.loader.book;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.launchwrapper.hacks.LaunchWrapperHacks;
import org.bookmc.loader.Loader;
import org.bookmc.loader.MinecraftModDiscoverer;
import org.bookmc.loader.vessel.json.JsonModVessel;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class ClasspathModDiscoverer implements MinecraftModDiscoverer {
    @Override
    public void discover(File[] files) {
        URL[] classpath = LaunchWrapperHacks.getClasspathURLs();

        for (URL url : classpath) {
            try {
                if (url.getPath().endsWith(".jar") || url.getPath().endsWith(".zip")) {
                    addConnection(url);
                }
            } catch (IOException | URISyntaxException e) {
                e.printStackTrace();
            }
        }
    }

    private void addConnection(URL url) throws URISyntaxException, IOException {
        File file = new File(url.toURI());
        try (ZipFile zip = new ZipFile(file)) {
            Enumeration<? extends ZipEntry> entries = zip.entries();

            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();

                if (entry.getName().equals("book.mod.json")) {
                    try (InputStream stream = zip.getInputStream(entry)) {
                        try (InputStreamReader inputStreamReader = new InputStreamReader(stream)) {
                            JsonArray mods = parser.parse(inputStreamReader).getAsJsonArray();

                            for (int i = 0; i < mods.size(); i++) {
                                JsonObject mod = mods.get(i).getAsJsonObject();
                                if (!Loader.getModVesselsMap().containsKey(mod.get("id").getAsString())) {
                                    Loader.registerVessel(new JsonModVessel(mod, file));
                                }
                            }
                        }
                    }
                    break;
                }
            }
        }
    }
}
