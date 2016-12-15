/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers.forms;

import alerts.ErrorAlert;
import database.DB;
import exceptions.NoWindowToClose;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import utils.App;
import database.DBComboBox;
import database.OracleConnector;
import java.sql.PreparedStatement;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.util.converter.DateStringConverter;
import javafx.util.converter.DateTimeStringConverter;
import utils.ItemIdValue;
import utils.Query;

/**
 * FXML Controller class
 *
 * @author Dominik
 */
public class ZakazkaFormController implements Initializable, IFormController {

    @FXML
    private ComboBox<ItemIdValue> clientCombo;
    @FXML
    private TextField pribliznaCenaTF;
    @FXML
    private DatePicker zacatekDatePicker;    
    @FXML
    private DatePicker konecDatePicker;  
    @FXML
    private Button upravButton;
    
    private DBComboBox clients;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        clients = new DBComboBox(clientCombo);        
        upravButton.setDisable(true);
        zacatekDatePicker.setValue(LocalDate.now());     
        konecDatePicker.setValue(LocalDate.now());
        try {            
            clients.init("select id, jmeno||' '||prijmeni||'('||telefon||')' from klienti");            
        }catch (SQLException ex) {
            new ErrorAlert("Chyba na pri plneni seznamu klientu.\n"+ex.getMessage()).showAndWait();            
        }        
    }    
    
    @FXML
    public void potvrdAction(ActionEvent ev) throws NoWindowToClose {
        App.closeActiveForm(true);
    }
    
    @FXML
    public void stornujAction(ActionEvent ev) throws NoWindowToClose {
        App.closeActiveForm(false);
    }
    
    @FXML
    public void comboAction(ActionEvent ev) {        
        boolean jeVybranKlient = clientCombo.getSelectionModel().getSelectedItem()!=null;
        upravButton.setDisable(!jeVybranKlient);
    }
    
    @FXML
    public void vytvorKlientaAction(ActionEvent ev) throws SQLException {
        App.createForm("Klient").showAndWait();                
        clients.refresh();        
        clients.select(App.removeComboItem());
    }
    
    @FXML
    public void upravKlientaAction(ActionEvent ev) throws SQLException {        
        PreparedStatement ps = OracleConnector.getConnection().prepareCall(Query.KLIENT_FORM);
        ps.setInt(1, clients.getItem().getId());
        App.createForm("Klient", DB.resultSetToMapString(ps.executeQuery()))
                .showAndWait();        
    }

    @Override
    public void setData(Map<String, String> data) {   
        System.out.println(data);
        //System.out.println(df.format(data.get("datum_prijmuti")));        
        upravButton.setDisable(false);
        pribliznaCenaTF.setText(data.get("priblizna_cena"));
        
        //zacatekDatePicker.setValue(DB.StringToLocalDate(data.get("datum_prijmuti")));
        //konecDatePicker.setValue(DB.StringToLocalDate(data.get("datum_priblizne_dokonceni")));
    }
    
}
