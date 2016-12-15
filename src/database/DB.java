/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package database;

import java.io.BufferedReader;
import java.io.IOException;
import java.sql.CallableStatement;
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
public class DB {
    
    /**
     * Vraci true pokud se commit provedl uspesne.
     * @return boolean
     */
    public static boolean commit() {
        System.out.println("Commiting");
        try {
            OracleConnector.getConnection().commit();
            return true;
        }catch (SQLException ex) {
            System.err.println("Commit: "+ex.getMessage());
            return false;
        }
    }
    /**
     * Vraci true pokud se rollback provedl uspesne.
     * @return boolean
     */
    public static boolean rollback() {
        System.out.println("Rollbacking");
        try {
            OracleConnector.getConnection().rollback();
            return true;
        }catch (SQLException ex) {
            System.err.println("Rollback: "+ex.getMessage());
            return false;
        }
    }
    
    public static CallableStatement prepareCall(String procedure, int countParameters) throws SQLException {
        if (countParameters < 1) 
            throw new IllegalArgumentException("Počet parametrů u procedury musí být větší než 0.");  
        StringBuilder sb = new StringBuilder("{call ").append(procedure).append("(?");
        for (int i = 1; i < countParameters; i++) {
            sb.append(",?");
        }
        sb.append(")}");        
        return OracleConnector.getConnection().prepareCall(sb.toString());
    }
    
    public static Map<String, String> resultSetToMapString(ResultSet rs) throws SQLException {
        LinkedList<Map<String, String>> list = resultSetToListOfMapString(rs);
        if (!list.isEmpty()) {
            return list.getFirst();
        }
        return null;
    }
    
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
    
}
