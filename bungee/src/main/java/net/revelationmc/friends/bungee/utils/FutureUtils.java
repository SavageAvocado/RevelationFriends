package net.revelationmc.friends.bungee.utils;

import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;

public final class FutureUtils {
    private FutureUtils() {
        throw new UnsupportedOperationException();
    }

    public static <T> CompletableFuture<T> makeFuture(Callable<T> callable) {
        final CompletableFuture<T> future = new CompletableFuture<>();
        try {
            future.complete(callable.call());
        } catch (Exception e) {
            future.completeExceptionally(e);
        }
        return future;
    }
}
