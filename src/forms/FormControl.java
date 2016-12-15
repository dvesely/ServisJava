/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package forms;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TextField;

/**
 *
 * @author Dominik
 */
public class FormControl {
    
    public static void addLengthLimit(final TextField textField, int limit) {
        textField.textProperty().addListener(
                (ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            if (newValue.length() > limit) {
                textField.setText(oldValue);
            }
        });
    }
    
}
