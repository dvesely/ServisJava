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
import java.sql.CallableStatement;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import oracle.jdbc.OracleTypes;
import user.User;
import util.JSON;

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
    private Button addButton;
    @FXML
    private ContextMenu menu;
    @FXML
    private MenuItem editItem;
    @FXML
    private MenuItem deleteItem;
    @FXML
    private MenuItem selectItem;
    @FXML
    private MenuItem doneItem;
        
    private Table table = null;   
    
    @FXML private AnchorPane anchor;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        menu.getItems().remove(selectItem); 
        menu.getItems().remove(doneItem);
        
    }    
    
    public void init(String title, Table table) {        
        if (table == null) 
            throw new NullPointerException("Třída 'Table' nesmí být null.");
        this.table = table;
        nadpisLabel.setText(title);
        initButtons();        
    }
    
    public TableView getTableView() {
        return tableView;
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
    public void selectAction(ActionEvent ev) throws SQLException {
        if (tableView.getSelectionModel().getSelectedItem() == null) 
            throw new NullPointerException("Nebyl vybrán žádný záznam.");
        CallableStatement cStmt = DB.prepareCall("pck_pocitace.prirad_k_technikovi", 3);
        cStmt.setInt("p_pocitace_id", table.getSelectedId());
        cStmt.setInt("p_personal_id", User.getId());
        cStmt.registerOutParameter("p_result", OracleTypes.CLOB);
        cStmt.execute();
        String result = cStmt.getString("p_result");
        cStmt.close();
        JSON.checkStatus(result);
        DB.commit();        
        table.refreshData();
    }
    
    @FXML
    public void doneAction(ActionEvent ev) throws SQLException {
        if (tableView.getSelectionModel().getSelectedItem() == null) 
            throw new NullPointerException("Nebyl vybrán žádný záznam.");
        CallableStatement cStmt = DB.prepareCall("pck_pocitace.dokonci_pocitac", 2);
        cStmt.setInt("p_pocitace_id", table.getSelectedId());        
        cStmt.registerOutParameter("p_result", OracleTypes.CLOB);
        cStmt.execute();
        String result = cStmt.getString("p_result");        
        cStmt.close();
        JSON.checkStatus(result);
        DB.commit();        
        table.refreshData();
    }
    
    @FXML
    public void deleteAction(ActionEvent ev) throws SQLException {
        
        if (tableView.getSelectionModel().getSelectedItem() == null) 
            throw new NullPointerException("Nebyl vybrán žádný záznam pro smazání.");
        
        table.deleteRow(); 
        DB.commit();        
        table.refreshData();
    }
    
    public void addSelectItem() {
        menu.getItems().add(0,selectItem);
    }
    
    public void addDoneItem() {
        menu.getItems().add(0,doneItem);
    }
    
    /**
     * Inicializace tlacitek pro praci se zaznamy v tabulce
     * editace, mazani a pridavani
     */
    private void initButtons() {
        addButton.setVisible(table.isInsertable());        
        if (!table.isEditable()){//neni povolene upravovani
            menu.getItems().remove(editItem);
        }
        if (!table.isDeletable()){//neni povolene mazani
            menu.getItems().remove(deleteItem);
        }
    }
}
