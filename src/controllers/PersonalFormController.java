/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import alerts.ErrorAlert;
import database.DBComboBox;
import exceptions.NoWindowToClose;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import utils.App;
import utils.ItemIdValue;
import utils.Utils;

/**
 * FXML Controller class
 *
 * @author Dominik
 */
public class PersonalFormController implements Initializable, IFormController {

    @FXML
    private TextField firstNameTF;
    @FXML
    private TextField lastNameTF;
    @FXML
    private ComboBox<ItemIdValue> positionCombo;
    @FXML
    private TextField telephoneTF;
    @FXML
    private TextField emailTF;
    @FXML
    private ComboBox<ItemIdValue> addressCombo;
    
    private DBComboBox positions;
    private DBComboBox addresses;
    
    public void initialize(URL url, ResourceBundle rb) {
        positions = new DBComboBox(positionCombo);
        addresses = new DBComboBox(addressCombo);
        try {
            positions.init("select id, nazev from pozice");
            addresses.init("select id, ulice||' '||cislo_popisne||', "
                    + "'||mesto||' '||psc||', '||zeme ulice from adresy");
        }catch (SQLException ex) {
            new ErrorAlert("Chyba na strane serveru.").showAndWait();
            ex.printStackTrace();
        }        
    }    
    
    @FXML
    public void add(ActionEvent ev) {
        
    }
    
    @FXML
    public void cancel(ActionEvent ev) throws NoWindowToClose {
        App.closeActive();
    }
    
    @FXML
    public void addAddress(ActionEvent ev) {
        App.createForm("Address").showAndWait();        
    }

    @Override
    public void setData(Object[] data) {        
        firstNameTF.setText(data[1].toString());
        lastNameTF.setText(data[2].toString());                
        telephoneTF.setText(data[3].toString());
        emailTF.setText(data[4].toString());
        positionCombo.getSelectionModel().select(new ItemIdValue(data[5].toString()));
        addressCombo.getSelectionModel().select(new ItemIdValue(data[6].toString()));
    }
    
}
