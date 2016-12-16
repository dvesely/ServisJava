package controllers;

import java.net.URL;
import java.sql.SQLException;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.layout.AnchorPane;
import table.Table;
import app.App;
import database.DB;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;

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
    private ContextMenu menu;
    @FXML
    private MenuItem editItem;
    @FXML
    private MenuItem deleteItem;
        
    private Table table = null;   
    
    @FXML private AnchorPane anchor;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        //deleteItem.setVisible(false);
        //editItem.setVisible(false);
    }    
    
    public Table initTable(String title, String formName, String query) throws SQLException {        
        table = new Table(formName, query, tableView);                    
        nadpisLabel.setText(title);
        return table;
    }
    
    @FXML
    public void addAction(ActionEvent ev) throws SQLException {        
        App.createForm(table.getFormName()).showAndWait();
        DB.commit();
        table.refreshData();
    }
    
    @FXML
    public void editAction(ActionEvent ev) throws SQLException {        
        Map<String, String> data = table.getSelectedRow();
        if (data == null) 
            throw new NullPointerException("Nebyl vybrán žádný záznam pro úpravu.");
        
        App.createForm(table.getFormName(), data).showAndWait();
        DB.commit();        
        table.refreshData();
    }
    
    @FXML
    public void deleteAction(ActionEvent ev) throws SQLException {
        Map<String, String> data = table.getSelectedRow();
        if (data == null) 
            throw new NullPointerException("Nebyl vybrán žádný záznam pro smazání.");
        table.deleteRow();
        DB.commit();        
        table.refreshData();
    }
}
