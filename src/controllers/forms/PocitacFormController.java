/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers.forms;

import alerts.ErrorAlert;
import app.App;
import database.DB;
import database.OracleConnector;
import database.Query;
import exceptions.NoWindowToClose;
import exceptions.ValidException;
import java.net.URL;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
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
import oracle.jdbc.OracleTypes;
import privileges.Opravneni;
import privileges.Pozice;
import tridy.Pocitac;
import util.FormWindow;
import util.JSON;
import util.Validator;

/**
 * FXML Controller class
 *
 * @author Dominik
 */
public class PocitacFormController implements Initializable, IFormController {

    @FXML
    private Label pocitacIdLabel;
    @FXML
    private Label zakazkaLabel;
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
    
    private Integer zakazkaId;
    private Integer pocitacId;  
    private boolean editace = false;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        opravyVBox.setVisible(editace);
        if (Opravneni.pristupPouze(Pozice.TECHNIK)) {
            popisTA.setEditable(false);
            pribliznaCenaTF.setEditable(false);
        }
    }    

    @FXML
    public void potvrdAction(ActionEvent ev) throws NoWindowToClose, ValidException, SQLException {
        Validator valid = new Validator();
        int cena = valid.toInteger(pribliznaCenaTF.getText(), "Přibližná cena");
        
        valid.validate();
        if (!editace) {//editace nebo prace s pocitacem pres zakazku
            Pocitac.setPocitac(new Pocitac(popisTA.getText(), cena));
        }else {//jedna se o upravu v tabulce pocitace
            CallableStatement cStmt = DB.prepareCall("pck_pocitace.pridej_uprav_pocitac", 5);
            cStmt.registerOutParameter("p_result", OracleTypes.CLOB);
            cStmt.setInt("p_id", pocitacId);
            cStmt.setClob("p_popis_zavady", DB.createClob(popisTA.getText()));
            cStmt.setInt("p_priblizna_cena", Integer.parseInt(pribliznaCenaTF.getText()));
            cStmt.setInt("p_zakazky_id", zakazkaId);            
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
    public void pridejOpravuAction(ActionEvent ev) throws SQLException {
        FormWindow form = App.createForm("Oprava");        
        ((OpravaFormController)App.getController()).setPocitacId(pocitacId);
        form.showAndWait();
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
        CallableStatement cStmt = DB.prepareCall("pck_opravy.smaz_opravu", 2);
        cStmt.setInt(1, getSelectId());        
        cStmt.registerOutParameter(2, OracleTypes.CLOB);
        cStmt.execute();
        String result = cStmt.getString(2);
        cStmt.close();
        JSON.checkStatus(result);
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
    }
    
    public void refreshOpravy() throws SQLException {
        if (!editace) return;
        LinkedList<ItemIdValue> list = new LinkedList<>();
        int celkovaCena = 0;
        
        PreparedStatement ps = OracleConnector.getConnection().prepareStatement(
                "select id, typ_komponenty||' ('||cena||' Kč)' value, cena "
                + "from v_opravy where pocitace_id = ?");
         ps.setInt(1, pocitacId);   
         ResultSet rs = ps.executeQuery();         
         for (Map<String, String> oprava : DB.resultSetToListOfMapString(rs)) {             
             list.add(new ItemIdValue(Integer.parseInt(oprava.get("id")), oprava.get("value")));
             celkovaCena += Integer.parseInt(oprava.get("cena"));
         }
         opravyLW.getItems().clear();
         opravyLW.getItems().setAll(list);
         celkovaCenaLabel.setText(String.valueOf(celkovaCena));
    }
    
    @Override
    public void setData(Map<String, String> data) {           
        editace = true;
        zakazkaId = Integer.parseInt(data.get("zakazky_id"));
        pocitacId = Integer.parseInt(data.get("id"));
        
        zakazkaLabel.setText("#"+zakazkaId);
        pocitacIdLabel.setText("#"+pocitacId);
        popisTA.setText(data.get("popis_zavady"));
        pribliznaCenaTF.setText(String.valueOf(data.get("priblizna_cena")));
        
        opravyVBox.setVisible(true);
        try {
            refreshOpravy();
        }catch (SQLException ex) {
            ErrorAlert.show("Opravy nebyly načteny...", ex);            
        }
        
    }
}
