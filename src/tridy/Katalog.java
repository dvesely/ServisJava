/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tridy;

import app.App;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/**
 *
 * @author Dominik
 */
public class Katalog {
    
    private static Stage katalog;            
    
    public static void show() {
        if (katalog == null) {
            katalog = App.createView("Katalog");
            katalog.initOwner(null);
            katalog.initModality(Modality.NONE);                        
        }
        katalog.show();
    }
    
    public static void close() {
        if (katalog == null) {
            return;
        }
        katalog.close();
    }
    
}
