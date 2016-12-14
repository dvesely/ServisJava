/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package app;

import alerts.ErrorAlert;
import exceptions.ValidException;
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
        stage.setMaximized(true);
        App.setScene("App");
        //stage.show();
        //App.showTable("Personal", "Personal", "select * from v_personal")
        //        .setRowQuery("select * from v_personal where id = ?");
        App.showTable("Klienti", "Klient", "select * from v_klienti")
                .setRowQuery("select * from v_klienti where id = ?");
        //App.showForm("Klient");
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
        System.err.println(e.getMessage());
        if (e instanceof SQLException) {              
            alert.setHeaderText("Chyba databáze");
        }else if (e instanceof ValidException) {
            alert.setHeaderText("Validace formuláře");
        }else {
            e.printStackTrace();
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
