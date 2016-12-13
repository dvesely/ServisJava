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
public class ErrorAlert extends AbstractAlert {
    
    public ErrorAlert(String message) {
        super(Alert.AlertType.ERROR, "Chybová hláška", message, ButtonType.CLOSE);
    }    
}
