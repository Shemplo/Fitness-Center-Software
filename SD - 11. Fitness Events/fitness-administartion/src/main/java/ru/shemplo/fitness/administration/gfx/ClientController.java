package ru.shemplo.fitness.administration.gfx;

import java.net.URL;

import java.util.Optional;
import java.util.ResourceBundle;

import javafx.concurrent.Service;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseButton;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ru.shemplo.fitness.administration.services.FXClientDataService;
import ru.shemplo.fitness.administration.services.FXClientUpdateService;
import ru.shemplo.fitness.entities.FitnessClient;

@Getter
@RequiredArgsConstructor
public class ClientController implements Initializable, AutoCloseable {

    private final AdminController adminController;
    private final FitnessClient client;
    
    private FitnessClient changedClient = new FitnessClient ();
    
    @FXML private Button saveClientDetails;
    
    @FXML private TextField 
        idF, 
        
        firstNameF, secondNameF, lastNameF,
        organizationF, positionF,
        countryF, stateF, cityF, districtF,
        phoneF, emailF, homePageF,
        remarkF;
    
    private Service <FitnessClient> clientDataService;
    
    @Override
    public void initialize (URL location, ResourceBundle resources) {
        bindFields ();
        
        clientDataService = new FXClientDataService (this, client);
        clientDataService.restart ();
        
        saveClientDetails.setOnMouseClicked (me -> {
            if (!MouseButton.PRIMARY.equals (me.getButton ())) {
                return;
            }
            
            new FXClientUpdateService (this, client, changedClient).restart ();
        });
    }
    
    public void initializeFields () {
        idF.          setText ("" + Optional.ofNullable (client.getId ()).orElse (-1));
        firstNameF.   setText (Optional.ofNullable (client.getFirstName ()).orElse (""));
        secondNameF.  setText (Optional.ofNullable (client.getSecondName ()).orElse (""));
        lastNameF.    setText (Optional.ofNullable (client.getLastName ()).orElse (""));
        organizationF.setText (Optional.ofNullable (client.getOrganization ()).orElse (""));
        positionF.    setText (Optional.ofNullable (client.getPosition ()).orElse (""));
        countryF.     setText (Optional.ofNullable (client.getCountry ()).orElse (""));
        stateF.       setText (Optional.ofNullable (client.getState ()).orElse (""));
        cityF.        setText (Optional.ofNullable (client.getCity ()).orElse (""));
        districtF.    setText (Optional.ofNullable (client.getDistrict ()).orElse (""));
        phoneF.       setText (Optional.ofNullable (client.getPhone ()).orElse (""));
        emailF.       setText (Optional.ofNullable (client.getEmail ()).orElse (""));
        homePageF.    setText (Optional.ofNullable (client.getHomePage ()).orElse (""));
        remarkF.      setText (Optional.ofNullable (client.getRemark ()).orElse (""));
    }
    
    private void bindFields () {
        idF.          textProperty ().addListener ((__, ___, v) -> changedClient.setId (Integer.parseInt (v)));
        firstNameF.   textProperty ().addListener ((__, ___, v) -> changedClient.setFirstName (v));
        secondNameF.  textProperty ().addListener ((__, ___, v) -> changedClient.setSecondName (v));
        lastNameF.    textProperty ().addListener ((__, ___, v) -> changedClient.setLastName (v));
        organizationF.textProperty ().addListener ((__, ___, v) -> changedClient.setOrganization (v));
        positionF.    textProperty ().addListener ((__, ___, v) -> changedClient.setPosition (v));
        countryF.     textProperty ().addListener ((__, ___, v) -> changedClient.setCountry (v));
        stateF.       textProperty ().addListener ((__, ___, v) -> changedClient.setState (v));
        cityF.        textProperty ().addListener ((__, ___, v) -> changedClient.setCity (v));
        districtF.    textProperty ().addListener ((__, ___, v) -> changedClient.setDistrict (v));
        phoneF.       textProperty ().addListener ((__, ___, v) -> changedClient.setPhone (v));
        emailF.       textProperty ().addListener ((__, ___, v) -> changedClient.setEmail (v));
        homePageF.    textProperty ().addListener ((__, ___, v) -> changedClient.setHomePage (v));
        remarkF.      textProperty ().addListener ((__, ___, v) -> changedClient.setRemark (v));
    }

    @Override
    public void close () throws Exception {
        
    }
    
}
