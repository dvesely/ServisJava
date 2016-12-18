/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package app;

import alerts.ErrorAlert;
import controllers.TabulkaController;
import controllers.forms.IFormController;
import exceptions.NoWindowToClose;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import table.Table;
import util.FormWindow;
import util.ItemIdValue;

/**
 *
 * @author Dominik
 */
public class App {

    public static final String TITLE_PREFIX = "Servis počítačů";

    private static Stage primaryStage;
    private static Stage activeStage;
    private static FXMLLoader loader;
    private static ItemIdValue comboItem;
    //private static Stack<Stage> stackForms = new Stack<>();//fronta aktivnich formularu        
    
    
    public static void setPrimaryStage(Stage stage) {        
        primaryStage = stage;
        activeStage = stage;
        addOnFocusEvent(stage);
    }
    
    public static Stage createView(String sceneName) {
        try {
            setLoader("/views/"+sceneName+"View.fxml");                
            Stage stage = new Stage();            
            stage.initOwner(activeStage);
            stage.setScene(new Scene(loader.load()));
            stage.initModality(Modality.APPLICATION_MODAL);
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
    
    public static FormWindow createForm(String formName) {
        return createForm(formName, null);
    }
    
    public static FormWindow createForm(String formName, Map<String, String> data) {
        try {
            setLoader("/forms/"+formName+"Form.fxml");    
            FormWindow form = new FormWindow(activeStage);
            form.setScene(new Scene(loader.load()));
            addOnFocusEvent(form);            
            if (data != null)//inicializace dat pro upravu zaznamu                             
                ((IFormController)getController()).setData(data);        
            return form;
        }catch (IOException ex) {
            ErrorAlert.show("Chyba při načítání controlleru. ("+formName+"FormController)");
        }catch ( Exception ex) {
            ErrorAlert.show("Formulář '"+formName+"' nebyl nalezen.", ex);                        
        }        
        return null;
    }
    
    public static Table createTable(String title, String formName, String query) throws SQLException {
        App.createView("Tabulka").setTitle(App.createTitle(title));                                        
        Table table = new Table((TabulkaController)getController(), title, formName, query);              
        
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
    
    public static FormWindow getActiveForm() {
        if (activeStage instanceof FormWindow) {
            return (FormWindow)activeStage;
        }
        return null;
    }
    
    public static void closeActive() throws NoWindowToClose {
        if (activeStage == null)
            throw new NoWindowToClose("Neexistuje žádné okno k zavření.");        
        activeStage.close();        
    }
    /**
     * Zavre aktualni formular a
     * pokud formular nebyl potvrzenej,
     * tak se zmeny vrati na posledni savepoint
     * @param confirm - parametr, ktery oznacuje zda byl formular potvrzen nebo ne
     * @throws exceptions.NoWindowToClose - pousi se zavrit okno, ktere neni formular
     */
    public static void closeActiveForm(boolean confirm) throws NoWindowToClose {
        FormWindow form = getActiveForm();
        if (form == null) {
            throw new NoWindowToClose("Akivni okno neni formular.");            
        }
        if (!confirm) {//formular nebyl potvrzen, rollback do saveppointu
            form.rollback();
        }
        form.close();
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
    
    public static String createTitle (String title) {
        if (title == null) {
            return TITLE_PREFIX;
        }
        
        return TITLE_PREFIX + " - "+ title;
    }
    
    public static void setTitle(String title) {
        activeStage.setTitle(createTitle(title));
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
    
    private App() {};   
    
}
