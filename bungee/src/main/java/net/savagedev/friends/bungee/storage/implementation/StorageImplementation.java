package net.savagedev.friends.bungee.storage.implementation;

import net.savagedev.friends.bungee.model.user.User;

import java.util.UUID;

public interface StorageImplementation {
    void init();

    void shutdown();

    void saveUser(User user);

    void createUser(UUID uuid, String username);

    User loadUser(UUID uuid);

    String getUsername(UUID uuid);

    UUID getUuid(String username);
}
