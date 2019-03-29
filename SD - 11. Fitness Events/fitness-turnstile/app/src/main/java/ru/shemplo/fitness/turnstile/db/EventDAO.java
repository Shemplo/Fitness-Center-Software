package ru.shemplo.fitness.turnstile.db;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

public class EventDAO implements DAO<Event> {

    private static final DAOConverter<Event> converter = new DAOConverter<Event>() {
        @Override
        public Event convert(ResultSet result) throws SQLException {
            String objectClass = result.getString("object_class");
            int objectId = result.getInt("object_id");
            String propertyName = result.getString("property_name");
            String propertyAction = result.getString("property_action");
            String propertyValue = result.getString("property_value");
            Date date = result.getDate("date");
            return new Event(objectClass, objectId, propertyName, propertyAction, propertyValue, date);
        }
    };

    @Override
    public List<Event> getAll() throws SQLException {
        return Database.getAll("SELECT * FROM `events`", converter);
    }

    @Override
    public void add(Event event) throws SQLException {
        String[] params = {
                event.getObjectClass(),
                String.valueOf(event.getObjectId()),
                event.getPropertyName(),
                event.getPropertyAction(),
                event.getPropertyValue()
        };
        Database.execute("INSERT INTO `events` (\n" +
                "    `object_class`, `object_id`, `property_name`, `property_action`, `property_value`\n" +
                ") VALUES (?, ?, ?, ?, ?)", params);
    }

    public List<Event> getAll(String key) throws SQLException {
        String[] params = {key};
        return Database.getAll("SELECT * FROM `events` es, (\n" +
                "    SELECT `object_id` FROM `events`\n" +
                "    WHERE `object_class` = 'seasonticket' AND `property_name` = 'secret'\n" +
                "        AND `property_action` = 'set' AND `property_value` = ? \n" +
                "    ORDER BY `date` DESC\n" +
                "    LIMIT 1\n" +
                ") id\n" +
                "WHERE es.`object_id` = id.`object_id` \n" +
                "ORDER BY `date` ASC", params, converter);
    }

    public int getVisits(String key) throws SQLException {
        List<Event> events = getAll(key);
        int visits = 0;
        for (Event event : events) {
            if (!event.getPropertyName().equals("visits")) continue;
            switch (event.getPropertyAction()) {
                case "add":
                    visits += Integer.valueOf(event.getPropertyValue());
                    break;
                case "subtract":
                    visits -= Integer.valueOf(event.getPropertyValue());
                    break;
                case "set":
                    visits = Integer.valueOf(event.getPropertyValue());
                    break;
            }
        }
        return visits;
    }

    public void addVisit(String key) throws SQLException {
        String[] params = {key};
        Database.execute("INSERT INTO `events` (\n" +
                "    `object_class`, `object_id`, `property_name`, `property_action`, `property_value`\n" +
                ") SELECT 'seasonticket', `object_id`, 'visits', 'subtract', '1' FROM `events`\n" +
                "    WHERE `object_class` = 'seasonticket' AND `property_name` = 'secret'\n" +
                "    AND `property_action` = 'set' AND `property_value` = ?\n" +
                "ORDER BY `date` DESC\n" +
                "LIMIT 1", params);
    }
}