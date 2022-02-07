package net.revelationmc.friends.bungee.storage.implementation;

import net.revelationmc.friends.bungee.model.friend.Friendship;
import net.revelationmc.friends.bungee.model.friend.FriendRequest;
import net.revelationmc.friends.bungee.model.user.UserSetting;

import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public interface StorageImplementation {
    void init();

    void shutdown();

    /**
     * Create default user settings.
     *
     * @param id - The id of the user to create settings for.
     */
    void createDefaultsIfNotExists(UUID id);

    /**
     * Create a new friend request.
     *
     * @param holderId    - The receiver of the request.
     * @param senderId    - The sender of the request.
     * @param dateCreated - The date/time the request was sent.
     */
    FriendRequest createFriendRequest(UUID holderId, UUID senderId, Date dateCreated);

    /**
     * Delete a friend request.
     *
     * @param holderId - The receiver of the request.
     * @param senderId - The sender of the request.
     */
    void deleteFriendRequest(UUID holderId, UUID senderId);

    /**
     * Delete all of a user's friend requests.
     *
     * @param holderId - The user whose friend requests to delete.
     */
    void deleteAllFriendRequests(UUID holderId);

    /**
     * Create a new friendship.
     *
     * @param holderId    - The holder of the friendship.
     * @param friendId    - The holder's friend.
     * @param sender      - Did the friend send the request?
     * @param dateCreated - The date/time the request was formed.
     */
    Friendship createFriendship(UUID holderId, UUID friendId, boolean sender, Date dateCreated);

    /**
     * Delete a friendship.
     *
     * @param holderId - The holder of the friendship.
     * @param friendId - The holder's friend.
     */
    void deleteFriendship(UUID holderId, UUID friendId);

    /**
     * Delete all of a user's friendships.
     *
     * @param holderId - The user whose friendships to delete.
     */
    void deleteAllFriendships(UUID holderId);

    /**
     * Update a player's setting value.
     *
     * @param id      - The player.
     * @param setting - The setting.
     * @param value   - The new value of the setting.
     */
    void updateSetting(UUID id, UserSetting setting, boolean value);

    /**
     * Get a friend request.
     *
     * @param holderId - The holder of the friend request.
     * @param senderId - The sender of the friend request.
     * @return - The friend request.
     */
    FriendRequest getFriendRequest(UUID holderId, UUID senderId);

    /**
     * Get all of a user's friend requests.
     *
     * @param holderId - The user whose friend requests to get.
     * @return - A set containing the user's received friend requests.
     */
    Set<FriendRequest> getAllFriendRequests(UUID holderId);

    /**
     * Get all of a user's friendships.
     *
     * @param holderId - The holder of the friendships.
     * @return - A set containing the user's friendships.
     */
    Set<Friendship> getAllFriendships(UUID holderId);

    /**
     * Get the value of a user's setting.
     *
     * @param id      - The id of the user.
     * @param setting - The type of setting.
     * @return - The value of the provided setting.
     */
    boolean getSetting(UUID id, UserSetting setting);

    /**
     * Get all of a user's settings.
     *
     * @param id - The id of the user.
     * @return - A map containing a user's settings.
     */
    Map<UserSetting, Boolean> getAllSettings(UUID id);
}
