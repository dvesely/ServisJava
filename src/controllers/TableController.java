/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import database.OracleConnector;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TableView;
import table.Table;
import utils.App;

/**
 * FXML Controller class
 *
 * @author Dominik
 */
public class TableController implements Initializable {

    @FXML
    private TableView tableView;
    @FXML
    private ProgressIndicator spinner;
    
    private Table table = null;     
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {}    
    
    public Table initTable(String formName, String query) throws SQLException {        
        table = new Table(formName, query, tableView);                    
        return table;
    }
    
    @FXML
    public void add(ActionEvent ev) throws SQLException {        
        App.createForm(table.getFormName()).showAndWait();        
    }
    
    @FXML
    public void edit(ActionEvent ev) throws SQLException {
        Object[] data = table.getSelectedRow();
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
