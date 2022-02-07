package net.revelationmc.friends.bungee.model.friend;

import java.util.Date;
import java.util.UUID;

public class FriendRequest {
    private final UUID senderId;
    private final Date dateCreated;

    public FriendRequest(UUID senderId, Date dateCreated) {
        this.senderId = senderId;
        this.dateCreated = dateCreated;
    }

    public UUID getSenderId() {
        return this.senderId;
    }

    public Date getDateCreated() {
        return this.dateCreated;
    }
}
