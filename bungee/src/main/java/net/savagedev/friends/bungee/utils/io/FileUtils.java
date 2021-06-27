package net.savagedev.friends.bungee.utils.io;

import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;

public final class FileUtils {
    private static final ConfigurationProvider PROVIDER = ConfigurationProvider.getProvider(YamlConfiguration.class);

    private FileUtils() {
        throw new UnsupportedOperationException();
    }


    public static void copy(String name, Path path) throws IOException {
        if (Files.notExists(path)) {
            Files.createDirectories(path.getParent());
            Files.createFile(path);
        }

        final InputStream stream = FileUtils.getResource(name);
        if (stream == null) {
            throw new IOException("Cannot create file. Resource stream null!");
        }

        Files.copy(stream, path);
    }

    public static void save(Configuration configuration, Path path) {
        try (final BufferedWriter writer = Files.newBufferedWriter(path)) {
            PROVIDER.save(configuration, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Configuration load(Path path) {
        try (final BufferedReader reader = Files.newBufferedReader(path)) {
            return PROVIDER.load(reader);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static InputStream getResource(String name) {
        final URL url = FileUtils.class.getClassLoader().getResource(name);
        if (url != null) {
            try {
                final URLConnection connection = url.openConnection();
                connection.setUseCaches(false);

                return connection.getInputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
