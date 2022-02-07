package net.revelationmc.friends.bungee.model.user;

import com.google.common.collect.ImmutableSet;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.revelationmc.friends.bungee.model.friend.Friendship;
import net.revelationmc.friends.bungee.model.friend.FriendRequest;

import java.util.*;

public class User {
    private final Map<UserSetting, Boolean> settingsMap = new HashMap<>();

    private final Set<FriendRequest> requests;
    private final Set<Friendship> friendships;

    private final ProxiedPlayer player;

    public User(ProxiedPlayer player, Set<FriendRequest> requests, Set<Friendship> friendships) {
        this.player = player;
        this.requests = requests;
        this.friendships = friendships;
    }

    public void addFriendRequest(FriendRequest request) {
        this.requests.add(request);
    }

    public void removeFriendRequest(UUID senderId) {
        this.requests.removeIf(request -> request.getSenderId().equals(senderId));
    }

    public void addFriendship(Friendship friendship) {
        this.friendships.add(friendship);
    }

    public void removeFriendship(UUID friendId) {
        this.friendships.removeIf(friendship -> friendship.getFriendId().equals(friendId));
    }

    public Map<UserSetting, Boolean> getSettingsMap() {
        return this.settingsMap;
    }

    public Set<FriendRequest> getFriendRequests() {
        return ImmutableSet.copyOf(this.requests);
    }

    public Set<Friendship> getFriendships() {
        return ImmutableSet.copyOf(this.friendships);
    }

    public ProxiedPlayer getPlayer() {
        return this.player;
    }
}
