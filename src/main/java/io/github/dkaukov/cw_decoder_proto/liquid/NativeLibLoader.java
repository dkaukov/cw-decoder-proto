package io.github.dkaukov.cw_decoder_proto.liquid;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import com.sun.jna.Library;
import com.sun.jna.Native;

public final class NativeLibLoader {
    private NativeLibLoader() {}

    public static <T extends Library> T loadFromResource(String resourcePath, Class<T> interfaceClass)  {
        try {
            InputStream in = NativeLibLoader.class.getClassLoader().getResourceAsStream(resourcePath);
            if (in == null) {
                throw new FileNotFoundException("Resource not found: " + resourcePath);
            }
            Path tempFile = Files.createTempFile("libliquid", ".dylib");
            tempFile.toFile().deleteOnExit();
            try (OutputStream out = Files.newOutputStream(tempFile)) {
                in.transferTo(out);
            }
            return Native.load(tempFile.toAbsolutePath().toString(), interfaceClass);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}