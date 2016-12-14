/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers.forms;

import alerts.ErrorAlert;
import database.DBComboBox;
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
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import oracle.jdbc.OracleTypes;
import utils.App;
import utils.ItemIdValue;
import utils.JSON;
import utils.Utils;
import utils.Validator;

/**
 * FXML Controller class
 *
 * @author Dominik
 */
public class PersonalFormController implements Initializable, IFormController {

    @FXML
    private TextField jmenoTF;
    @FXML
    private TextField prijmeniTF;    
    @FXML
    private ComboBox<ItemIdValue> poziceCombo;
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
    
    private DBComboBox poziceDBCombo;    
    
    private Integer idZamestnance;
    
    public void initialize(URL url, ResourceBundle rb) {
        poziceDBCombo = new DBComboBox(poziceCombo);        
        try {
            poziceDBCombo.init("select id, nazev from v_pozice");            
        }catch (SQLException ex) {
            new ErrorAlert("Chyba na strane serveru.").showAndWait();
            ex.printStackTrace();
        }        
    }    
    
    @FXML
    public void potvrdAction(ActionEvent ev) throws SQLException, ValidException, NoWindowToClose {
        Validator valid =  new Validator();
        CallableStatement cStmt;        
        String procedureAdd = "{call pck_personal.pridej_uprav_personal_a_adresu"
                + "(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}";        
        
        cStmt = OracleConnector.getConnection().prepareCall(procedureAdd);
        if (idZamestnance == null) {//insert
            cStmt.setNull("p_id", OracleTypes.NUMBER);
        }else {//update
            cStmt.setInt("p_id", idZamestnance);
        }
        
        cStmt.setString("p_jmeno", jmenoTF.getText());
        cStmt.setString("p_prijmeni", prijmeniTF.getText());
        cStmt.setInt("p_telefon", valid.toInteger(telefonTF.getText(), "Telefon"));
        cStmt.setString("p_email", emailTF.getText());  
        cStmt.setInt("p_pozice_id", valid.comboBoxToInteger(poziceCombo));  
        cStmt.setString("p_ulice", uliceTF.getText());  
        cStmt.setInt("p_cislo_popisne", valid.toInteger(cisloTF.getText(), "Číslo popisné"));  
        cStmt.setString("p_mesto", mestoTF.getText());  
        cStmt.setInt("p_psc", valid.toInteger(pscTF.getText(), "PSČ"));  
        cStmt.setString("p_zeme", zemeTF.getText());  
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
    public void setData(Map<String, String> data) {       
        idZamestnance = new Integer(data.get("id"));
        
        jmenoTF.setText(data.get("jmeno"));
        prijmeniTF.setText(data.get("prijmeni"));                        
        //pozice
        poziceCombo.getSelectionModel().select(new ItemIdValue(data.get("pozice_id")));
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
