/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import alerts.ErrorAlert;
import alerts.InfoAlert;
import database.OracleConnector;
import exceptions.NoWindowToClose;
import exceptions.ValidException;
import java.net.URL;
import java.sql.CallableStatement;
import java.sql.SQLException;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import oracle.jdbc.OracleTypes;
import utils.App;
import utils.JSON;
import utils.Validator;

/**
 * FXML Controller class
 *
 * @author Dominik
 */
public class AddressFormController implements Initializable, IFormController {

    @FXML
    private TextField streetTF;
    @FXML
    private TextField streetNumberTF;
    @FXML
    private TextField cityTF;
    @FXML
    private TextField zipCodeTF;
    @FXML
    private TextField countryTF;
    @FXML
    private Button confirmButton;
    
    private boolean uprava = false;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
    }    
    
    @FXML
    public void confirm(ActionEvent ev) throws SQLException, ValidException, NoWindowToClose {        
        
        Validator valid = new Validator();
        
        CallableStatement cStmt = OracleConnector.getConnection()
                .prepareCall("{call pck_adresy.uprav_adresu(?, ?, ?, ?, ?, ?, ?)}");
        if (uprava) 
            cStmt.setInt("p_id", 5);
        else 
            cStmt.setNull("p_id", OracleTypes.NUMBER);
        
        cStmt.setString("p_ulice", streetTF.getText());
        cStmt.setInt("p_cislo_popisne", valid.toInteger(streetNumberTF.getText(), "Číslo popisné"));
        cStmt.setString("p_mesto", cityTF.getText());
        cStmt.setInt("p_psc", valid.toInteger(zipCodeTF.getText(), "PSČ"));                
        cStmt.setString("p_zeme", countryTF.getText());
        cStmt.registerOutParameter("p_result", OracleTypes.CLOB);
        valid.validate();
        
        cStmt.execute();
        JSON.checkStatus(cStmt.getString("p_result"));        
        cStmt.close();            
        OracleConnector.getConnection().commit();
        App.setComboItem(JSON.getAsInt("id"));
        App.closeActive();           
    }
    
    @FXML
    public void cancel(ActionEvent ev) throws NoWindowToClose {
        App.closeActive();               
    }

    @Override
    public void setData(Object[] data) {
        confirmButton.setText("Upravit");
        uprava = true;
        
        streetTF.setText(data[1].toString());
        streetNumberTF.setText(data[2].toString());                
        cityTF.setText(data[3].toString());
        zipCodeTF.setText(data[4].toString());
        countryTF.setText(data[5].toString());        
    }
    
}
