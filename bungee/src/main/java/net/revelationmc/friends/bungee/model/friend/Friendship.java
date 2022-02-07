package net.revelationmc.friends.bungee.model.friend;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public class Friendship {
    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("MMM d, yyy");

    private final UUID friendId;
    private final Date dateCreated;

    private final boolean initiator;

    public Friendship(UUID friendId, Date dateCreated, boolean initiator) {
        this.friendId = friendId;
        this.dateCreated = dateCreated;
        this.initiator = initiator;
    }

    /**
     * Get the UUID of this friend.
     *
     * @return - The friend's UUID.
     */
    public UUID getFriendId() {
        return this.friendId;
    }

    /**
     * Get the date this friendship started.
     *
     * @return - The date this friendship stared.
     */
    public Date getDateCreated() {
        return this.dateCreated;
    }

    /**
     * Gets a prettier formatted date.
     *
     * @return - A formatted string of the date created.
     */
    public String getFormattedDateCreated() {
        return DATE_FORMAT.format(this.dateCreated);
    }

    /**
     * If this friend sent the friend the request.
     *
     * @return - Whether this friend sent the friend request.
     */
    public boolean isInitiator() {
        return this.initiator;
    }
}
