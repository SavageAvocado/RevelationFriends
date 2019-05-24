package net.savagedev.friends.bungee.utils.io;

import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;

public class FileUtils {
    public static void save(Configuration configuration, File file) {
        try {
            YamlConfiguration.getProvider(YamlConfiguration.class).save(configuration, file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean create(String name, File file) {
        if (!create(file)) {
            return false;
        }

        InputStream inputStream = getResource(name);
        if (inputStream == null) {
            BungeeCord.getInstance().getLogger().severe(String.format("Failed to save resource %s. InputStream was null.", name));
            return false;
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream)); FileWriter writer = new FileWriter(file)) {
            String line;
            while ((line = reader.readLine()) != null) {
                writer.write(line + "\n");
            }

            writer.flush();
        } catch (IOException e) {
            BungeeCord.getInstance().getLogger().severe(String.format("Failed to save resource %s. %s", name, e.getMessage()));
        }

        return true;
    }

    public static boolean create(File file) {
        if (file.exists()) {
            return false;
        }

        file.getParentFile().mkdir();

        try {
            file.createNewFile();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static Configuration load(File file) {
        try {
            return YamlConfiguration.getProvider(YamlConfiguration.class).load(file);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static InputStream getResource(String name) {
        URL resourceStream = FileUtils.class.getClassLoader().getResource(name);

        if (resourceStream == null) {
            return null;
        }

        try {
            URLConnection connection = resourceStream.openConnection();
            connection.setUseCaches(false);

            return connection.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
