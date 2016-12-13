/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

import database.OracleConnector;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Clob;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import javafx.scene.control.ComboBox;

/**
 *
 * @author Dominik
 */
public class Utils {
    
    public static LinkedList<Object[]> resultSetToListOfArrayObject(ResultSet rs) throws SQLException {
        LinkedList<Object[]> result = new LinkedList<>();
        int count = -1;
        while (rs.next()) {
            if (count == -1) {
                count = rs.getMetaData().getColumnCount();
            }
            Object[] row = new Object[count];
            for (int i = 0; i < count; i++) {
                row[i] = rs.getString(i+1);
            }
            result.add(row);
        }        
        return result;
    }
    
    public static String clobToString(Clob clob) {        
        try {
            BufferedReader br = new BufferedReader(clob.getCharacterStream());
            String line;
            String result = "";
            while ((line = br.readLine()) != null){result += line;};
            return result;
        }catch (SQLException | IOException e) {
            return null;
        }
        
    }
}
