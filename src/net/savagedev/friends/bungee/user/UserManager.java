package net.savagedev.friends.bungee.user;

import net.md_5.bungee.config.Configuration;
import net.savagedev.friends.bungee.RevelationFriends;
import net.savagedev.friends.bungee.user.friend.Friend;
import net.savagedev.friends.bungee.utils.io.FileUtils;

import java.io.File;
import java.util.*;
import java.util.logging.Level;

public class UserManager {
    private final RevelationFriends plugin;
    private final Map<UUID, User> users;
    private final Configuration usermap;
    private final File usermap_file;
    private final List<UUID> login;

    public UserManager(RevelationFriends plugin) {
        this.usermap_file = new File(plugin.getDataFolder(), "storage/usermap.yml");
        FileUtils.create(this.usermap_file);

        this.usermap = FileUtils.load(this.usermap_file);
        this.login = new ArrayList<>();
        this.users = new HashMap<>();
        this.plugin = plugin;
        this.init();
    }

    private void init() {
    }

    public void create(UUID uuid) {
        FileUtils.create("defaults.yml", new File(this.plugin.getDataFolder(), String.format("storage/%s.yml", uuid.toString())));
    }

    public void load(UUID uuid) {
        File file = new File(this.plugin.getDataFolder(), String.format("storage/%s.yml", uuid.toString()));
        Configuration storageFile = FileUtils.load(file);

        if (storageFile == null) {
            this.plugin.getProxy().getLogger().log(Level.WARNING, String.format("[RevelationFriends] Unable to load data of %s! Data file is null.", uuid.toString()));
            return;
        }

        Map<User.Setting, Boolean> settings = new HashMap<>();

        for (String setting : storageFile.getSection("settings").getKeys()) {
            settings.putIfAbsent(User.Setting.valueOf(setting.toUpperCase()), storageFile.getBoolean(String.format("settings.%s", setting)));
        }

        List<Friend> friends = new ArrayList<>();

        for (String friend_uuid : storageFile.getSection("friends").getKeys()) {
            friends.add(new Friend(storageFile.getString(String.format("friends.%s.username", friend_uuid)), UUID.fromString(friend_uuid), storageFile.getLong(String.format("friends.%s.time", friend_uuid))));
        }

        List<UUID> friend_requests = new ArrayList<>();

        for (String requester_uuid : storageFile.getStringList("requests")) {
            friend_requests.add(UUID.fromString(requester_uuid));
        }

        this.users.putIfAbsent(uuid, new User(friends, friend_requests, settings));
    }

    public void save(UUID uuid) {
        File file = new File(this.plugin.getDataFolder(), String.format("storage/%s.yml", uuid.toString()));
        Configuration storageFile = FileUtils.load(file);

        if (storageFile == null) {
            this.plugin.getProxy().getLogger().log(Level.WARNING, String.format("[RevelationFriends] Unable to save data of %s! Data file is null.", uuid.toString()));
            return;
        }

        User user = this.get(uuid);

        for (User.Setting setting : user.getSettings().keySet()) {
            storageFile.set(String.format("settings.%s", setting.name().toLowerCase()), user.getSetting(setting));
        }

        List<String> requests = new ArrayList<>();

        for (UUID requester_uuid : user.getFriendRequests()) {
            requests.add(requester_uuid.toString());
        }

        storageFile.set("requests", requests);
        storageFile.set("friends", null);

        for (Friend friend : user.getFriends()) {
            storageFile.set(String.format("friends.%s.username", friend.getUniqueId()), friend.getUsername());
            storageFile.set(String.format("friends.%s.time", friend.getUniqueId()), friend.getTime());
        }

        FileUtils.save(storageFile, file);
    }

    public void update(UUID uuid, String username) {
        String old_username = this.usermap.getString(uuid.toString());

        if (old_username != null && !old_username.equals(username)) {
            this.usermap.set(old_username, null);
        }

        this.usermap.set(uuid.toString(), username);
        this.usermap.set(username, uuid.toString());
        FileUtils.save(this.usermap, this.usermap_file);
    }

    public void unCache(UUID uuid) {
        this.users.remove(uuid);
    }

    public void addLogin(UUID uuid) {
        this.login.add(uuid);
    }

    public void removeLogin(UUID uuid) {
        this.login.remove(uuid);
    }

    public boolean isLogin(UUID uuid) {
        return this.login.contains(uuid);
    }

    public UUID getUniqueId(String username) {
        if (this.plugin.getProxy().getPlayer(username) != null) {
            return this.plugin.getProxy().getPlayer(username).getUniqueId();
        }

        String uuid_str = this.usermap.getString(username);

        if (uuid_str == null || uuid_str.trim().equals("")) {
            this.plugin.getProxy().getLogger().log(Level.WARNING, String.format("[RevelationFriends] Was unable to fetch UUID of %s.", username));
            return null;
        }

        return UUID.fromString(uuid_str);
    }

    public String getUsername(UUID uuid) {
        if (this.plugin.getProxy().getPlayer(uuid) != null) {
            return this.plugin.getProxy().getPlayer(uuid).getName();
        }

        String username = this.usermap.getString(uuid.toString());

        if (username == null) {
            this.plugin.getProxy().getLogger().log(Level.WARNING, String.format("[RevelationFriends] Was unable to fetch username of %s.", uuid.toString()));
            return null;
        }

        return username;
    }

    public boolean hasRequestFrom(UUID uuid, UUID target) {
        if (this.get(target) != null) {
            return this.get(target).getFriendRequests().contains(uuid);
        }

        Configuration storageFile = FileUtils.load(new File(this.plugin.getDataFolder(), String.format("storage/%s.yml", target.toString())));

        if (storageFile == null) {
            this.plugin.getProxy().getLogger().log(Level.WARNING, String.format("[RevelationFriends] Was unable to get requests of %s.", target.toString()));
            return false;
        }

        List<String> requests = storageFile.getStringList("requests");
        return requests.contains(uuid.toString());
    }

    public boolean isAllowingFriendRequests(UUID uuid) {
        if (this.get(uuid) != null) {
            return this.get(uuid).getSetting(User.Setting.REQUESTS);
        }

        Configuration storageFile = FileUtils.load(new File(this.plugin.getDataFolder(), String.format("storage/%s.yml", uuid.toString())));

        if (storageFile == null) {
            this.plugin.getProxy().getLogger().log(Level.WARNING, String.format("[RevelationFriends] Was unable to get settings of %s.", uuid.toString()));
            return false;
        }

        return storageFile.getBoolean("settings.requests");
    }

    public User get(UUID uuid) {
        return this.users.get(uuid);
    }
}
