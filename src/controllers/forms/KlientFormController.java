/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers.forms;

import database.DB;
import database.OracleConnector;
import exceptions.NoWindowToClose;
import exceptions.ValidException;
import forms.FormControl;
import java.net.URL;
import java.sql.CallableStatement;
import java.sql.SQLException;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import oracle.jdbc.OracleTypes;
import app.App;
import util.JSON;
import util.Length;
import util.Validator;

/**
 * FXML Controller class
 *
 * @author Dominik
 */
public class KlientFormController implements Initializable, IFormController {

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
        FormControl.addLengthLimit(jmenoTF, Length.JMENO);
        FormControl.addLengthLimit(prijmeniTF, Length.PRIJMENI);
        FormControl.addLengthLimit(uliceTF, Length.ULICE);
        FormControl.addLengthLimit(cisloTF, Length.CISLO_POPISNE);
        FormControl.addLengthLimit(mestoTF, Length.MESTO);        
        FormControl.addLengthLimit(pscTF, Length.PSC);
        FormControl.addLengthLimit(zemeTF, Length.ZEME);
        FormControl.addLengthLimit(telefonTF, Length.TELEFON);
        FormControl.addLengthLimit(emailTF, Length.EMAIL);
    }    
    
    /**
     * Pridani noveho klienta nebo jeho uprava podle ID
     * @param ev
     * @throws SQLException
     * @throws NoWindowToClose
     * @throws ValidException 
     */
    @FXML
    public void potvrdAction(ActionEvent ev) throws SQLException, NoWindowToClose, ValidException {              
               
        Validator valid = new Validator();        
        CallableStatement cStmt;        
        
        if (idKlienta == null) {//insert
            cStmt = DB.prepareCall("pck_klienti.pridej_klienta", 10);
        }else {//update
            cStmt = DB.prepareCall("pck_klienti.uprav_klienta", 11);
            cStmt.setInt("p_id", idKlienta);            
        }
        
        cStmt.setString("p_jmeno", jmenoTF.getText());
        cStmt.setString("p_prijmeni", prijmeniTF.getText());
        cStmt.setString("p_ulice", uliceTF.getText());                        
        cStmt.setInt("p_cislo_popisne", valid.toInteger(cisloTF.getText(), "Číslo popisné"));
        cStmt.setString("p_mesto", mestoTF.getText());                        
        cStmt.setInt("p_psc", valid.toInteger(pscTF.getText(), "PSČ"));
        cStmt.setString("p_zeme", zemeTF.getText());                        
        cStmt.setInt("p_telefon", valid.toInteger(telefonTF.getText(), "Telefon"));
        if (emailTF.getText().equals("")) {
           cStmt.setNull("p_email", OracleTypes.VARCHAR);
        }else {
            cStmt.setString("p_email", emailTF.getText());  
        }
        
        cStmt.registerOutParameter("p_result", OracleTypes.CLOB);
        
        valid.validate();
        
        cStmt.execute();        
        JSON.checkStatus(cStmt.getString("p_result"));        
        cStmt.close();                    
        App.setComboItem(JSON.getAsInt("id"));
        App.closeActiveForm(true);
    }
    
    @FXML
    public void stornujAction(ActionEvent ev) throws NoWindowToClose {
        App.closeActiveForm(false);
    }

    @Override
    public void setData(Map<String, String> data) throws NumberFormatException {        
        idKlienta = new Integer(data.get("id"));        
        
        jmenoTF.setText(data.get("jmeno"));
        prijmeniTF.setText(data.get("prijmeni"));                        
        //adresa
        uliceTF.setText(data.get("ulice"));
        cisloTF.setText(data.get("cislo_popisne"));
        mestoTF.setText(data.get("mesto"));
        pscTF.setText(data.get("psc"));
        zemeTF.setText(data.get("zeme"));
        //email
        emailTF.setText(data.get("email"));
        //telefon
        telefonTF.setText(data.get("telefon"));               
    }
}
