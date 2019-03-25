package ru.shemplo.fitness.administration.gfx;

import java.io.IOException;
import java.net.URL;

import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;

public class AdminController implements Initializable {
    
    //@FXML private BorderPane clientDetails;
    @FXML private ScrollPane clientDetails;
    
    @Override
    public void initialize (URL location, ResourceBundle resources) {
        final URL url = getClass ().getResource ("/fxml/client.fxml");
        try {
            clientDetails.setContent(FXMLLoader.load (url));
            //clientDetails.applyCss();
            //clientDetails.setRight (FXMLLoader.load (url));
        } catch (IOException e) { e.printStackTrace(); }
    }
    
}
