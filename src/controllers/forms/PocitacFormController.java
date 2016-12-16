/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers.forms;

import alerts.ErrorAlert;
import app.App;
import database.DB;
import database.DBComboBox;
import database.OracleConnector;
import database.Query;
import exceptions.NoWindowToClose;
import exceptions.ValidException;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import java.util.Map;
import javafx.event.ActionEvent;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import util.ItemIdValue;
import java.sql.SQLException;
import java.util.LinkedList;
import javafx.scene.control.ListView;
import tridy.Pocitac;
import util.Validator;

/**
 * FXML Controller class
 *
 * @author Dominik
 */
public class PocitacFormController implements Initializable, IFormController {

    
    @FXML
    private ComboBox<ItemIdValue> zakazkaCombo;    
    @FXML
    private TextArea popisTA;
    @FXML
    private TextField pribliznaCenaTF;
    @FXML
    private VBox opravyVBox;
    @FXML
    private Label celkovaCenaLabel;    
    @FXML
    private ListView<ItemIdValue> opravyLW;
    
    private DBComboBox zakazka;
    
    private Integer pocitacId;
    private Pocitac pocitac;
    boolean editace = false;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        zakazka = new DBComboBox(zakazkaCombo);
        try {
            zakazka.init("select pocitace.id, pocitace.id||'('||jmeno||' '||prijmeni||')' "
            + "from pocitace "
            + "join personal on personal.id = personal_id");
        }catch (SQLException ex){
            ErrorAlert.show("Chyba pri vyberu zakazek z databaze.");
        };        
    }    

    @FXML
    public void potvrdAction(ActionEvent ev) throws NoWindowToClose, ValidException {
        Validator valid = new Validator();
        int cena = valid.toInteger(pribliznaCenaTF.getText(), "Přibližná cena");
        
        valid.validate();
        if (editace || pocitac == null) {
            Pocitac.setPocitac(new Pocitac(popisTA.getText(), cena));
        }else {
            pocitac.setPopis(popisTA.getText());
            pocitac.setCena(cena);
        }
            
        App.closeActiveForm(true);
    }
    
    @FXML
    public void stornujAction(ActionEvent ev) throws NoWindowToClose {
        App.closeActiveForm(false);
    }
    
    @FXML
    public void pridejOpravuAction(ActionEvent ev) throws SQLException {
        App.createForm("Oprava").showAndWait();
        refreshOpravy();
    }
    
    @FXML
    public void upravOpravuAction(ActionEvent ev) throws SQLException {
        PreparedStatement ps = OracleConnector.getConnection().prepareStatement(Query.OPRAVY_FORM);
        ps.setInt(1, getSelectId());
        ResultSet rs = ps.executeQuery();
        Map<String, String> data = DB.resultSetToMapString(rs);
        rs.close();
        App.createForm("Oprava", data).showAndWait();
        refreshOpravy();
    }
    
    @FXML
    public void odeberOpravuAction(ActionEvent ev) throws SQLException {
        PreparedStatement ps = OracleConnector.getConnection()
                .prepareStatement("delete from opravy where id = ?");
        ps.setInt(1, getSelectId());
        ps.execute();
        ps.close();
        refreshOpravy();
    }
    
    private int getSelectId() {
        ItemIdValue item = opravyLW.getSelectionModel().getSelectedItem();        
        if (item == null) return -1;
        
        return item.getId();
    }
    
    public void init(Pocitac pocitac) {
        popisTA.setText(pocitac.getPopis());
        pribliznaCenaTF.setText(String.valueOf(pocitac.getCena()));
        this.pocitac = pocitac;
    }
    
    public void refreshOpravy() throws SQLException {
        if (pocitacId == null) return;
        LinkedList<ItemIdValue> list = new LinkedList<>();
        
        PreparedStatement ps = OracleConnector.getConnection().prepareStatement(
                "select id, typ_komponenty||' ('||cena||' Kč)' value "
                + "from v_opravy where pocitace_id = ?");
         ps.setInt(1, pocitacId);   
         ResultSet rs = ps.executeQuery();         
         for (Map<String, String> oprava : DB.resultSetToListOfMapString(rs)) {             
             list.add(new ItemIdValue(Integer.parseInt(oprava.get("id")), oprava.get("value")));
         }
         opravyLW.getItems().clear();
         opravyLW.getItems().setAll(list);
    }
    
    @Override
    public void setData(Map<String, String> data) {   
        editace = true;
        opravyVBox.setVisible(true);
        pocitacId = Integer.parseInt(data.get("id"));
        
        popisTA.setText(data.get("popis_zavady"));
        pribliznaCenaTF.setText(data.get("priblizna_cena"));
        
        try {
            refreshOpravy();
        }catch (SQLException ex) {
            ErrorAlert.show("Opravy nebyly načteny...", ex);            
        }
        
    }
}
