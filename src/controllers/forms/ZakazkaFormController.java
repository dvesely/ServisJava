/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers.forms;

import alerts.ErrorAlert;
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
import database.DBComboBox;
import java.util.Map;
import utils.ItemIdValue;
import utils.Utils;

/**
 * FXML Controller class
 *
 * @author Dominik
 */
public class ZakazkaFormController implements Initializable, IFormController {

    @FXML
    private ComboBox<ItemIdValue> clientCombo;
    @FXML
    private TextField approxCostTF;
    @FXML
    private TextField startDateTF;    
    @FXML
    private TextField endDateTF;    
    
    private DBComboBox clients;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        clients = new DBComboBox(clientCombo);
        try {            
            clients.init("select id, jmeno||' '||prijmeni||'('||telefon||')' from klienti");            
        }catch (SQLException ex) {
            new ErrorAlert("Chyba na pri plneni seznamu klientu.\n"+ex.getMessage()).showAndWait();            
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
    public void newClientAction(ActionEvent ev) throws SQLException {
        App.createForm("Client").showAndWait();        
        clients.refresh();        
        clients.select(App.removeComboItem());
    }

    @Override
    public void setData(Map<String, String> data) {        
        //firstNameTF.setText(data[1].toString());                
        //addressCombo.getSelectionModel().select(new ItemIdValue(data[6].toString()));
    }
    
}
