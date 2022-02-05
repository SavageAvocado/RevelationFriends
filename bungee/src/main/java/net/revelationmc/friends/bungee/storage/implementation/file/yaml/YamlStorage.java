package net.revelationmc.friends.bungee.storage.implementation.file.yaml;

import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import net.revelationmc.friends.bungee.model.user.User;
import net.revelationmc.friends.bungee.storage.implementation.file.AbstractFileStorage;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

public class YamlStorage extends AbstractFileStorage<Configuration> {
    private static final ConfigurationProvider PROVIDER = ConfigurationProvider.getProvider(YamlConfiguration.class);

    public YamlStorage(Path dataFolder) {
        super(dataFolder);
    }

    @Override
    public void saveUser(User user) {
    }

    @Override
    public void createUser(UUID uuid, String username) {
    }

    @Override
    public User loadUser(UUID uuid) {
        return null;
    }

    @Override
    public String getUsername(UUID uuid) {
        return null;
    }

    @Override
    public UUID getUuid(String username) {
        return null;
    }

    @Override
    protected Configuration load(Path path) {
        try (final BufferedReader reader = Files.newBufferedReader(path)) {
            return PROVIDER.load(reader);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void save(Path path, Configuration configuration) {
        try (final BufferedWriter writer = Files.newBufferedWriter(path)) {
            PROVIDER.save(configuration, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected String getFileExtension() {
        return ".yml";
    }
}
