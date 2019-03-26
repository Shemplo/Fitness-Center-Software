package ru.shemplo.fitness.administration.gfx;

import java.io.IOException;
import java.net.URL;

import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.concurrent.Service;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.util.Duration;

import lombok.Getter;
import ru.shemplo.fitness.administration.services.FXClientsListService;
import ru.shemplo.fitness.entities.FitnessClient;

public class AdminController implements Initializable {
    
    @FXML private ScrollPane clientDetails;
    
    @Getter @FXML 
    private ListView <FitnessClient> clientsList;
    
    private Service <List <FitnessClient>> clientsListService;
    
    private final Timeline clientListServiceCaller = new Timeline (
        new KeyFrame (Duration.millis (0), __ -> {
            if (!clientsListService.isRunning ()) {
                clientsListService.restart ();
            }
        }),
        new KeyFrame (Duration.seconds (30))
    );
    
    @Override
    public final void initialize (URL location, ResourceBundle resources) {
        clientsList.setCellFactory (__ -> new ClientCell (this));
        clientsList.editableProperty ().set (false);
        
        clientsListService = new FXClientsListService (clientsList);
        clientListServiceCaller.setCycleCount (Timeline.INDEFINITE);
        clientListServiceCaller.playFromStart ();
    }
    
    private volatile ClientController currentClient;
    
    public synchronized void openClientDetails (FitnessClient client) {
        if (currentClient != null) { // stopping previous opened controller
            try   { currentClient.close (); } 
            catch (Exception e) {}
        }
        
        FitnessClient insert = Optional.ofNullable (client).orElse (new FitnessClient ());
        final ClientController controller = new ClientController (this, insert);
        final URL url = getClass ().getResource ("/fxml/client.fxml");
        final FXMLLoader loader = new FXMLLoader (url);
        loader.setController (controller);
        currentClient = controller;
        
        try   { clientDetails.setContent(loader.load ()); } 
        catch (IOException e) { e.printStackTrace(); }
    }
    
}
