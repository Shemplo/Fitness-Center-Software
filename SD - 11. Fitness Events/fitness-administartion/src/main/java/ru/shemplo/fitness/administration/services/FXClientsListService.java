package ru.shemplo.fitness.administration.services;

import static javafx.collections.FXCollections.*;
import static ru.shemplo.fitness.services.AbsService.*;

import java.time.LocalDateTime;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.scene.control.ListView;

import ru.shemplo.fitness.entities.FitnessClient;
import ru.shemplo.fitness.services.FitnessClientService;
import ru.shemplo.snowball.annot.Snowflake;
import ru.shemplo.snowball.annot.processor.Snowball;
import ru.shemplo.snowball.annot.processor.SnowflakeInitializer;

public class FXClientsListService extends Service <List <FitnessClient>> {
    
    private static final Comparator <FitnessClient> LEXICOGRAPHIC_ORDER 
          = (a, b) -> a.getLastName ().compareToIgnoreCase (b.getLastName ());

    private FitnessClientService clientService;
    
    @Snowflake (manual = true) private LocalDateTime lastUpdate;
    
    public FXClientsListService (final ListView <FitnessClient> listView) {
        SnowflakeInitializer.initFields (Snowball.getContext (), this);
        
        setOnSucceeded (wse -> {
            final List <FitnessClient> clients = getValue ();
            clients.sort (LEXICOGRAPHIC_ORDER);
            
            listView.setItems (observableArrayList (clients));
        });
    }
    
    @Override
    protected Task <List <FitnessClient>> createTask () {
        return new Task <List<FitnessClient>> () {
            
            @Override 
            protected List <FitnessClient> call () throws Exception {
                LocalDateTime from = Optional.ofNullable (lastUpdate)
                                   . orElse (getStartDate ());
                return clientService.getAllAfter (from);
            }
            
        };
    }
    
}
