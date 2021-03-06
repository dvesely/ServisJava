package controllers.forms;

import alerts.ErrorAlert;
import database.DB;
import database.DBComboBox;
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
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import oracle.jdbc.OracleTypes;
import app.App;
import util.ItemIdValue;
import util.JSON;
import util.Length;
import util.Validator;

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
        
        cStmt = DB.prepareCall("pck_personal.pridej_uprav_personal", 12);
        if (idZamestnance == null) {//insert
            cStmt.setNull("p_id", OracleTypes.NUMBER);
        }else {//update
            cStmt.setInt("p_id", idZamestnance);
        }
        
        cStmt.setString("p_jmeno", jmenoTF.getText());
        cStmt.setString("p_prijmeni", prijmeniTF.getText());
        cStmt.setInt("p_telefon", valid.toInteger(telefonTF.getText(), "Špatný formát telefonu.", true));
        if (emailTF.getText().equals("")) {
           cStmt.setNull("p_email", OracleTypes.VARCHAR);
        }else {
            cStmt.setString("p_email", emailTF.getText());  
        }
        cStmt.setInt("p_pozice_id", valid.comboBoxToInteger(poziceCombo, "Pozice"));  
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
        App.setComboItem(JSON.getAsInt("id"));
        App.closeActiveForm(true);
    }
    
    @FXML
    public void stornujAction(ActionEvent ev) throws NoWindowToClose {
        App.closeActiveForm(false);
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
