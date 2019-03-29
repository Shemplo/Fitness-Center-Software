package ru.shemplo.fitness.turnstile.db;

import java.sql.SQLException;
import java.util.List;

public interface DAO<T> {

    List<T> getAll() throws SQLException;

    void add(T t) throws SQLException;
}
