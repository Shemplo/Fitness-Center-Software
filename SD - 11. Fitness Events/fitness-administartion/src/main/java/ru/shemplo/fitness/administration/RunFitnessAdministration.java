package ru.shemplo.fitness.administration;

import java.io.IOException;
import java.nio.file.Paths;

import java.util.List;

import javafx.application.Application;

import ru.shemplo.fitness.AppConfiguration;
import ru.shemplo.fitness.administration.gfx.AdminApplication;
import ru.shemplo.fitness.db.DBManager;
import ru.shemplo.fitness.entities.FitnessClient;
import ru.shemplo.fitness.entities.SeasonTicket;
import ru.shemplo.fitness.services.FitnessClientService;
import ru.shemplo.fitness.services.SeasonTicketService;
import ru.shemplo.snowball.annot.Wind;
import ru.shemplo.snowball.annot.processor.Snowball;

@Wind (blow = {AppConfiguration.class, SeasonTicketService.class,
               DBManager.class})
public class RunFitnessAdministration extends Snowball {
    
    public static void main (String ... args) { shape (args); }
    
    private SeasonTicketService seasonTicketService;
    private FitnessClientService clientService;
    private AppConfiguration configuration;
    
    @Override
    protected void onShaped (String ... args) {
        try {
            configuration.readConfigurationFile (Paths.get ("config.yml"));
        } catch (IOException ioe) { ioe.printStackTrace (); }
        
        try {
            FitnessClient client = clientService.getClientByID (0);
            System.out.println (client);
            
            //System.out.println (seasonTicketService.createTicket (client, "secret for 2 ticket", 7));
            List <SeasonTicket> tickets = seasonTicketService.getTicketsByClient (client);
            tickets.stream ().map (t -> {
                try   { return seasonTicketService.updateTicket (t); } 
                catch (IOException e) {}
                
                return t;   
            }).forEach (System.out::println);
            
            System.out.println (seasonTicketService.getTicketBySecret ("some secret value"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        Application.launch (AdminApplication.class, args);
    }
    
}
