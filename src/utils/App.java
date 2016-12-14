/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

import alerts.ErrorAlert;
import controllers.forms.IFormController;
import controllers.TabulkaController;
import database.OracleConnector;
import exceptions.NoWindowToClose;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;
import table.Table;

/**
 *
 * @author Dominik
 */
public class App {

    public static final String TITLE_PREFIX = "Servis počítačů";
    
    private static int userId;
    private static Stage primaryStage;
    private static Stage activeStage;
    private static FXMLLoader loader;
    private static ItemIdValue comboItem;
    //private static Stack<Stage> stackForms = new Stack<>();//fronta aktivnich formularu        
    
    
    public static void setPrimaryStage(Stage stage) {
        primaryStage = stage;
        activeStage = stage;
    }
    
    public static Stage createView(String sceneName) {
        return createView(sceneName, true);
    }
    
    public static Stage createView(String viewName, boolean setOwner) {
        try {
            setLoader("/views/"+viewName+"View.fxml");                
            Stage stage = new Stage();            
            if (setOwner) stage.initOwner(activeStage);
            stage.setScene(new Scene(loader.load()));
            stage.setOnCloseRequest(onWindowEventRollback());
            addOnFocusEvent(stage);
            return stage;
        }catch (IOException e){};
        return null;
    }
    
    public static void showForm(String formName) {
        createForm(formName).show();
    }
    
    public static void showForm(String formName, Map<String, String> data) {
        createForm(formName, data).show();
    }
    
    public static Stage createForm(String formName) {
        return createForm(formName, null);
    }
    
    public static Stage createForm(String formName, Map<String, String> data) {
        try {
            setLoader("/forms/"+formName+"Form.fxml");    
            Stage stage = new Stage(StageStyle.UTILITY);
            stage.setScene(new Scene(loader.load()));
            stage.initOwner(activeStage);
            stage.initModality(Modality.WINDOW_MODAL);
            stage.setOnCloseRequest(onWindowEventRollback());
            addOnFocusEvent(stage);            
            if (data != null)//inicializace dat pro upravu zaznamu                             
                ((IFormController)getController()).setData(data);
            
            //stackForms.push(stage);            
            return stage;
        }catch ( Exception ex) {
            new ErrorAlert("Formulář '"+formName+"' nebyl nalezen.").showAndWait();                             
            ex.printStackTrace();
            return null;
        }
    }
    
    public static Table showTable(String title, String formName, String query) throws SQLException {
        Stage stage = App.createView("Tabulka", false);                                
        Table table = ((TabulkaController)getController()).initTable(title, formName, query); 
        stage.show();        
        App.setTitle(title);
        return table;
    }
    
    public static ItemIdValue removeComboItem() {
        ItemIdValue tmp = comboItem;
        comboItem = null;
        return tmp;
    }
    
    public static void setComboItem(int itemId) {
        comboItem = new ItemIdValue(String.valueOf(itemId));
    }
    
    public static Stage getActive() {
        return activeStage;
    }
    
    public static void closeActive() throws NoWindowToClose {
        if (activeStage == null)
            throw new NoWindowToClose("Neexistuje žádné okno k zavření.");        
        activeStage.close();
        rollback();        
    }
    
    public static Scene getScene() {
        return activeStage.getScene();
    }
    
    public static void setScene(String sceneName) {                
        try {
            setLoader("/views/"+sceneName+"View.fxml");            
            Parent root = loader.load();            
            //nastaveni sceny
            if (activeStage.getScene() == null) {//scena neexistuje
                activeStage.setScene(new Scene(root));//vytvoreni nove sceny
            }else {
                activeStage.getScene().setRoot(root);//pouze zmena sceny
            }                        
        }catch (IOException ex) {            
            ex.printStackTrace();
            new ErrorAlert("Scéna '"+sceneName+"' nebyla nalezena.").showAndWait();                             
            return;
        }        
    };
    
    public static <T> T getController() {
        if (loader == null) throw new NullPointerException("Scena nebyla nastavena.");
        return loader.getController();
    }
    
    public static void setTitle(String title) {
        if (title == null) {
            activeStage.setTitle(TITLE_PREFIX);
        }
        else {
            activeStage.setTitle(TITLE_PREFIX + " - "+ title);
        }
        
    }
    
    public static int getUserId() {
        return userId;
    }
    
    public static void setUserId(int userId) {
        App.userId = userId;
    }
    
    private static EventHandler<WindowEvent> onWindowEventRollback() {
        return (WindowEvent event) -> {
            rollback();
        };
    }
    
    private static void setLoader(String relativePath) {
        loader = new FXMLLoader(App.class.getResource(relativePath));    
    }
    
    private static void addOnFocusEvent(Stage stage) {
        stage.focusedProperty().addListener(
            (ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {                    
                Stage focused = (Stage)((ReadOnlyBooleanProperty)observable).getBean();
                App.activeStage = focused;
        });
    }
    /**
     * Pokusi se o rollback transakce, pokud se nepodari, 
     * vypise do konzole chybovou hlasku.
     */
    private static void rollback() {
        try {
            OracleConnector.getConnection().rollback();
        }catch (SQLException ex) {
            System.out.println("Rollback se nepodarilo provest.\n"+ex.getMessage());
        };    
    }
    
    private App() {};   
    
}
