package ru.shemplo.fitness.turnstile.db;

import android.util.Log;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class Database {

    private static final Properties properties = new Properties();

    static {
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("MySQL database driver not found", e);
        }

        try {
            properties.load(Database.class.getResourceAsStream("/assets/env.properties"));
        } catch (IOException e) {
            Log.e(Database.class.getSimpleName(), "Database config file not found");
        }
    }

    public static Connection getConnection() throws SQLException {
        String url = properties.getProperty("DATABASE_URL");
        String user = properties.getProperty("DATABASE_USER");
        String password = properties.getProperty("DATABASE_PASS");
        return DriverManager.getConnection("jdbc:" + url, user, password);
    }

    public static Connection getConnection(String url) throws SQLException {
        return DriverManager.getConnection("jdbc:" + url);
    }

    public static <T> T get(String sql, DAOConverter<T> converter) throws SQLException {
        T result = null;

        Connection connection = Database.getConnection();
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(sql);

        while (resultSet.next()) {
            result = converter.convert(resultSet);
        }

        resultSet.close();
        statement.close();
        connection.close();

        return result;
    }

    public static <T> List<T> getAll(String sql, DAOConverter<T> converter) throws SQLException {
        List<T> result = new ArrayList<>();

        Connection connection = Database.getConnection();
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(sql);

        while (resultSet.next()) {
            result.add(converter.convert(resultSet));
        }

        resultSet.close();
        statement.close();
        connection.close();

        return result;
    }

    public static <T> List<T> getAll(String sql, String[] params, DAOConverter<T> converter) throws SQLException {
        List<T> result = new ArrayList<>();

        Connection connection = Database.getConnection();
        PreparedStatement statement = connection.prepareStatement(sql);
        for (int i = 0; i < params.length; i++) {
            statement.setString(i + 1, params[i]);
        }
        ResultSet resultSet = statement.executeQuery();

        while (resultSet.next()) {
            result.add(converter.convert(resultSet));
        }

        resultSet.close();
        statement.close();
        connection.close();

        return result;
    }

    public static int execute(String sql) throws SQLException {
        int result;

        Connection connection = Database.getConnection();
        Statement statement = connection.createStatement();
        result = statement.executeUpdate(sql);
        statement.close();
        connection.close();

        return result;
    }

    public static int execute(String sql, String[] params) throws SQLException {
        int result;

        Connection connection = Database.getConnection();
        PreparedStatement statement = connection.prepareStatement(sql);
        for (int i = 0; i < params.length; i++) {
            statement.setString(i + 1, params[i]);
        }
        result = statement.executeUpdate();
        statement.close();
        connection.close();

        return result;
    }
}
