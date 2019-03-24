package ru.shemplo.fitness.app.db;

import static ru.shemplo.fitness.app.db.DBConfiguration.*;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnector {

    public Connection openConnection () throws IOException {
        try {
            return DriverManager.getConnection (URL, LOGIN, PASSWORD);
        } catch (SQLException sqle) { throw new IOException (sqle); }
    }

}
