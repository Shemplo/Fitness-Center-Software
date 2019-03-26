package ru.shemplo.fitness.administration.services;

import static javafx.collections.FXCollections.*;

import java.time.LocalDateTime;

import java.util.Comparator;
import java.util.List;

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
          = (a, b) -> a.getName ().compareToIgnoreCase (b.getName ());

    private final ListView <FitnessClient> listView;
    private FitnessClientService clientService;
    
    @Snowflake (manual = true) private LocalDateTime lastUpdate;
    
    public FXClientsListService (final ListView <FitnessClient> listView) {
        SnowflakeInitializer.initFields (Snowball.getContext (), this);
        this.listView = listView;
        
        setOnSucceeded (wse -> {
            final List <FitnessClient> clients = getValue ();
            clients.sort (LEXICOGRAPHIC_ORDER);
            
            listView.setItems (observableArrayList (clients));
        });
    }
    
    @Override
    protected Task <List <FitnessClient>> createTask () {
        return null;
    }
    
}
