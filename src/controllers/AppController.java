/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import table.Table;
import app.App;
import database.Query;

/**
 * FXML Controller class
 *
 * @author Dominik
 */
public class AppController implements Initializable {

    @FXML
    private Label usernameLabel;
    @FXML
    private Label positionLabel;    
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        App.setTitle(null);
    }    
    
    @FXML
    public void logoutAction(ActionEvent e) {        
        App.setScene("Login");
    }
    
    @FXML
    public void openZakazkyAction(ActionEvent e) throws SQLException {
        Table table = App.showTable("Zakázky", "Zakazka", "select * from v_zakazky");
        table.setRowQuery(Query.ZAKAZKY_FORM);
        table.setDeleteProcedure("pck_zakazky.smaz_zakazku");
    }
    
    @FXML
    public void openPocitaceAction(ActionEvent e) throws SQLException {
        Table table = App.showTable("Počítače", "Pocitac", "select * from v_pocitace");
        table.setRowQuery(Query.POCITACE_FORM);
        table.setDeleteProcedure("pck_pocitace.smaz_pocitac");
    }    
    
    @FXML
    public void openPersonalAction(ActionEvent e) throws SQLException {
        Table table = App.showTable("Personál", "Personal", "select * from v_personal");
        table.setRowQuery(Query.PERSONAL_FORM);
        table.setDeleteProcedure("pck_personal.smaz_personal");
    }
    
    @FXML
    public void openKlientiAction(ActionEvent e) throws SQLException {
        Table table = App.showTable("Klienti", "Klient", "select * from v_klienti");
        table.setRowQuery(Query.KLIENT_FORM);
        table.setDeleteProcedure("pck_klienti.smaz_klienta");
    }
    
    @FXML
    public void openOpravyAction(ActionEvent e) throws SQLException {
        Table table = App.showTable("Opravy", "Oprava", "select * from v_opravy");
        table.setRowQuery(Query.OPRAVY_FORM);
        table.setDeleteProcedure("pck_opravy.smaz_opravu");
    }
    
    
}
