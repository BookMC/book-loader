package org.bookmc.loader.shared.zip;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.zip.ZipFile;

public class BetterZipFile extends ZipFile {
    private final File file;

    public BetterZipFile(Path path) throws IOException {
        super(path.toFile());
        this.file = path.toFile();
    }

    public File getFile() {
        return file;
    }
}
