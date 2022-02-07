package net.revelationmc.friends.bungee.storage.implementation.sql;

import net.revelationmc.friends.bungee.model.friend.Friendship;
import net.revelationmc.friends.bungee.model.friend.FriendRequest;
import net.revelationmc.friends.bungee.model.user.UserSetting;
import net.revelationmc.friends.bungee.storage.implementation.StorageImplementation;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public abstract class AbstractSqlStorage implements StorageImplementation {
    // Create statements:
    private static final String CREATE_SETTINGS_TABLE = "CREATE TABLE IF NOT EXISTS settings (id VARCHAR(36) NOT NULL, setting ENUM('SERVER_CHANGE_MESSAGES', 'QUIT_MESSAGES', 'JOIN_MESSAGES', 'ALLOW_FRIEND_REQUESTS') NOT NULL, value BOOLEAN, PRIMARY KEY (id, setting));";
    private static final String CREATE_REQUESTS_TABLE = "CREATE TABLE IF NOT EXISTS requests (holderId VARCHAR(36) PRIMARY KEY NOT NULL, senderId VARCHAR(36) NOT NULL, dateCreated LONG);";
    private static final String CREATE_FRIENDSHIPS_TABLE = "CREATE TABLE IF NOT EXISTS friendships (holderId VARCHAR(36) PRIMARY KEY NOT NULL, friendId VARCHAR(36) NOT NULL, requester BOOLEAN, dateCreated LONG);";

    // Insert statements:
    private static final String INSERT_DEFAULT_SETTING = "INSERT INTO settings (id, setting, value) VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE value = ?;";
    private static final String INSERT_FRIEND_REQUEST = "INSERT INTO requests (holderId, senderId, dateCreated) VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE holderId = ?;";
    private static final String INSERT_FRIENDSHIP = "INSERT INTO friendships (holderId, friendId, requester, dateCreated) VALUES (?, ?, ?, ?) ON DUPLICATE KEY UPDATE holderId = ?;";

    // Update statements:
    private static final String UPDATE_SETTING = "UPDATE settings SET value = ? WHERE id = ? AND setting = ?;";

    // Select statements:
    private static final String SELECT_ALL_SETTINGS = "SELECT * FROM settings WHERE id = ?;";
    private static final String SELECT_ALL_FRIEND_REQUESTS = "SELECT * FROM requests WHERE holderId = ?;";
    private static final String SELECT_ALL_FRIENDSHIPS = "SELECT * FROM friendships WHERE holderId = ?";

    // Delete statements:
    private static final String DELETE_ALL_FRIEND_REQUESTS = "DELETE FROM requests WHERE holderId = ?;";
    private static final String DELETE_FRIEND_REQUEST = "DELETE FROM requests WHERE holderId = ? AND senderId = ?";
    private static final String DELETE_ALL_FRIENDSHIPS = "DELETE FROM friendships WHERE holderId = ?;";
    private static final String DELETE_FRIENDSHIP = "DELETE FROM friendships WHERE holderId = ? AND friendId = ?";

    @Override
    public void init() {
        // Create tables.
        try (final Connection connection = this.getConnection()) {
            try (final PreparedStatement statement = connection.prepareStatement(CREATE_REQUESTS_TABLE)) {
                statement.executeUpdate();
            }
            try (final PreparedStatement statement = connection.prepareStatement(CREATE_FRIENDSHIPS_TABLE)) {
                statement.executeUpdate();
            }
            try (final PreparedStatement statement = connection.prepareStatement(CREATE_SETTINGS_TABLE)) {
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void createDefaultsIfNotExists(UUID id) {
        try (final Connection connection = this.getConnection()) {
            try (final PreparedStatement statement = connection.prepareStatement(INSERT_DEFAULT_SETTING)) {
                for (UserSetting setting : UserSetting.values()) {
                    statement.setString(1, id.toString());
                    statement.setString(2, setting.name());
                    statement.setBoolean(3, setting.getDefault());
                    statement.setBoolean(4, setting.getDefault());
                    statement.addBatch();
                }
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public FriendRequest createFriendRequest(UUID holderId, UUID senderId, Date dateCreated) {
        try (final Connection connection = this.getConnection()) {
            try (final PreparedStatement statement = connection.prepareStatement(INSERT_FRIEND_REQUEST)) {
                statement.setString(1, holderId.toString());
                statement.setString(2, senderId.toString());
                statement.setLong(3, dateCreated.getTime());
                statement.setString(4, holderId.toString());
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new FriendRequest(senderId, dateCreated);
    }

    @Override
    public void deleteFriendRequest(UUID holderId, UUID senderId) {
        try (final Connection connection = this.getConnection()) {
            try (final PreparedStatement statement = connection.prepareStatement(DELETE_FRIEND_REQUEST)) {
                statement.setString(1, holderId.toString());
                statement.setString(2, senderId.toString());
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteAllFriendRequests(UUID holderId) {
        try (final Connection connection = this.getConnection()) {
            try (final PreparedStatement statement = connection.prepareStatement(DELETE_ALL_FRIEND_REQUESTS)) {
                statement.setString(1, holderId.toString());
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Friendship createFriendship(UUID holderId, UUID friendId, boolean sender, Date dateCreated) {
        try (final Connection connection = this.getConnection()) {
            try (final PreparedStatement statement = connection.prepareStatement(INSERT_FRIENDSHIP)) {
                statement.setString(1, holderId.toString());
                statement.setString(2, friendId.toString());
                statement.setBoolean(3, sender);
                statement.setLong(4, dateCreated.getTime());
                statement.setString(5, holderId.toString());
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new Friendship(friendId, dateCreated, sender);
    }

    @Override
    public void deleteFriendship(UUID holderId, UUID friendId) {
        try (final Connection connection = this.getConnection()) {
            try (final PreparedStatement statement = connection.prepareStatement(DELETE_FRIENDSHIP)) {
                statement.setString(1, holderId.toString());
                statement.setString(2, friendId.toString());
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteAllFriendships(UUID holderId) {
        try (final Connection connection = this.getConnection()) {
            try (final PreparedStatement statement = connection.prepareStatement(DELETE_ALL_FRIENDSHIPS)) {
                statement.setString(1, holderId.toString());
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateSetting(UUID id, UserSetting setting, boolean value) {
        try (final Connection connection = this.getConnection()) {
            try (final PreparedStatement statement = connection.prepareStatement(UPDATE_SETTING)) {
                statement.setBoolean(1, value);
                statement.setString(2, id.toString());
                statement.setString(3, setting.name());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public FriendRequest getFriendRequest(UUID holderId, UUID senderId) {
        return null;
    }

    @Override
    public Set<FriendRequest> getAllFriendRequests(UUID holderId) {
        final Set<FriendRequest> requests = new HashSet<>();
        try (final Connection connection = this.getConnection()) {
            try (final PreparedStatement statement = connection.prepareStatement(SELECT_ALL_FRIEND_REQUESTS)) {
                statement.setString(1, holderId.toString());
                try (final ResultSet result = statement.executeQuery()) {
                    while (result.next()) {
                        requests.add(new FriendRequest(UUID.fromString(result.getString("senderId")), new Date(result.getLong("dateCreated"))));
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return requests;
    }

    @Override
    public Set<Friendship> getAllFriendships(UUID holderId) {
        final Set<Friendship> friendships = new HashSet<>();
        try (final Connection connection = this.getConnection()) {
            try (final PreparedStatement statement = connection.prepareStatement(SELECT_ALL_FRIENDSHIPS)) {
                statement.setString(1, holderId.toString());
                try (final ResultSet result = statement.executeQuery()) {
                    while (result.next()) {
                        friendships.add(new Friendship(
                                UUID.fromString(result.getString("friendId")),
                                new Date(result.getLong("dateCreated")),
                                result.getBoolean("sender")
                        ));
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return friendships;
    }

    @Override
    public boolean getSetting(UUID id, UserSetting setting) {
        return false;
    }

    @Override
    public Map<UserSetting, Boolean> getAllSettings(UUID id) {
        final Map<UserSetting, Boolean> settingsMap = new HashMap<>();
        try (final Connection connection = this.getConnection()) {
            try (final PreparedStatement statement = connection.prepareStatement(SELECT_ALL_SETTINGS)) {
                statement.setString(1, id.toString());
                try (final ResultSet result = statement.executeQuery()) {
                    while (result.next()) {
                        settingsMap.putIfAbsent(UserSetting.valueOf(result.getString("setting")), result.getBoolean("value"));
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    protected abstract Connection getConnection();
}
