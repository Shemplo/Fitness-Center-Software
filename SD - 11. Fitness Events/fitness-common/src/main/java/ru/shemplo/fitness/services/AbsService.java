package ru.shemplo.fitness.services;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDateTime;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import ru.shemplo.fitness.AppConfiguration;
import ru.shemplo.fitness.db.DBManager;
import ru.shemplo.fitness.db.DBObjectUnwrapper;
import ru.shemplo.fitness.entities.Completable;
import ru.shemplo.fitness.entities.FitnessEvent;
import ru.shemplo.fitness.entities.Identifiable;
import ru.shemplo.fitness.entities.Updatable;

@RequiredArgsConstructor
public abstract class AbsService <T extends Identifiable & Completable & Updatable> {
    
    protected DBObjectUnwrapper objectUnwrapper;
    protected AppConfiguration configuration;
    protected DBManager database;
    
    protected final Class <T> TOKEN;
    
    protected static final LocalDateTime START_DATE = LocalDateTime.parse ("2019-03-20T00:00:00");
    
    public List <T> getAll () throws IOException {
        return getAllAfter (START_DATE); // No events earlier can be
    }
    
    public List <T> getAllAfter (LocalDateTime dateTime) throws IOException {
        List <FitnessEvent> events;
        
        final String template = configuration.<String> get ("retrieve-all-by-type-after").get ();
        final String date = dateTime.toString ().replace ('T', ' ');
        //final String date = Utils.DATETIME_FORMAT.format (dateTime);
        
        final String request = String.format (template, "ticket", date);
        try   { events = database.retrieve (request, FitnessEvent.class); } 
        catch (SQLException sqle) { throw new IOException (sqle); }
        
        return eventsToInstances (events);
    }
    
    protected List <T> eventsToInstances (final List <FitnessEvent> events) {
        final Map <Integer, List <FitnessEvent>> eventsByTickets = events.stream ()
                . collect (Collectors.groupingBy (FitnessEvent::getObjectId));
        final List <T> instances = new ArrayList <> ();
            
        eventsByTickets.forEach ((id, sequence) -> {
            try {
                final T instance = objectUnwrapper.unwrap (sequence, TOKEN);
                if (instance.isCompleted ()) { instances.add (instance); }
            } catch (IOException e) {}
        });
        
        return instances;
    }
    
}
