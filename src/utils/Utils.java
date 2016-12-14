/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.sql.Clob;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 *
 * @author Dominik
 */
public class Utils {
    
    public static LinkedList<Map<String, String>> resultSetToListOfMapString(ResultSet rs) throws SQLException {
        LinkedList<Map<String, String>> result = new LinkedList<>();
        int count = -1;
        String[] columnNames = new String[]{};
        while (rs.next()) {
            if (count == -1) {
                ResultSetMetaData meta = rs.getMetaData();
                count = meta.getColumnCount();
                columnNames = new String[count];
                for (int i = 0; i < count; i++) {
                    columnNames[i] = meta.getColumnLabel(i+1).toLowerCase();
                }
            }
            Map<String, String> row = new HashMap<>();
            for (int i = 0; i < count; i++) {
                row.put(columnNames[i],rs.getString(i+1));
            }
            result.add(row);
        }        
        return result;
    }
    
    public static LinkedList<String[]> resultSetToListOfArrayString(ResultSet rs) throws SQLException {
        LinkedList<String[]> result = new LinkedList<>();
        int count = -1;        
        while (rs.next()) {
            if (count == -1) {                
                count = rs.getMetaData().getColumnCount();                
            }
            String[] row = new String[count];
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
