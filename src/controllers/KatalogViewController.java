/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import alerts.ErrorAlert;
import database.OracleConnector;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;


public class KatalogViewController implements Initializable {

    private final String[] NOT_INCLUDE_TYPES = new String[]{
        "LOB", "PROCEDURE", "TABLE" 
    };
    private final String[] NAMES = new String[]{
        "SUBOBJECT_NAME", "CREATED", "LAST_DDL_TIME", "TIMESTAMP", 
        "STATUS", "TEMPORARY", "GENERATED", "SECONDARY","NAMESPACE"
        //"EDITION_NAME"
    };
    
    @FXML
    private ComboBox<String> typeCombo;
    @FXML
    private ComboBox<String> nameCombo;        
    @FXML
    private AnchorPane container;
    @FXML
    private VBox headerVBox;
    @FXML
    private VBox dataVBox;
    
    private Connection conn;     
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            conn = OracleConnector.getConnection();
            if (conn == null) return;
            Statement stm = conn.createStatement();
            ResultSet rs = stm.executeQuery(
                "select object_type \n" +
                "from user_objects \n" +
                "where object_type not "+
                "in('"+implode(NOT_INCLUDE_TYPES, "','")+"')\n" +
                "group by object_type"
            );
            fillCombo(rs, typeCombo, "Vyber objekt");            
        }catch (SQLException e) {
            ErrorAlert.show("Nebylo navázáno spojení se serverem.", e);
        }        
        for (int i = 0; i < NAMES.length; i++) {
            headerVBox.getChildren().add(new Label(NAMES[i]));
        }        
    }
    
    @FXML
    public void typeAction(ActionEvent ev) throws SQLException {
        String type = typeCombo.getSelectionModel().getSelectedItem();        
        if (type == null) return;
        PreparedStatement ps = conn.prepareStatement(
            "select object_name from user_objects "
            + "where object_type = ?");
        ps.setString(1, type);        
        fillCombo(ps.executeQuery(), nameCombo, "Vyber název");
    }
    
    @FXML
    public void nameAction(ActionEvent ev) throws SQLException {
        String type = typeCombo.getSelectionModel().getSelectedItem();        
        String name = nameCombo.getSelectionModel().getSelectedItem();        
        PreparedStatement ps = conn.prepareStatement(
            "select "+implode(NAMES, ",")+" from user_objects "
            + "where object_type = ? AND object_name = ?");
        
        ps.setString(1, type);
        ps.setString(2, name);        
        fillInformation(ps.executeQuery());        
    }
    
    
    private void fillCombo(ResultSet rs, ComboBox<String> combo, String label) throws SQLException {        
        List items = combo.getItems();        
        items.clear();
        items.add(label);
        combo.getSelectionModel().selectFirst();
        while (rs.next()) {
            String s = rs.getString(1);
            s = (s == null) ? "" : s;
            items.add(s);
        }
        rs.close();
    }
    
    private String implode(String[] pole, String c) {
        if (pole.length == 0) return "";
        
        StringBuilder sb = new StringBuilder();
        for (String s : pole) {
            sb.append(s).append(c);            
        }
        //System.out.format("%d\n%d\n%d\n%s\n%s", sb.length(), c.length(), sb.length()-c.length()-1, sb, c);
        return sb.substring(0, sb.length()-c.length());
    }
    
    private void fillInformation(ResultSet rs) throws SQLException {
        List<Node> items = dataVBox.getChildren();
        items.clear();
        if (rs.next()) {
            for (int i = 0; i < NAMES.length; i++) {
                String str = rs.getString(i+1);
                if (str == null) str = "(nevyplněno)";
                items.add(new Label(str));
            }
        }
        rs.close();
    }
}
