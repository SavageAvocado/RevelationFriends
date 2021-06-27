package net.savagedev.friends.bungee.storage.implementation.file;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import net.savagedev.friends.bungee.storage.implementation.StorageImplementation;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public abstract class AbstractFileStorage<T> implements StorageImplementation {
    private final Cache<String, T> cache = CacheBuilder.newBuilder()
            .expireAfterAccess(5L, TimeUnit.MINUTES)
            .maximumSize(100)
            .build();

    private final Path dataFolder;

    public AbstractFileStorage(Path dataFolder) {
        this.dataFolder = dataFolder;
    }

    @Override
    public void init() {
        if (Files.notExists(this.dataFolder)) {
            try {
                Files.createDirectories(this.dataFolder);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void shutdown() {
        this.cache.invalidateAll();
    }

    protected abstract void save(Path path, T t);

    protected abstract T load(Path path);

    protected abstract String getFileExtension();

    protected boolean exists(String file) {
        return Files.exists(this.dataFolder.resolve(this.sanitize(file)));
    }

    protected T getOrCreate(String file) {
        final String sanitizedFile = this.sanitize(file);
        try {
            return this.cache.get(sanitizedFile, () -> {
                final Path path = this.dataFolder.resolve(sanitizedFile);
                if (Files.notExists(path)) {
                    Files.createFile(path);
                }
                return this.load(path);
            });
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String sanitize(String file) {
        if (file.endsWith(this.getFileExtension())) {
            return file;
        }
        return file + this.getFileExtension();
    }
}
