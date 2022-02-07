package net.revelationmc.friends.bungee.storage;

import net.revelationmc.friends.bungee.storage.implementation.StorageImplementation;

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
}
