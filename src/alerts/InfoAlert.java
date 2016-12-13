/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package alerts;

import javafx.scene.control.ButtonType;

/**
 *
 * @author Dominik
 */
public class InfoAlert extends AbstractAlert {

    public InfoAlert(String message) {
        super(AlertType.INFORMATION, "Informace", message, ButtonType.CLOSE);
    }    
    
}
