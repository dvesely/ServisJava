/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package app;

import alerts.ErrorAlert;
import static app.App.setPrimaryStage;
import exceptions.ValidException;
import java.sql.SQLException;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import privileges.Pozice;
import user.User;

/**
 *
 * @author Dominik
 */
public class Main extends Application {
    
    @Override
    public void start(Stage stage) throws Exception {        
        
        Thread.setDefaultUncaughtExceptionHandler(Main::showError);        
        App.setPrimaryStage(stage);             
        //App.setScene("Katalog");
        App.setScene("Login");
        stage.show();
        //App.showForm("Zakazka");
        /*
        Table table = App.createTable("Personál", "Personal", "select * from v_personal");
        table.setRowQuery(Query.PERSONAL_FORM);
        table.setDeleteProcedure("pck_personal.smaz_personal");
        */
        /*
       Table table = App.createTable("Počítače", "Pocitac", "select * from v_pocitace");
        table.setRowQuery(Query.POCITACE_FORM);
        table.setDeleteProcedure("pck_pocitace.smaz_pocitac");*/
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
            e.printStackTrace();
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
