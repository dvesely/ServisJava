/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package app;

import alerts.ErrorAlert;
import java.sql.SQLException;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import utils.App;

/**
 *
 * @author Dominik
 */
public class Main extends Application {
    
    @Override
    public void start(Stage stage) throws Exception {        
        
        Thread.setDefaultUncaughtExceptionHandler(Main::showError);
        
        App.setPrimaryStage(stage);
        //App.setScene("Table");
        //stage.show();
        //App.openTable("asd", "Order", "select * from v_zakazky");
        App.createForm("Client").showAndWait();
    }

        private static void showError(Thread t, Throwable e) {
        System.err.println("***Default exception handler***");
        if (Platform.isFxApplicationThread()) {
            while (e.getCause() != null) {
                e = e.getCause();
            }
            showErrorDialog(e);
        } else {
            System.err.println("An unexpected error occurred in "+t);

        }
    }

    private static void showErrorDialog(Throwable e) {
        ErrorAlert alert = new ErrorAlert(e.getMessage());
        if (e instanceof SQLException) {              
            alert.setHeaderText("Chyba databáze");
        }else {
            alert.setContentText(e.toString()+"\n"+alert.getContentText());
        }
        alert.show();
    }
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {        
        launch(args);        
    }
    
}
