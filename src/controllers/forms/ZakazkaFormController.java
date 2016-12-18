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
import app.App;
import database.DBComboBox;
import database.OracleConnector;
import java.sql.PreparedStatement;
import java.time.LocalDate;
import java.util.Map;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import util.ItemIdValue;
import database.Query;
import exceptions.ValidException;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.ResultSet;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.LinkedList;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import oracle.jdbc.OracleTypes;
import tridy.Pocitac;
import user.User;
import util.FormWindow;
import util.JSON;
import util.StringDate;
import util.Validator;

/**
 * FXML Controller class
 *
 * @author Dominik
 */
public class ZakazkaFormController implements Initializable, IFormController {

    @FXML
    private ComboBox<ItemIdValue> clientCombo;    
    //@FXML
    //private DatePicker zacatekDatePicker;    
    @FXML
    private TextField zacatekTF;
    @FXML
    private DatePicker konecDatePicker;  
    @FXML
    private ListView<Pocitac> pocitaceLW;    
    
    @FXML
    private Button upravButton;
    
    private DBComboBox clients;
    
    private Integer zakazkaId;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        clients = new DBComboBox(clientCombo);        
        upravButton.setDisable(true);
        
        Format formatter = new SimpleDateFormat(StringDate.FORMAT);
        zacatekTF.setText(formatter.format(Calendar.getInstance().getTime()));
        konecDatePicker.setValue(LocalDate.now());
        try {            
            clients.init("select id, jmeno||' '||prijmeni||'('||telefon||')' from klienti");            
            refreshPocitace();
        }catch (SQLException ex) {
            new ErrorAlert("Chyba na pri inicializaci formulare zakazky.\n"+ex.getMessage()).showAndWait();            
        }        
    }    
    
    @FXML
    public void potvrdAction(ActionEvent ev) throws NoWindowToClose, SQLException, ValidException {
        Validator valid = new Validator();
        CallableStatement cStmt = DB.prepareCall("pck_zakazky.pridejZakazku", 4);
        cStmt.setInt("p_personal_id", User.getId());        
        cStmt.setInt("p_klient_id", valid.comboBoxToInteger(clientCombo, "Klient"));
        cStmt.setDate("p_datum_priblizne_dokonceni", StringDate.LocalDateToSQLDate(konecDatePicker.getValue()));
        cStmt.registerOutParameter("p_result", OracleTypes.CLOB);
       
        valid.minListSize(pocitaceLW.getItems(), 1, "Zakázka musí obsahovat alespoň 1 počítač.");
        
        try {
            valid.validate();
        }catch (ValidException ex)  {
            cStmt.close();
            throw ex;
        }
        cStmt.execute();
        String result = cStmt.getString("p_result");
        JSON.checkStatus(result);
        int idZakazky = JSON.getAsInt("id");
        cStmt = DB.prepareCall("pck_pocitace.pridej_uprav_pocitac", 5);
        cStmt.registerOutParameter("p_result", OracleTypes.CLOB);
        cStmt.setNull("p_id", OracleTypes.NUMBER);
        //pridani pocitacu        
        for (Pocitac pocitac : pocitaceLW.getItems()) {                        
            cStmt.setClob("p_popis_zavady", DB.createClob(pocitac.getPopis()));
            cStmt.setInt("p_priblizna_cena", pocitac.getCena());
            cStmt.setInt("p_zakazky_id", idZakazky);
            //cStmt.setNull("p_personal_id", OracleTypes.NUMBER);
            cStmt.execute();
            try {
                JSON.checkStatus(cStmt.getString("p_result"));
            }catch (SQLException ex) {  
                try {
                    cStmt.close();                
                }catch (SQLException sqlex) {
                    throw ex;
                }
                throw ex;
            }            
        }
        
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
    
    @FXML
    public void pridejPocitacAction(ActionEvent ev) {
        otevriFormular(false);
    }
    
    @FXML
    public void upravPocitacAction(ActionEvent ev) {
        otevriFormular(true);
    }
    
    @FXML
    public void odeberPocitacAction(ActionEvent ev) {
        
    }
    
    private void pridejPocitac(Pocitac pocitac) {
        if (pocitac != null) pocitaceLW.getItems().add(pocitac);
    }
    
    private void refreshPocitace() {
        if (zakazkaId == null) return;
        
        LinkedList<Pocitac> list = new LinkedList<>();
        try {
            PreparedStatement ps = OracleConnector.getConnection().prepareStatement(
                "select id, popis_zavady, priblizna_cena "
                + "from v_pocitace where zakazky_id = ?");
            ps.setInt(1, zakazkaId);   
            ResultSet rs = ps.executeQuery();         
            for (Map<String, String> oprava : DB.resultSetToListOfMapString(rs)) {             
                list.add(new Pocitac(
                        Integer.parseInt(oprava.get("id")),
                        oprava.get("popis_zavady"), 
                        Integer.parseInt(oprava.get("priblizna_cena"))));
            }
        }catch (SQLException ex) {
            ErrorAlert.show("Chyba při vyběru počítačů z databáze.", ex);
        }
        
         pocitaceLW.getItems().clear();
         pocitaceLW.getItems().setAll(list);
    }
    
    private void otevriFormular(boolean uprava) {        
        FormWindow form = App.createForm("Pocitac");
        if (uprava) {            
            int index = pocitaceLW.getSelectionModel().getSelectedIndex();
            Pocitac pocitac = pocitaceLW.getItems().get(index);            
            ((PocitacFormController)App.getController()).init(pocitac);
            form.showAndWait();
            pocitaceLW.refresh();
        }else {
            form.showAndWait();
            Pocitac pocitac = Pocitac.removePocitac();        
            if (pocitac != null) {
                pridejPocitac(pocitac);
            }
        }        
    }
    
    @Override
    public void setData(Map<String, String> data) {   
//        zakazkaId = Integer.parseInt(data.get("id"));
//        upravButton.setDisable(false); 
//        System.out.println(zakazkaId);
//        clients.select(new ItemIdValue(data.get("klienti_id")));                               
//        zacatekTF.setText(StringDate.formatDate(data.get("datum_prijmuti"), StringDate.FORMAT));
//        konecDatePicker.setValue(StringDate.StringToLocalDate(data.get("datum_priblizne_dokonceni")));
//        refreshPocitace();
    }
    
}
