package ru.shemplo.fitness.turnstile.db;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface DAOConverter<T> {

    T convert(ResultSet result) throws SQLException;
}
