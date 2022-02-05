package net.revelationmc.friends.common.messenger;

import java.util.UUID;

public interface MessagingService {
    void init();

    void shutdown();

    void sendFriendRequest(UUID sender, UUID receiver);
}
