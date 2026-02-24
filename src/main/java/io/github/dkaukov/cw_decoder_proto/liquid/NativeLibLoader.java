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

    public static <T extends Library> T loadLiquidDsp(Class<T> interfaceClass) {
        return loadFromResource(resolveLiquidDspResourcePath(), interfaceClass);
    }

    public static <T extends Library> T loadFromResource(String resourcePath, Class<T> interfaceClass)  {
        try {
            InputStream in = NativeLibLoader.class.getClassLoader().getResourceAsStream(resourcePath);
            if (in == null) {
                throw new FileNotFoundException("Resource not found: " + resourcePath);
            }
            String suffix = getFileSuffix(resourcePath);
            Path tempFile = Files.createTempFile("libliquid", suffix);
            tempFile.toFile().deleteOnExit();
            try (OutputStream out = Files.newOutputStream(tempFile)) {
                in.transferTo(out);
            }
            return Native.load(tempFile.toAbsolutePath().toString(), interfaceClass);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static String resolveLiquidDspResourcePath() {
        String os = System.getProperty("os.name", "").toLowerCase();
        String arch = normalizeArch(System.getProperty("os.arch", ""));

        if (os.contains("mac")) {
            if ("aarch64".equals(arch)) {
                return "native/macos-aarch64/libliquid.dylib";
            }
            if ("x86_64".equals(arch)) {
                return "native/macos-x86_64/libliquid.dylib";
            }
        } else if (os.contains("linux")) {
            if ("aarch64".equals(arch)) {
                return "native/linux-aarch64/libliquid.so";
            }
            if ("x86_64".equals(arch)) {
                return "native/linux-x86_64/libliquid.so";
            }
        } else if (os.contains("win")) {
            if ("aarch64".equals(arch)) {
                return "native/windows-aarch64/liquid.dll";
            }
            if ("x86_64".equals(arch)) {
                return "native/windows-x86_64/liquid.dll";
            }
        }

        throw new IllegalStateException("Unsupported platform for bundled LiquidDSP: os=" + os + ", arch=" + arch);
    }

    private static String normalizeArch(String arch) {
        String v = arch.toLowerCase();
        if ("amd64".equals(v) || "x64".equals(v)) {
            return "x86_64";
        }
        if ("arm64".equals(v)) {
            return "aarch64";
        }
        return v;
    }

    private static String getFileSuffix(String resourcePath) {
        int dot = resourcePath.lastIndexOf('.');
        if (dot < 0 || dot == resourcePath.length() - 1) {
            return ".bin";
        }
        return resourcePath.substring(dot);
    }
}
