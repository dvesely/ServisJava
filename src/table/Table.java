package table;

import controllers.TabulkaController;
import database.DB;
import database.OracleConnector;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import oracle.jdbc.OracleTypes;
import user.User;
import util.JSON;
import util.StringDate;

public final class Table {

    private static final String NULL_VALUE = "(nevyplněno)";//String reprezuntujici hodnotu null
    
    private TableView<Row> tableView;    
    private TabulkaController controller;
    private List<String> columnNames = new LinkedList<>();
    
    private String query;
    private String formQuery;//dotaz pro urcity radek podle ID
    
    private String deleteProcedure;//tabulka v ktere se bude mazat podle ID, pokud je null => nelze mazat
    
    private boolean onlyOwnRow; //flag pro kontrolu, zda muze uzivatel upravovat zaznamy ktere jsou jeho
    private boolean editable; //flag pro kontrolu zda se muze editovat
    private boolean insertable;//flag pro kontrolu zda se muze vkladat
    
    private String formName;
    private String title;
    
    private int countColumns = 0;
    
    public Table(TabulkaController controller, String title, String formName, String query) throws SQLException {
        if (controller == null) 
            throw new NullPointerException("TabulkaController nesmí být null.");
        this.controller = controller;
        this.title = title;        
        this.tableView = controller.getTableView();             
        this.formName = formName;            
        initQuery(query);     
    }  
    
    public void setMenuItems(boolean select, boolean done) {        
        if (select)
            controller.addSelectItem();
        if (done)
            controller.addDoneItem();
    }
    
    public void show() { 
        controller.init(title, this);
        ((Stage)tableView.getScene().getWindow()).show();
    }
    
    public int getSelectedId() {
        Row selectedRow = tableView.getSelectionModel().getSelectedItem();
        if (selectedRow == null)
            return -1;
        
        return Integer.parseInt(selectedRow.data[0]);
    }
    
    public String getFormName() {
        return formName;
    }
    
    /**
     * Vrací true, pokud se můžou vkládat záznamy
     * @return 
     */
    public boolean isInsertable() {
        return insertable;
    }
    
    public void setInsertable(boolean insertable) {
        this.insertable = insertable;
    }

    public boolean onlyOwnRow() {
        return onlyOwnRow;
    }

    public void setOnlyOwnRow(boolean onlyOwnRow) {
        this.onlyOwnRow = onlyOwnRow;
    }
    
    /**
     * Vrací true, pokud se můžou editovat záznamy
     * @return 
     */
    public boolean isEditable() {
        return formQuery != null;
    }
    
    /**
     * Vrací true, pokud se můžou mazat záznamy
     * @return 
     */
    public boolean isDeletable() {
        return deleteProcedure != null;
    }
    
    public void setDeleteProcedure(String nameProcedure) {
        this.deleteProcedure = nameProcedure;
    }
    
    /**
     * Vybere data z databaze pomoci dotazu fromQuery
     * @return String[] data radku ktery se rovna aktualnimu vybranemu ID
     * @throws SQLException 
     */
    public Map<String, String> getSelectedRow() throws SQLException {
        int id = getSelectedId();
        if (id != -1 && formQuery != null) {            
            PreparedStatement ps = OracleConnector.getConnection()
                    .prepareStatement(formQuery);
            ps.setInt(1, id);            
            Map<String, String> r = DB.resultSetToMapString(ps.executeQuery());            
            ps.close();
            return r;
        }
        return null;
    }
    
    public boolean deleteRow() throws SQLException {
        int id = getSelectedId();
        if (id != -1) {
            if (!isDeletable()) {
                throw new NullPointerException("V tabulce neni povolené mazat záznamy.");
            }
            if (onlyOwnRow()) {
                String hledanySloupec = "PERSONAL_ID";
                Row row = tableView.getSelectionModel().getSelectedItem();                
                int i = 0;
                for (String column : columnNames) {                    
                    if (column.equals(hledanySloupec)) {                        
                        break;
                    }
                    i++;
                }                
                if (i > 0 && i < countColumns) {                    
                    if (!row.data[i].equals(String.valueOf(User.getId()))) {
                        throw new SQLException("Nelze mazat cizí zakázky.");
                    }
                }
                
            }
            CallableStatement cStmt = DB.prepareCall(deleteProcedure, 2);
            cStmt.setInt(1, id);
            cStmt.registerOutParameter(2, OracleTypes.CLOB);                       
            cStmt.execute();
            String result = cStmt.getString(2);
            cStmt.close();
            JSON.checkStatus(result);
            return true;
        }
        return false;
    }
    
    public void addColumn(String name) {           
        TableColumn<Row, String> tc = new TableColumn(name);   
        tc.setSortable(false);
        tc.setCellValueFactory(new PropertyValueFactory(name));
        tc.setCellValueFactory(param -> {
            int index = param.getTableView().getColumns().indexOf(param.getTableColumn());
            if (index != -1) {                    
                String cell = param.getValue().data[index];
                if (cell == null)
                    return new SimpleStringProperty(NULL_VALUE);                
                else
                    return new SimpleStringProperty(cell);
                
                
            }
            return null;
        });        
        tableView.getColumns().add(tc);                    
        columnNames.add(name);
    }
    
    public void setItems(ObservableList<Row> items) {                
        tableView.getItems().setAll(items);        
    }
    
    public void refreshData() throws SQLException {
        Statement stm = OracleConnector.getConnection().createStatement();
        ResultSet rs = stm.executeQuery(query);
        refreshData(rs);
        rs.close();
    }
    
    public void initQuery(String query) throws SQLException {
        this.query = query;
        ResultSet data = createColumns();
        refreshData(data);
        data.close();
    }
    
    public void setUpdateQuery(String rowQuery) {
        this.formQuery = rowQuery;
    }
    
    private ResultSet createColumns() throws SQLException {
        tableView.getColumns().clear();
        columnNames.clear();
        Statement stm = database.OracleConnector.getConnection()
                .createStatement();
        ResultSet rs = stm.executeQuery(query);
        //columns            
        ResultSetMetaData meta = rs.getMetaData();            
        countColumns = meta.getColumnCount();

        for (int i = 1; i <= countColumns; i++) {                
            addColumn(meta.getColumnLabel(i));
        }                    
        return rs;        
    }
    
    private void refreshData(ResultSet data) throws SQLException {
        Row lastSelected = tableView.getSelectionModel().getSelectedItem();
        clearData();
        ObservableList<Row> items = FXCollections.observableArrayList();                
        while (data.next()) {
            String[] row = new String[countColumns];
            for (int i = 0; i < countColumns; i++) {
                row[i] = data.getString(i+1);                
                if (StringDate.isDate(row[i])) {
                    row[i] = StringDate.formatDate(row[i], StringDate.FORMAT);
                }
            }            
            items.add(new Row(row));
        }
        setItems(items);        
        tableView.getSelectionModel().select(lastSelected);
    }
    
    private void clearData() {
        tableView.getItems().clear();
    }
    
    class Row {

        String[] data;
        
        public Row(String[] data) {
            this.data = data;
        }

        @Override
        public int hashCode() {
            int hash = 7;      
            int hashId;
            if (data.length == 0) {
                hashId = -1;
            }else {
                hashId = data[0].hashCode();
            }
            hash = 97 * hash + hashId;
            return hash;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final Row other = (Row) obj;
            if (this.data.length!=other.data.length) {                
                return false;
            }
            if (this.data.length > 0 && !this.data[0].equals(other.data[0])) {
                //prvni parametry se nerovnaji
                return false;
            }
            return true;
        }
    }
}

