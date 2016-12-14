/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

import exceptions.ValidException;
import java.util.LinkedList;
import javafx.scene.control.ComboBox;

/**
 *
 * @author Dominik
 */
public class Validator {
    
    LinkedList<String> errorMessages;
    
    public Validator() {errorMessages = new LinkedList<>();}
    
    public void addMessage(String message) {
        errorMessages.add(message);
    }
    
    public int toInteger(String string, String message) {
        return toInteger(string, message, false);
    }
    
    public int toInteger(String string, String message, boolean fullMessage) {
        try {
            return Integer.parseInt(string);
        }catch (NumberFormatException e) {            
            addMessage(message + (fullMessage ? " může obsahovat pouze čísla." : ""));
            return -1;
        }
    }
    
    public Validator fixLength(String str, int length, String name) {
        if (str.length() == length) {
            addMessage(name + " musí mít přesně %d znaků.", length);
        }
        return this;
    }
    
    public Validator length(String str, int min, int max, String name) {
        if (str.length() < min || str.length() > max)
            addMessage(name + " musí obsahovat %d-%d znaků.", min, max);
        return this;
    }
    
    public int comboBoxToInteger(ComboBox<ItemIdValue> combo) {
        ItemIdValue item = combo.getSelectionModel().getSelectedItem();
        if (item != null) {            
            return item.getId();
        }
        return -1;
    }
    
    public void validate() throws ValidException {        
        if (errorMessages.isEmpty()) {
            return;
        }
        StringBuilder sb = new StringBuilder();
        for (String str : errorMessages) {
            sb.append(str).append('\n');
        }        
        throw new ValidException(sb.toString());
    }
    
    private void addMessage(String message, Object... parametrs) {
        errorMessages.add(String.format(message, parametrs));
    }
    
}
