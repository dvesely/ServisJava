package database;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;
import javafx.scene.control.ComboBox;
import utils.ItemIdValue;
import static utils.Utils.resultSetToListOfArrayObject;


public class DBComboBox {

    private ComboBox<ItemIdValue> comboBox;
    private String query;
    
    public DBComboBox(ComboBox<ItemIdValue> combo) {
        comboBox = combo;
    }
    
    public void select(ItemIdValue item) {
        try {
            refresh();
        }catch (SQLException e){};        
        comboBox.getSelectionModel().select(item);
    }
    
    public void init(String query) throws SQLException {        
        this.query = query;
        refresh();
    }
    
    public void refresh() throws SQLException {
        Statement stm = OracleConnector.getConnection().createStatement();
        LinkedList<Object[]> data = resultSetToListOfArrayObject(stm.executeQuery(query));
        List<ItemIdValue> list = new LinkedList<>();
        for (Object[] o : data) {
            list.add(new ItemIdValue(Integer.parseInt(o[0].toString()), o[1].toString()));            
        }
        comboBox.getItems().clear();
        comboBox.getItems().setAll(list);
    }
    
}
