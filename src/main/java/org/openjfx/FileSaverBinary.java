package org.openjfx;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileSaverBinary {


    public ObjectOutputStream saveFileBinary(Path path) throws IOException {
        OutputStream os = Files.newOutputStream(path);
        ObjectOutputStream out = new ObjectOutputStream(os);
        return out;
    }


}
