package org.bookmc.loader.impl.loader.resolver;

import org.bookmc.loader.api.mod.ModCandidate;
import org.bookmc.loader.api.mod.resolution.ModResolver;
import org.bookmc.loader.impl.loader.candidate.ZipModCandidate;
import org.bookmc.loader.shared.zip.BetterZipFile;
import org.bookmc.loader.shared.zip.ZipUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public record FolderModResolver(Path folder) implements ModResolver, Predicate<Path> {
    @Override
    public ModCandidate[] resolveMods() {
        if (!Files.isDirectory(folder)) {
            throw new IllegalStateException("The folder provided (" + folder + ") is not a folder!");
        }

        try {
            List<ModCandidate> candidates = new ArrayList<>();

            List<Path> zipMods = Files.walk(folder)
                .filter(this)
                .collect(Collectors.toList());

            for (Path zipMod : zipMods) {
                candidates.add(new ZipModCandidate(new BetterZipFile(zipMod)));
            }
            return candidates.toArray(new ModCandidate[0]);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new ModCandidate[0];
    }

    @Override
    public boolean test(Path path) {
        return ZipUtils.isZipFile(path);
    }
}