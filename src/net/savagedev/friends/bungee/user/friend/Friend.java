package net.savagedev.friends.bungee.user.friend;

import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.UUID;

public class Friend {
    private final String username;
    private final UUID uuid;
    private final long time;

    public Friend(String username, UUID uuid, long time) {
        this.username = username;
        this.uuid = uuid;
        this.time = time;
    }

    public ProxiedPlayer getPlayer() {
        return BungeeCord.getInstance().getPlayer(this.uuid);
    }

    public String getUsername() {
        return this.username;
    }

    public UUID getUniqueId() {
        return this.uuid;
    }

    public boolean isOnline() {
        return BungeeCord.getInstance().getPlayer(this.uuid) != null;
    }

    public long getTime() {
        return this.time;
    }
}
