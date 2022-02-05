package net.revelationmc.friends.bungee.storage.implementation.sql.mysql;

import net.revelationmc.friends.bungee.model.friend.Friend;
import net.revelationmc.friends.bungee.model.friend.FriendRequest;
import net.revelationmc.friends.bungee.model.user.UserSetting;
import net.revelationmc.friends.bungee.model.user.User;
import net.revelationmc.friends.bungee.storage.implementation.sql.AbstractSqlStorage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class MySqlStorage extends AbstractSqlStorage {
    private static final String CREATE_USER_DATA_TABLE = "CREATE TABLE IF NOT EXISTS user_data (id VARCHAR(36) PRIMARY KEY NOT NULL, username VARCHAR(16), firstOnline LONG, lastOnline LONG);";
    private static final String CREATE_FRIEND_DATA_TABLE = "CREATE TABLE IF NOT EXISTS friend_data (id VARCHAR(36) PRIMARY KEY NOT NULL, friendId VARCHAR(36), requesterId VARCHAR(36), timeAccepted LONG);";
    private static final String CREATE_FRIEND_REQUEST_DATA_TABLE = "CREATE TABLE IF NOT EXISTS friend_request_data (id VARCHAR(36) PRIMARY KEY NOT NULL, senderId VARCHAR(36), timeSent LONG);";
    private static final String CREATE_SETTINGS_DATA_TABLE = "CREATE TABLE IF NOT EXISTS settings_data (id VARCHAR(36) PRIMARY KEY NOT NULL, setting ENUM('SERVER_CHANGE_MESSAGES', 'QUIT_MESSAGES', 'JOIN_MESSAGES', 'ALLOW_FRIEND_REQUESTS'), settingValue BOOLEAN);";

    private static final String CREATE_USER_DATA = "INSERT INTO user_data (id, username, firstOnline, lastOnline) VALUES (?, ?, ?, ?) ON DUPLICATE KEY UPDATE username = ?, lastOnline = ?;";
    private static final String CREATE_SETTINGS_DATA = "INSERT INTO settings_data (id, setting, settingValue) VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE setting = ?;";

    private static final String SELECT_SETTINGS_DATA = "SELECT * FROM settings_data WHERE id = ?;";
    private static final String SELECT_FRIEND_REQUEST_DATA = "SELECT * FROM friend_request_data WHERE id = ?;";
    private static final String SELECT_FRIEND_DATA = "SELECT * FROM friend_data WHERE id = ?;";

    private static final String DELETE_FRIEND_DATA = "DELETE FROM friend_data WHERE friendId = ?;";

    private static final String INSERT_FRIEND_DATA = "INSERT INTO friend_data (id, friendId, requesterId, timeAccepted) VALUES (?, ?, ?, ?);";
    private static final String INSERT_FRIEND_REQUEST_DATA = "INSERT INTO friend_request_data (id, friendId, requesterId, timeAccepted) VALUES (?, ?, ?, ?);";
    private static final String INSERT_SETTINGS_DATA = "INSERT INTO settings_data (id, setting, settingValue) VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE setting = ?, settingValue = ?;";

    public MySqlStorage(String host, int port, String database, String username, String password) {
        super(host, port, database, username, password);
    }

    @Override
    protected void setupTables() {
        try (final Connection connection = this.getConnection()) {
            try (final PreparedStatement statement = connection.prepareStatement(CREATE_USER_DATA_TABLE)) {
                statement.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            try (final PreparedStatement statement = connection.prepareStatement(CREATE_FRIEND_DATA_TABLE)) {
                statement.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            try (final PreparedStatement statement = connection.prepareStatement(CREATE_FRIEND_REQUEST_DATA_TABLE)) {
                statement.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            try (final PreparedStatement statement = connection.prepareStatement(CREATE_SETTINGS_DATA_TABLE)) {
                statement.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void saveUser(User user) {
        try (final Connection connection = this.getConnection()) {
            final Set<UUID> friendsSnapshot = this.getFriends(connection, user.getUuid()).stream().map(Friend::getUuid).collect(Collectors.toSet());
            final Set<UUID> friendsCache = user.getFriends().stream().map(Friend::getUuid).collect(Collectors.toSet());

            final Set<UUID> toRemove = new HashSet<>(friendsSnapshot);
            toRemove.removeAll(friendsCache);
            for (UUID friendId : toRemove) {
                try (final PreparedStatement statement = connection.prepareStatement(DELETE_FRIEND_DATA)) {
                    statement.setString(1, friendId.toString());
                    statement.executeUpdate();
                }
            }
            final Set<UUID> toAdd = new HashSet<>(friendsCache);
            friendsSnapshot.removeAll(toRemove);
            toAdd.removeAll(friendsSnapshot);
            for (UUID friendId : toAdd) {
                final Friend friend = user.getFriend(friendId);
                try (final PreparedStatement statement = connection.prepareStatement(INSERT_FRIEND_DATA)) {
                    statement.setString(1, user.getUuid().toString());
                    statement.setString(2, friendId.toString());
                    statement.setString(3, friend.getSenderUuid().toString());
                    statement.setLong(4, friend.getAdded());
                    statement.executeUpdate();
                }
            }
            // TODO: Save friend requests.
            for (Map.Entry<UserSetting, Boolean> entry : user.getSettings().entrySet()) {
                try (final PreparedStatement statement = connection.prepareStatement(INSERT_SETTINGS_DATA)) {
                    statement.setString(1, user.getUuid().toString());
                    statement.setString(2, entry.getKey().name());
                    statement.setBoolean(3, entry.getValue());
                    statement.setString(4, entry.getKey().name());
                    statement.setBoolean(5, entry.getValue());
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void createUser(UUID uuid, String username) {
        try (final Connection connection = this.getConnection()) {
            try (final PreparedStatement statement = connection.prepareStatement(CREATE_USER_DATA)) {
                statement.setString(1, uuid.toString());
                statement.setString(2, username);
                final long currentTime = System.currentTimeMillis();
                statement.setLong(3, currentTime);
                statement.setLong(4, currentTime);
                statement.setString(5, username);
                statement.setLong(6, currentTime);
                statement.executeUpdate();
            }
            for (UserSetting setting : UserSetting.values()) {
                try (final PreparedStatement statement = connection.prepareStatement(CREATE_SETTINGS_DATA)) {
                    statement.setString(1, uuid.toString());
                    statement.setString(2, setting.name());
                    statement.setBoolean(3, setting.getDefault());
                    statement.setString(4, setting.name());
                    statement.executeUpdate();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public User loadUser(UUID uuid) {
        try (final Connection connection = this.getConnection()) {
            final User user = new User(this.getUsername(connection, uuid), uuid);
            try (final PreparedStatement statement = connection.prepareStatement(SELECT_SETTINGS_DATA)) {
                statement.setString(1, uuid.toString());
                try (final ResultSet result = statement.executeQuery()) {
                    while (result.next()) {
                        user.getSettings().set(UserSetting.valueOf(result.getString("setting")), result.getBoolean("settingValue"));
                    }
                }
            }
            for (FriendRequest request : this.getFriendRequests(connection, uuid)) {
                user.addFriendRequest(request);
            }
            for (Friend friend : this.getFriends(connection, uuid)) {
                user.addFriend(friend);
            }
            return user;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public UUID getUuid(String username) {
        try (final PreparedStatement statement = this.getConnection().prepareStatement("SELECT id FROM user_data WHERE username = ?;")) {
            statement.setString(1, username);
            try (final ResultSet result = statement.executeQuery()) {
                if (result.next()) {
                    return UUID.fromString(result.getString("id"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public String getUsername(UUID uuid) {
        try (final Connection connection = this.getConnection()) {
            return this.getUsername(connection, uuid);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String getUsername(Connection connection, UUID uuid) {
        try (final PreparedStatement statement = connection.prepareStatement("SELECT username FROM user_data WHERE id = ?;")) {
            statement.setString(1, uuid.toString());
            try (final ResultSet result = statement.executeQuery()) {
                if (result.next()) {
                    return result.getString("username");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Set<FriendRequest> getFriendRequests(Connection connection, UUID uuid) {
        final Set<FriendRequest> friendRequests = new HashSet<>();
        try (final PreparedStatement statement = connection.prepareStatement(SELECT_FRIEND_REQUEST_DATA)) {
            statement.setString(1, uuid.toString());
            try (final ResultSet result = statement.executeQuery()) {
                while (result.next()) {
                    final UUID senderUuid = UUID.fromString(result.getString("senderId"));
                    friendRequests.add(new FriendRequest(this.getUsername(connection, senderUuid), senderUuid, uuid, result.getLong("timeSent")));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return friendRequests;
    }

    private Set<Friend> getFriends(Connection connection, UUID uuid) {
        final Set<Friend> friends = new HashSet<>();
        try (final PreparedStatement statement = connection.prepareStatement(SELECT_FRIEND_DATA)) {
            statement.setString(1, uuid.toString());
            try (final ResultSet result = statement.executeQuery()) {
                while (result.next()) {
                    final UUID friendUuid = UUID.fromString(result.getString("friendId"));
                    friends.add(new Friend(friendUuid, this.getUsername(connection, friendUuid), UUID.fromString(result.getString("requesterId")), result.getLong("timeAccepted")));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return friends;
    }
}
