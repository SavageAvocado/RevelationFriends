package net.revelationmc.friends.bungee.storage.implementation.sql.mysql;

import net.revelationmc.friends.bungee.storage.implementation.sql.AbstractSqlStorage;
import net.revelationmc.lib.storage.factory.SqlConnectionFactory;

import java.sql.Connection;
import java.sql.SQLException;

public class MySqlStorage extends AbstractSqlStorage {
    private final SqlConnectionFactory factory;

    public MySqlStorage(String host, int port, String database, String username, String password) {
        this.factory = new SqlConnectionFactory.Builder()
                .setPoolName("revelation-friends-hikari")
                .setHost(host)
                .setPort(port)
                .setDatabase(database)
                .setUsername(username)
                .setPassword(password)
                .build();
    }

    @Override
    public void shutdown() {
        this.factory.shutdown();
    }

    @Override
    protected Connection getConnection() {
        try {
            return this.factory.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
