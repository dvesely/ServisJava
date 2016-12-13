/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import alerts.ErrorAlert;
import database.DBComboBox;
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
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import oracle.jdbc.OracleTypes;
import utils.App;
import utils.ItemIdValue;
import utils.JSON;
import utils.Validator;

/**
 * FXML Controller class
 *
 * @author Dominik
 */
public class ClientFormController implements Initializable, IFormController {

    @FXML
    private TextField jmenoTF;
    @FXML
    private TextField prijmeniTF;    
    @FXML
    private TextField uliceTF;        
    @FXML
    private TextField cisloTF;        
    @FXML
    private TextField mestoTF;        
    @FXML
    private TextField pscTF;        
    @FXML
    private TextField zemeTF;        
    @FXML
    private TextField telefonTF;
    @FXML
    private TextField emailTF;        
    
    private Integer idKlienta;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {                     
    }    
    
    @FXML
    public void add(ActionEvent ev) throws SQLException, NoWindowToClose, ValidException {      
        Validator valid = new Validator();        
        CallableStatement cStmt;
        String procedureAdd = "{call pck_klienti.pridej_klienta(?, ?, ?, ?, ?, ?)}";
        String procedureUpdate = "{call pck_klienti.uprav_klienta(?, ?, ?, ?, ?, ?, ?)}";
        
        if (idKlienta != null) {
             cStmt = OracleConnector.getConnection().prepareCall(procedureAdd);
             cStmt.setInt("p_id", idKlienta);
        }else {
            cStmt = OracleConnector.getConnection().prepareCall(procedureUpdate);            
        } 
        
        cStmt.setString("p_jmeno", jmenoTF.getText());
        cStmt.setString("p_prijmeni", prijmeniTF.getText());
        cStmt.setInt("p_telefon", valid.toInteger(telefonTF.getText(), "Telefon musí obsahovat pouze čísla."));
        cStmt.setString("p_email", emailTF.getText());                        
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
    public void stornujAction(ActionEvent ev) throws NoWindowToClose {
        App.closeActive();        
    }

    @Override
    public void setData(Object[] data) throws NumberFormatException {        
        idKlienta = new Integer(data[0].toString());
        jmenoTF.setText(data[1].toString());
        prijmeniTF.setText(data[2].toString());                
        telefonTF.setText(data[3].toString());
        emailTF.setText(data[4].toString());                
    }
}
