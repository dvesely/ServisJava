package table;

import database.OracleConnector;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import utils.Utils;

public final class Table {
    
    private TableView<Row> tableView;    
    
    private String query;
    private String rowQuery;//dotaz pro urcity radek podle ID
    private String tableDelete;//tabulka v ktere se bude mazat podle ID
    
    private String formName;
    
    private int countColumns = 0;
    
    public Table(String formName, String query, TableView table) throws SQLException {
        if (table == null) throw new NullPointerException("TableView nesmí být null.");        
        this.tableView = table;        
        this.formName = formName;
        setQuery(query);        
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
     * Vybere data z databaze pomoci dotazu rowQuery
     * @return Object[] data radku ktery se rovna aktualnimu vybranemu ID
     * @throws SQLException 
     */
    public Object[] getSelectedRow() throws SQLException {
        int id = getSelectedId();
        if (id != -1 && rowQuery != null) {
            PreparedStatement ps = OracleConnector.getConnection()
                    .prepareStatement(rowQuery);
            ps.setInt(1, id);
            Object[] r = Utils.resultSetToListOfArrayObject(ps.executeQuery()).getFirst();
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
        tc.setSortable(false);
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
        });
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
    
    public void setQuery(String query) throws SQLException {
        this.query = query;
        ResultSet data = createColumns();
        refreshData(data);
        data.close();
    }

    public void setRowQuery(String rowQuery) {
        this.rowQuery = rowQuery;
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
    }
    
    private void clearData() {
        tableView.getItems().clear();
    }
    
    public class Row {

        Object[] data;
        
        public Row(Object[] data) {
            this.data = data;
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

