/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

import database.DB;
import java.sql.SQLException;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 *
 * @author Dominik
 */
public class FormWindow extends Stage {

    private boolean confirm = false;
    
    public FormWindow(Stage parent) {
        super(StageStyle.UTILITY);
        initOwner(parent);
        initModality(Modality.WINDOW_MODAL);        
    }

    public void setConfirm(boolean confirm) {
        this.confirm = confirm;
    }
    
    public boolean confirm() {
        return confirm;
    }

    @Override
    public void showAndWait() {
        super.showAndWait(); //To change body of generated methods, choose Tools | Templates.
        if (confirm) {
            DB.commit();
        }else {
            DB.rollback();
        }
    }
    
    
}
