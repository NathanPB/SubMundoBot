package piva.sbb.bot;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;

public class Database {
    private static HikariDataSource databasePool;

    public static void start(String host, int port, String database, String user, String pass) {
        HikariConfig config = new HikariConfig();

        config.setJdbcUrl("jdbc:mysql://" + host + ":" + port + "/" + database);
        config.setDriverClassName("com.mysql.cj.jdbc.Driver");
        config.setUsername(user);
        config.setPassword(pass);
        config.setIdleTimeout(60000);
        config.setConnectionTimeout(15000);
        config.setMaximumPoolSize(10);
        config.setLeakDetectionThreshold(60000);

        databasePool = new HikariDataSource(databasePool);
    }

    public static HikariDataSource getDatabasePool() {
        return databasePool;
    }

    public static Connection getConnection() throws SQLException {
        return databasePool.getConnection();
    }
}