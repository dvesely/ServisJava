package database;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;
import javafx.scene.control.ComboBox;
import utils.ItemIdValue;


public class DBComboBox {

    private ComboBox<ItemIdValue> comboBox;
    private String query;
    
    public DBComboBox(ComboBox<ItemIdValue> combo) {
        comboBox = combo;
    }
    
    public ItemIdValue getItem() {
        return comboBox.getSelectionModel().getSelectedItem();
    }
    
    public void select(ItemIdValue item) {
        if (item != null)
            comboBox.getSelectionModel().select(item);
    }
    
    public void init(String query) throws SQLException {        
        this.query = query;
        refresh();
    }
    
    public void refresh() throws SQLException {
        Statement stm = OracleConnector.getConnection().createStatement();
        LinkedList<String[]> data = DB.resultSetToListOfArrayString(stm.executeQuery(query));
        List<ItemIdValue> list = new LinkedList<>();
        for (Object[] o : data) {
            list.add(new ItemIdValue(Integer.parseInt(o[0].toString()), o[1].toString()));            
        }
        ItemIdValue selectedItem = comboBox.getSelectionModel().getSelectedItem();
        comboBox.getItems().clear();
        comboBox.getItems().setAll(list);
        comboBox.getSelectionModel().select(selectedItem);
    }
    
}
