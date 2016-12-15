package table;

import database.DB;
import database.OracleConnector;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public final class Table {
    
    private TableView<Row> tableView;    
    
    private String query;
    private String fromQuery;//dotaz pro urcity radek podle ID
    private String tableDelete;//tabulka v ktere se bude mazat podle ID
    
    private String formName;
    
    private int countColumns = 0;
    
    public Table(String formName, String query, TableView table) throws SQLException {
        if (table == null) throw new NullPointerException("TableView nesmí být null.");        
        this.tableView = table;        
        this.formName = formName;        
        initQuery(query);        
             
    }
    
    public int getSelectedId() {
        Row selectedRow = tableView.getSelectionModel().getSelectedItem();
        if (selectedRow == null)
            return -1;
        
        return Integer.parseInt(selectedRow.data[0].toString());
    }

    public String getFormName() {
        return formName;
    }
    
    /**
     * Vybere data z databaze pomoci dotazu fromQuery
     * @return Object[] data radku ktery se rovna aktualnimu vybranemu ID
     * @throws SQLException 
     */
    public Map<String, String> getSelectedRow() throws SQLException {
        int id = getSelectedId();
        if (id != -1 && fromQuery != null) {
            PreparedStatement ps = OracleConnector.getConnection()
                    .prepareStatement(fromQuery);
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
            if (tableDelete == null) {
                throw new NullPointerException("Dotaz pro smazani dat neni nastaveny.");
            }
            PreparedStatement ps = OracleConnector.getConnection()
                    .prepareStatement("delete from "+tableDelete+" where id = ?");
            ps.setInt(1, id);            
            boolean r = ps.execute();
            ps.close();
            return r;
        }
        return false;
    }
    
    public void addColumn(String name) {        
        TableColumn<Row, String> tc = new TableColumn(name);   
        //tc.setSortable(false);
        tc.setCellValueFactory(new PropertyValueFactory(name));
        tc.setCellValueFactory(param -> {
            int index = param.getTableView().getColumns().indexOf(param.getTableColumn());
            if (index != -1) {                    
                Object cell = param.getValue().data[index];
                if (cell == null)
                    return new SimpleStringProperty("(null)");                
                else
                    return new SimpleStringProperty(cell.toString());
                
                
            }
            return null;
        });        /*              
        tc.setCellFactory(new Callback<TableColumn<Row, String>, TableCell<Row, String>>() {
            @Override
            public TableCell<Row, String> call(TableColumn<Row, String> param) {
                
            }
        });*/        
        tableView.getColumns().add(tc);                    
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

    public void setRowQuery(String rowQuery) {
        this.fromQuery = rowQuery;
    }

    public void setDeleteQuery(String removeQuery) {
        this.tableDelete = removeQuery;
    }
    
    private ResultSet createColumns() throws SQLException {
        tableView.getColumns().clear();
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
            Object[] row = new Object[countColumns];
            for (int i = 0; i < countColumns; i++) {
                row[i] = data.getString(i+1);
            }            
            items.add(new Row(row));
        }
        setItems(items);        
        tableView.getSelectionModel().select(lastSelected);
    }
    
    private void clearData() {
        tableView.getItems().clear();
    }
    
    public class Row {

        Object[] data;
        
        public Row(Object[] data) {
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
    
    public class Column {

        String name;
        Class type;

        public Column(String name, Class type) {
            this.name = name;
            this.type = type;
        }
        
    }
}

