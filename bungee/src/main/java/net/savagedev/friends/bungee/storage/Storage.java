package net.savagedev.friends.bungee.storage;

import net.savagedev.friends.bungee.model.user.User;
import net.savagedev.friends.bungee.storage.implementation.StorageImplementation;
import net.savagedev.friends.bungee.utils.FutureUtils;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class Storage {
    private final StorageImplementation implementation;

    public Storage(StorageImplementation implementation) {
        this.implementation = implementation;
    }

    public void init() {
        this.implementation.init();
    }

    public void shutdown() {
        this.implementation.shutdown();
    }

    public CompletableFuture<Void> saveUser(User user) {
        return CompletableFuture.runAsync(() -> this.implementation.saveUser(user));
    }

    public CompletableFuture<Void> createUser(UUID uuid, String username) {
        return CompletableFuture.runAsync(() -> this.implementation.createUser(uuid, username));
    }

    public CompletableFuture<User> loadUser(UUID uuid) {
        return FutureUtils.makeFuture(() -> this.implementation.loadUser(uuid));
    }

    public CompletableFuture<String> getUsername(UUID uuid) {
        return FutureUtils.makeFuture(() -> this.implementation.getUsername(uuid));
    }

    public CompletableFuture<UUID> getUuid(String username) {
        return FutureUtils.makeFuture(() -> this.implementation.getUuid(username));
    }
}
