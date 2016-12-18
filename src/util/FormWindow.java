/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import database.DB;
import database.OracleConnector;
import java.sql.SQLException;
import java.sql.Savepoint;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 *
 * @author Dominik
 */
public class FormWindow extends Stage {

    private static final int WIDTH = 700;
    private static final int HEIGHT = 500;
    
    private Savepoint point;    
    
    public FormWindow(Stage parent) {
        super(StageStyle.UTILITY);
        initOwner(parent);
        initModality(Modality.WINDOW_MODAL);  
        setWidth(WIDTH);
        setHeight(HEIGHT);
        setResizable(false);
        setOnCloseRequest(event -> {
            rollback();
        });
        try {
            point = OracleConnector.getConnection().setSavepoint();
        }catch (SQLException ex) {
            System.out.println("Nebyl ulozen savepoint.");
        }
    }
    
    public void rollback() {
        if (point != null) {            
            DB.rollback(point);            
        }
    }

    @Override
    public void showAndWait() {
        super.showAndWait(); //To change body of generated methods, choose Tools | Templates.
    }
    
    
    
}
