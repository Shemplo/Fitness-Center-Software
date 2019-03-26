package ru.shemplo.fitness.administration.gfx;

import java.io.IOException;
import java.net.URL;

import java.util.List;
import java.util.ResourceBundle;

import javafx.concurrent.Service;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;

import ru.shemplo.fitness.administration.services.FXClientsListService;
import ru.shemplo.fitness.entities.FitnessClient;

public class AdminController implements Initializable {
    
    @FXML private ScrollPane clientDetails;
    
    @FXML private ListView <FitnessClient> clientsList;
    
    private Service <List <FitnessClient>> clientsListService;
    
    @Override
    public final void initialize (URL location, ResourceBundle resources) {
        clientsListService = new FXClientsListService (clientsList);
        //clientsListService.restart ();
        
        final URL url = getClass ().getResource ("/fxml/client.fxml");
        try   { clientDetails.setContent(FXMLLoader.load (url)); } 
        catch (IOException e) { e.printStackTrace(); }
        
    }
    
}
