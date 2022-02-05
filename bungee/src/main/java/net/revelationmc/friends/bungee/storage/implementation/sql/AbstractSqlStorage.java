package net.revelationmc.friends.bungee.storage.implementation.sql;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import net.revelationmc.friends.bungee.storage.implementation.StorageImplementation;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.TimeUnit;

public abstract class AbstractSqlStorage implements StorageImplementation {
    private static final String DRIVER_CLASS = "com.mysql.jdbc.Driver";
    private static final String JDBC_URL = "jdbc:mysql://%s:%s/%s";

    private final HikariConfig config = new HikariConfig();

    private HikariDataSource dataSource;

    public AbstractSqlStorage(String host, int port, String database, String username, String password) {
        this.config.setDriverClassName(DRIVER_CLASS);
        this.config.setJdbcUrl(String.format(JDBC_URL, host, port, database));
        this.config.setMaxLifetime(TimeUnit.MINUTES.toMillis(30L));
        this.config.setPoolName("friends-hikari");
        this.config.setUsername(username);
        this.config.setPassword(password);
    }

    protected abstract void setupTables();

    @Override
    public void init() {
        this.dataSource = new HikariDataSource(this.config);
        this.setupTables();
    }

    @Override
    public void shutdown() {
        this.dataSource.close();
    }

    protected Connection getConnection() {
        try {
            final Connection connection = this.dataSource.getConnection();
            if (connection != null) {
                return connection;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
