package net.savagedev.friends.bungee.user;

import net.savagedev.friends.bungee.user.friend.Friend;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class User {
    private final Map<Setting, Boolean> settings;
    private final List<UUID> friendRequests;
    private final List<Friend> friends;

    User(List<Friend> friends, List<UUID> friendRequests, Map<Setting, Boolean> settings) {
        this.friendRequests = friendRequests;
        this.settings = settings;
        this.friends = friends;
    }

    public boolean isFriends(UUID uuid) {
        return this.getFriend(uuid) != null;
    }

    public void addFriend(Friend friend) {
        this.friends.add(friend);
    }

    public void removeFriend(Friend friend) {
        this.friends.remove(friend);
    }

    public void addFriendRequest(UUID uuid) {
        this.friendRequests.add(uuid);
    }

    public void removeFriendRequest(UUID uuid) {
        this.friendRequests.remove(uuid);
    }

    public void editSetting(Setting setting, boolean value) {
        this.settings.replace(setting, this.settings.get(setting), value);
    }

    public Friend getFriend(UUID uuid) {
        for (Friend friend : this.friends) {
            if (friend.getUniqueId().equals(uuid)) {
                return friend;
            }
        }

        return null;
    }

    public boolean getSetting(Setting setting) {
        return this.settings.get(setting);
    }

    public List<UUID> getFriendRequests() {
        return this.friendRequests;
    }

    public Map<Setting, Boolean> getSettings() {
        return this.settings;
    }

    public List<Friend> getFriends() {
        return this.friends;
    }

    public enum Setting {
        REQUESTS,
        JOIN_MESSAGES,
        LEAVE_MESSAGES,
        SERVER_SWITCH_MESSAGES
    }
}
