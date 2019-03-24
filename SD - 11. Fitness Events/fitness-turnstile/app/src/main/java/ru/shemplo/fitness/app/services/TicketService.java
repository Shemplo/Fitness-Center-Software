package ru.shemplo.fitness.app.services;

import android.os.AsyncTask;
import android.support.v4.util.Consumer;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.function.BiConsumer;

import ru.shemplo.fitness.app.db.DBConnector;
import ru.shemplo.fitness.app.services.responses.Response;
import ru.shemplo.fitness.app.services.responses.ResponseTicketVisits;

public class TicketService {

    private final DBConnector connector = new DBConnector ();

    private static final String GET_TICKET_VISITS_QUERY = "" +
            "SELECT es.`object_id`, es.`property_name`, es.`property_action`, " +
            "       es.`property_value`, es.`date`" +
            "FROM `events` es, (" +
            "    SELECT `object_id`" +
            "    FROM `events`" +
            "    WHERE `object_class` = 'ticket' AND `property_name` = 'secret'" +
            "        AND `property_action` = 'set' AND `property_value` = '%s'" +
            "    ORDER BY `date` DESC" +
            "    LIMIT 1" +
            ") id" +
            "WHERE es.`object_id` = id.`object_id`" +
            "ORDER BY `date` ASC";

    public void getVisitOfTicketBySecret (String secret,
            Consumer <ResponseTicketVisits> callback) {
        AsyncTask.execute (new Runnable () {

            @Override
            public void run () {
                Connection connection;
                try {
                    connection = connector.openConnection ();
                } catch (IOException ioe) {
                    callback.accept (new ResponseTicketVisits (null, ioe));
                    return;
                }

                String query = String.format (GET_TICKET_VISITS_QUERY, secret);
                try {
                    ResultSet rows = connection.prepareStatement (query)
                                   . executeQuery ();

                    int visits = 0;
                    while (rows.next ()) {
                        if (!rows.getString ("object_class").equals ("ticket")) {
                            continue;
                        }
                        if (!rows.getString ("property_name").equals ("visits")) {
                            continue;
                        }

                        switch (rows.getString ("property_action")) {
                            case "add":
                                visits += rows.getInt ("property_value");
                                break;
                            case "subtract":
                                visits -= rows.getInt ("property_value");
                                break;
                            case "set":
                                visits = rows.getInt ("property_value");
                                break;
                        }
                    }

                    callback.accept (new ResponseTicketVisits (visits, null));
                } catch (SQLException sqle) {
                    IOException exception = new IOException (sqle);
                    callback.accept (new ResponseTicketVisits (null, exception));
                    return;
                }
            }

        });
    }

}
