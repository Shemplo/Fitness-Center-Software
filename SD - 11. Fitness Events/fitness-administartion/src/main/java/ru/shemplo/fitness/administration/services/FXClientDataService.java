package ru.shemplo.fitness.administration.services;

import javafx.concurrent.Service;
import javafx.concurrent.Task;

import ru.shemplo.fitness.administration.gfx.ClientController;
import ru.shemplo.fitness.entities.FitnessClient;
import ru.shemplo.fitness.services.FitnessClientService;
import ru.shemplo.snowball.annot.processor.Snowball;
import ru.shemplo.snowball.annot.processor.SnowflakeInitializer;

public class FXClientDataService extends Service <FitnessClient> {

    private FitnessClientService clientService;
    
    private final FitnessClient client;
    
    public FXClientDataService (ClientController controller, FitnessClient client) {
        SnowflakeInitializer.initFields (Snowball.getContext (), this);
        this.client = client;
        
        setOnSucceeded (wse -> {
            controller.getAdminController ().getClientsList ().refresh ();
            controller.initializeFields ();
        });
    }
    
    @Override
    protected Task <FitnessClient> createTask () {
        return new Task <FitnessClient> () {
            
            @Override protected FitnessClient call () throws Exception {
                return clientService.updateClient (client);
            }
            
        };
    }
    
}
