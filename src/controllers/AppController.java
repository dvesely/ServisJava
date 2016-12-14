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
import utils.App;

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
        Table table = App.showTable("Zakázky", "Order", "select * from v_zakazky");
        table.setRowQuery("select * from zakazky where id = ?");
        table.setDeleteQuery("zakazky");
    }
    
    @FXML
    public void openOpravyAction(ActionEvent e) {
        //App.createView("Opravy");
    }
    
    @FXML
    public void openPersonalAction(ActionEvent e) {
        //App.createView("Personal");
    }
    
}
