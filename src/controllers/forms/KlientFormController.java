/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers.forms;

import database.OracleConnector;
import exceptions.NoWindowToClose;
import exceptions.ValidException;
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
import utils.App;
import utils.ItemIdValue;
import utils.JSON;
import utils.Validator;

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
        /**
         *     procedure pridej_uprav_personal_a_adresu(p_id number,
    p_jmeno varchar2, p_prijmeni varchar2, p_telefon number, 
    p_email varchar2, p_pozice_id number, p_ulice varchar2, p_cislo_popisne number,
    p_mesto varchar2, p_psc number, p_zeme varchar2, p_result out clob)
         */
        String procedureAdd = "{call pck_personal.pridej_uprav_personal_a_adresu"
                + "(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}";        
        
        cStmt = OracleConnector.getConnection().prepareCall(procedureAdd);
        if (idKlienta == null) {//insert
            cStmt.setNull("p_id", OracleTypes.NUMBER);
        }else {//update
            cStmt.setInt("p_id", idKlienta);
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
    public void setData(Map<String, String> data) throws NumberFormatException {        
        idKlienta = new Integer(data.get("id"));
        
        jmenoTF.setText(data.get("jmeno"));
        prijmeniTF.setText(data.get("prijmeni"));                        
        //adresa
        uliceTF.setText(data.get("ulice"));
        cisloTF.setText(data.get("cislo"));
        mestoTF.setText(data.get("mesto"));
        pscTF.setText(data.get("psc"));
        zemeTF.setText(data.get("zeme"));
        //email
        emailTF.setText(data.get("email"));
        //telefon
        telefonTF.setText(data.get("telefon"));               
    }
}
