package ru.shemplo.fitness.services;

import java.io.IOException;
import java.sql.SQLException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;
import ru.shemplo.fitness.AppConfiguration;
import ru.shemplo.fitness.db.DBManager;
import ru.shemplo.fitness.db.DBObjectUnwrapper;
import ru.shemplo.fitness.entities.FitnessClient;
import ru.shemplo.fitness.entities.FitnessEvent;
import ru.shemplo.fitness.entities.SeasonTicket;
import ru.shemplo.snowball.annot.Snowflake;

@Snowflake
@AllArgsConstructor
public class SeasonTicketService {
    
    private DBObjectUnwrapper objectUnwrapper;
    private AppConfiguration configuration;
    private DBManager database;
    
    public SeasonTicket createTicket (FitnessClient client, String secret, int visits) throws IOException {
        Integer nextID;
        try   { nextID = database.runFunction ("SELECT GET_NEXT_ID_FOR ('ticket');"); } 
        catch (SQLException sqle) { throw new IOException (sqle); }
        
        try { 
            String tCreate = configuration.<String> get ("create-new-ticket")      .get (),
                   tSecret = configuration.<String> get ("set-secret-to-ticket")   .get (),
                   tClient = configuration.<String> get ("set-client-to-ticket")   .get (),
                   tVisits = configuration.<String> get ("change-visits-of-ticket").get ();
            database.update ( // Creating batch of insert requests
                String.format (tCreate, nextID), 
                String.format (tSecret, nextID, secret),
                String.format (tClient, nextID, client.getId ()),
                String.format (tVisits, nextID, "add", visits)
            ); 
        } catch (SQLException sqle) { throw new IOException (sqle); }
        
        LocalDateTime dateTime = Instant.now ().atOffset (ZoneOffset.UTC)
                               . toLocalDateTime ();
        SeasonTicket ticket = new SeasonTicket ();
        
        ticket.setLastTimeUpdated (dateTime);
        ticket.setSecret (secret);
        ticket.setVisits (visits);
        ticket.setId (nextID);
        
        return ticket;
    }
    
    private final LocalDateTime START_DATE = LocalDateTime.parse ("2019-03-20T00:00:00");
    
    public List <SeasonTicket> getAllTickets () throws IOException {
        return getAllTicketsAfter (START_DATE); // No events earlier can be
    }
    
    /**
     * Returns all {@link SeasonTicket}s that was created after specified
     * {@link LocalDateTime time} in the past
     * 
     * @param dateTime time border for all events in past
     * 
     * @return list of {@link SeasonTicket tickets}
     * 
     * @throws IOException if failed to retrieve data from DB
     * 
     */
    public List <SeasonTicket> getAllTicketsAfter (LocalDateTime dateTime) throws IOException {
        List <FitnessEvent> events;
        
        final String template = configuration.<String> get ("retrieve-all-by-type-after").get ();
        final String date = dateTime.toString ().replace ('T', ' ');
        
        final String request = String.format (template, "ticket", date);
        try   { events = database.retrieve (request, FitnessEvent.class); } 
        catch (SQLException sqle) { throw new IOException (sqle); }
        
        return eventsToTickets (events);
    }
    
    /**
     * Returns all {@link SeasonTicket}s that belong to specified {@link FitnessClient}
     * 
     * @param client representation of real world client entity
     * 
     * @return list of {@link SeasonTicket tickets} referenced to the on {@link FitnessClient client}
     * 
     * @throws IOException if failed to retrieve data from DB
     * 
     * @see FitnessClientService#getClientByID(int)
     * 
     */
    public List <SeasonTicket> getTicketsByClient (FitnessClient client) throws IOException {
        String template = configuration.<String> get ("retrieve-p-client-tickets").get ();
        String request = String.format (template, client.getId ());
        
        List <FitnessEvent> events;
        try   { events = database.retrieve (request, FitnessEvent.class); } 
        catch (SQLException sqle) { throw new IOException (sqle); }
        
        final Integer clientID = client.getId ();
        return eventsToTickets (events).stream ().filter (t -> t.getClient ().equals (clientID))
             . collect (Collectors.toList ());
    }
    
    /**
     * Returns the last {@link SeasonTicket} for that was assigned this secret keyword
     * 
     * @param secret some string identifier for the {@link SeasonTicket ticket} object
     * 
     * @return one {@link SeasonTicket ticket} that has such <i>secret</i>
     * 
     * @throws IOException if failed to retrieve data from DB
     * 
     */
    public SeasonTicket getTicketBySecret (String secret) throws IOException {
        String template = configuration.<String> get ("retrieve-ticket-by-secret").get ();
        String request = String.format (template, secret);
        
        List <FitnessEvent> events;
        try   { events = database.retrieve (request, FitnessEvent.class); } 
        catch (SQLException sqle) { throw new IOException (sqle); }
        
        return objectUnwrapper.unwrapTo (events, new SeasonTicket ());
    }
    
    /**
     * Returns passed {@link SeasonTicket} object but adds all changes that
     * happened since the last update (if wasn't updated then since retrieve time).
     * Time border for new events - time when the last field in object was changed
     * 
     * @param ticket object that should be updated with last events
     * 
     * @return the given {@link SeasonTicket ticket} object with updates
     * 
     * @throws IOException if failed to retrieve data from DB
     * 
     */
    public SeasonTicket updateTicket (SeasonTicket ticket) throws IOException {
        String template = configuration.<String> get ("retrieve-by-id-type-after").get ();
        String request = String.format (template, "ticket", ticket.getId (), 
                                                 ticket.getLastTimeUsed ());
        
        List <FitnessEvent> events;
        try   { events = database.retrieve (request, FitnessEvent.class); } 
        catch (SQLException sqle) { throw new IOException (sqle); }
        
        return objectUnwrapper.unwrapTo (events, ticket);
    }
    
    private List <SeasonTicket> eventsToTickets (List <FitnessEvent> events) {
        final Map <Integer, List <FitnessEvent>> eventsByTickets = events.stream ()
                . collect (Collectors.groupingBy (FitnessEvent::getObjectId));
        final List <SeasonTicket> tickets = new ArrayList <> ();
            
        eventsByTickets.forEach ((id, sequence) -> {
            try {
                SeasonTicket ticket = objectUnwrapper.unwrap (sequence, 
                                                   SeasonTicket.class);
                ticket.setId (id);
                
                if (ticket.isCompleted ()) { tickets.add (ticket); }
            } catch (IOException e) {}
        });
        
        return tickets;
    }
    
}
