/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package alerts;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

/**
 *
 * @author Dominik
 */
abstract class AbstractAlert extends Alert {    

    public AbstractAlert(AlertType alertType, String title, String message, ButtonType... buttons) {
        super(alertType, message, buttons);
        setTitle(title);               
        getDialogPane().setPrefSize(400, 250);        
    }
    
}
