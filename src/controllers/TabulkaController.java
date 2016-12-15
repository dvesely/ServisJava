/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import java.net.URL;
import java.sql.SQLException;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TableView;
import table.Table;
import utils.App;

/**
 * FXML Controller class
 *
 * @author Dominik
 */
public class TabulkaController implements Initializable {

    @FXML
    private Label nadpisLabel;
    @FXML
    private TableView tableView;
    @FXML
    private ProgressIndicator spinner;
    
    private Table table = null;     
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {}    
    
    public Table initTable(String title, String formName, String query) throws SQLException {        
        table = new Table(formName, query, tableView);                    
        nadpisLabel.setText(title);
        return table;
    }
    
    @FXML
    public void add(ActionEvent ev) throws SQLException {        
        App.createForm(table.getFormName()).showAndWait();        
    }
    
    @FXML
    public void edit(ActionEvent ev) throws SQLException {
        Map<String, String> data = table.getSelectedRow();
        if (data == null) {
            table.refreshData();
            return;
        }
        App.createForm(table.getFormName(), data).showAndWait();        
    }
    
    private void disableTable(boolean disable) {
        tableView.setDisable(disable);
        spinner.setVisible(disable);
    }
}