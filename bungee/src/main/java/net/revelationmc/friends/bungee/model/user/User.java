package net.revelationmc.friends.bungee.model.user;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.revelationmc.friends.bungee.model.friend.Friend;
import net.revelationmc.friends.bungee.model.friend.FriendRequest;

import java.util.*;

public class User {
    private final String username;
    private final UUID uuid;

    private final SettingsMap settings = new SettingsMap();
    private final Map<UUID, FriendRequest> requests = new HashMap<>();
    private final Map<UUID, Friend> friends = new HashMap<>();

    public User(String username, UUID uuid) {
        this.username = username;
        this.uuid = uuid;
    }

    public void addFriendRequest(FriendRequest request) {
        this.requests.put(request.getSenderUuid(), request);
    }

    public void removeFriendRequest(FriendRequest request) {
        this.requests.remove(request.getSenderUuid());
    }

    public void addFriend(FriendRequest request) {
        this.friends.put(request.getSenderUuid(), new Friend(request.getSenderUuid(), request.getSenderName(), request.getSenderUuid(), System.currentTimeMillis()));
    }

    public void addFriend(ProxiedPlayer player) {
        this.friends.put(player.getUniqueId(), new Friend(player.getUniqueId(), player.getName(), player.getUniqueId(), System.currentTimeMillis()));
    }

    public void addFriend(Friend friend) {
        this.friends.put(friend.getUuid(), friend);
    }

    public void removeFriend(UUID uuid) {
        this.friends.remove(uuid);
    }

    public boolean isFriendsWith(UUID uuid) {
        return this.friends.containsKey(uuid);
    }

    public boolean hasRequestFrom(UUID uuid) {
        return this.requests.containsKey(uuid);
    }

    public FriendRequest getFriendRequest(UUID uuid) {
        return this.requests.get(uuid);
    }

    public Friend getFriend(UUID uuid) {
        return this.friends.get(uuid);
    }

    public String getUsername() {
        return this.username;
    }

    public UUID getUuid() {
        return this.uuid;
    }

    public SettingsMap getSettings() {
        return this.settings;
    }

    public Collection<FriendRequest> getFriendRequests() {
        return this.requests.values();
    }

    public Collection<Friend> getFriends() {
        return this.friends.values();
    }

    public static final class SettingsMap implements Map<UserSetting, Boolean> {
        private final Map<UserSetting, Boolean> map = new HashMap<>();

        public SettingsMap() {
            for (UserSetting setting : UserSetting.values()) {
                this.map.put(setting, setting.getDefault());
            }
        }

        public void set(UserSetting setting, boolean value) {
            this.map.replace(setting, value);
        }

        @Override
        public int size() {
            return UserSetting.values().length;
        }

        @Override
        public boolean isEmpty() {
            return false;
        }

        @Override
        public boolean containsKey(Object key) {
            return key instanceof UserSetting;
        }

        @Override
        public boolean containsValue(Object value) {
            return value instanceof Boolean;
        }

        @Override
        public Boolean get(Object key) {
            return this.map.get(key);
        }

        @Override
        public Boolean put(UserSetting key, Boolean value) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Boolean remove(Object key) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void putAll(Map<? extends UserSetting, ? extends Boolean> m) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void clear() {
            throw new UnsupportedOperationException();
        }

        @Override
        public Set<UserSetting> keySet() {
            return this.map.keySet();
        }

        @Override
        public Collection<Boolean> values() {
            return this.map.values();
        }

        @Override
        public Set<Entry<UserSetting, Boolean>> entrySet() {
            return this.map.entrySet();
        }
    }
}
