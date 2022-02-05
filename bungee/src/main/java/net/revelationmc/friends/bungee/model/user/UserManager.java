package net.revelationmc.friends.bungee.model.user;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.revelationmc.friends.bungee.utils.FutureUtils;
import net.revelationmc.friends.bungee.RevelationFriendsPlugin;

import java.util.*;
import java.util.concurrent.CompletableFuture;

public class UserManager {
    private final Map<UUID, User> userMap = new HashMap<>();
    private final Set<UUID> loggingIn = new HashSet<>();

    private final RevelationFriendsPlugin revelationFriends;

    public UserManager(RevelationFriendsPlugin revelationFriends) {
        this.revelationFriends = revelationFriends;
    }

    public CompletableFuture<User> getOrLoad(UUID uuid) {
        return FutureUtils.makeFuture(() -> {
            if (!this.userMap.containsKey(uuid)) {
                this.userMap.put(uuid, this.revelationFriends.getStorage().loadUser(uuid).join());
            }
            return this.userMap.get(uuid);
        });
    }

    public User unload(UUID uuid) {
        return this.userMap.remove(uuid);
    }

    public Set<UUID> getLoggingIn() {
        return this.loggingIn;
    }

    public CompletableFuture<String> getUsername(UUID uuid) {
        final ProxiedPlayer player = this.revelationFriends.getProxy().getPlayer(uuid);
        if (player == null) {
            return this.revelationFriends.getUserManager().getUsername(uuid);
        }
        return FutureUtils.makeFuture(player::getName);
    }

    public CompletableFuture<UUID> getUuid(String username) {
        final ProxiedPlayer player = this.revelationFriends.getProxy().getPlayer(username);
        if (player == null) {
            return this.revelationFriends.getStorage().getUuid(username);
        }
        return FutureUtils.makeFuture(player::getUniqueId);
    }
}
