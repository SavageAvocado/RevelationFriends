package net.savagedev.friends.bungee.model.friend;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;

import java.util.Optional;
import java.util.UUID;

public class Friend implements Identifiable {
    private final long added;

    private final UUID senderUuid;

    private final String username;
    private final UUID uuid;

    public Friend(UUID uuid, String username, UUID senderUuid, long added) {
        this.uuid = uuid;
        this.username = username;
        this.senderUuid = senderUuid;
        this.added = added;
    }

    public String getUsername() {
        return this.username;
    }

    public UUID getSenderUuid() {
        return this.senderUuid;
    }

    public boolean isOnline() {
        return ProxyServer.getInstance().getPlayer(this.uuid) != null;
    }

    public long getAdded() {
        return this.added;
    }

    public Optional<ServerInfo> getServer() {
        if (this.isOnline()) {
            return Optional.of(ProxyServer.getInstance().getPlayer(this.uuid).getServer().getInfo());
        }
        return Optional.empty();
    }

    @Override
    public UUID getUuid() {
        return this.uuid;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Friend) {
            return this.getUuid().equals(((Friend) o).getUuid());
        }
        return false;
    }
}
