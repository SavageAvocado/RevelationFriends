package net.savagedev.friends.bungee.model.friend;

import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.UUID;

public class FriendRequest implements Identifiable {
    private final long timeSent;

    private final String senderName;
    private final UUID senderUuid;

    private final UUID target;

    public FriendRequest(ProxiedPlayer sender, UUID target) {
        this(sender, target, System.currentTimeMillis());
    }

    public FriendRequest(ProxiedPlayer sender, UUID target, long timeSent) {
        this(sender.getName(), sender.getUniqueId(), target, timeSent);
    }

    public FriendRequest(String senderName, UUID senderUuid, UUID target, long timeSent) {
        this.senderName = senderName;
        this.senderUuid = senderUuid;
        this.target = target;
        this.timeSent = timeSent;
    }

    public String getSenderName() {
        return this.senderName;
    }

    public UUID getSenderUuid() {
        return this.senderUuid;
    }

    public UUID getTarget() {
        return this.target;
    }

    public long getTimeSent() {
        return this.timeSent;
    }

    @Override
    public UUID getUuid() {
        return this.target;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof FriendRequest) {
            return this.getSenderUuid().equals(((FriendRequest) o).getSenderUuid());
        }
        return false;
    }
}
