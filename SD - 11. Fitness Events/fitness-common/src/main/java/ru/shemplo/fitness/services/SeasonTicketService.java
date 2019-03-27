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

import java.util.concurrent.atomic.AtomicBoolean;

import ru.shemplo.fitness.entities.FitnessClient;
import ru.shemplo.fitness.entities.FitnessEvent;
import ru.shemplo.fitness.entities.SeasonTicket;
import ru.shemplo.snowball.annot.Snowflake;
import ru.shemplo.snowball.stuctures.Pair;

@Snowflake
public class SeasonTicketService extends AbsService <SeasonTicket> {
    
    public SeasonTicketService () {
        super (SeasonTicket.class);
    }

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
        
        SeasonTicket ticket = new SeasonTicket ();
        LocalDateTime dateTime = Instant.now ().atOffset (ZoneOffset.UTC)
                               . toLocalDateTime ();
        
        ticket.setLastTimeUpdated (dateTime);
        ticket.setSecret (secret);
        ticket.setVisits (visits);
        ticket.setId (nextID);
        
        return ticket;
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
        return eventsToInstances (events).stream ()
             . filter (t -> t.getClient ().equals (clientID))
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
        
        SeasonTicket ticket = objectUnwrapper.unwrapTo (events, new SeasonTicket ());
        if (!ticket.isCompleted ()) { throw new IOException ("Ticket not found"); }
        
        return ticket;
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
    
    public Pair <List <SeasonTicket>, Boolean> updateTickets (List <SeasonTicket> tickets,
            LocalDateTime lastUpdate, int current) throws IOException {
        Map <Integer, List <FitnessEvent>> events = getAllEventsAfter (lastUpdate).stream ()
          . collect (Collectors.groupingBy (FitnessEvent::getObjectId));
        Map <Integer, SeasonTicket> ticketss = tickets.stream ()
          . collect (Collectors.toMap (SeasonTicket::getId, __ -> __));
        AtomicBoolean currentUpdated = new AtomicBoolean (false);
        final List <SeasonTicket> toAdd = new ArrayList <> ();
        
        events.forEach ((id, eventss) -> {
            if (ticketss.containsKey (id)) {
                final SeasonTicket ticket = ticketss.get (id);
                objectUnwrapper.unwrapTo (eventss, ticket);
                if (current == ticket.getClient ()) { 
                    currentUpdated.set (true); 
                }
            } else {
                final SeasonTicket ticket = new SeasonTicket ();
                objectUnwrapper.unwrapTo (eventss, ticket);
                toAdd.add (ticket);
            }
        });
        
        List <SeasonTicket> toAddFiltered = toAdd.stream ()
           . filter (SeasonTicket::isCompleted)
           . collect (Collectors.toList ());
             
        return Pair.mp (toAddFiltered, currentUpdated.get ());
    }
    
}
