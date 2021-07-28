package org.bookmc.loader.impl.discoverer;

import net.minecraft.launchwrapper.hacks.LaunchWrapperHacks;
import org.bookmc.loader.api.MinecraftModDiscoverer;
import org.bookmc.loader.impl.Loader;
import org.bookmc.loader.impl.candidate.DirectoryModCandidate;
import org.bookmc.loader.impl.candidate.ZipModCandidate;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipFile;

public class ClasspathModDiscoverer implements MinecraftModDiscoverer {
    private final Map<File, Boolean> zipCache = new HashMap<>();

    private static final String DISABLED_SUFFIX = ".disabled";

    @Override
    public void discover(File[] files) {
        URL[] classpath = LaunchWrapperHacks.getClasspathURLs();

        for (URL url : classpath) {
            try {
                File file = new File(url.toURI());
                String name = file.getName();

                if (!name.endsWith(DISABLED_SUFFIX)) {
                    if (isZipFile(file)) {
                        Loader.registerCandidate(new ZipModCandidate(new File(url.toURI())));
                    } else if (file.isDirectory()) {
                        Loader.registerCandidate(new DirectoryModCandidate(file));
                    }
                }
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean isFilesRequired() {
        return false;
    }

    private boolean isZipFile(File file) {
        if (zipCache.containsKey(file)) {
            return zipCache.get(file);
        }

        try {
            new ZipFile(file);
            zipCache.put(file, true);
            return true;
        } catch (IOException e) {
            zipCache.put(file, false);
            return false;
        }
    }
}
