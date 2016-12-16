/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import database.DB;
import exceptions.NotFoundException;
import java.net.URL;
import java.sql.CallableStatement;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutionException;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import oracle.jdbc.OracleTypes;
import app.App;
import privileges.Pozice;
import user.User;
import util.JSON;

/**
 *
 * @author Dominik
 */
public class LoginController implements Initializable {

    public static int pocet = 0;

    @FXML
    private TextField usernameTF;
    @FXML
    private TextField passwordTF;
    @FXML
    private Button loginButton;
    @FXML
    private Button interruptButton;
    @FXML
    private ProgressIndicator spinner;
    @FXML
    private Label errorLabel;

    private Thread thread = null;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        loginState(false);
        App.setTitle("Přihlášení");
        usernameTF.setText("sobotalu");
        passwordTF.setText("1234");
    }

    @FXML
    public void loginAction(ActionEvent event) {        
        thread = new LoginThread();
        thread.start();            
        loginState(true);
    }

    @FXML
    public void interruptLoginAction(ActionEvent e) {                                        
        
        //loginState(false);        
    }

    private void loginState(boolean bool) {
        errorLabel.setVisible(!bool);
        spinner.setVisible(bool);
        loginButton.setDisable(bool);
        //interruptButton.setVisible(bool);
        usernameTF.setDisable(bool);
        passwordTF.setDisable(bool);
    }

    private void setErrorMessage(String message) {
        errorLabel.setText(message);
        loginState(false);
    }

    private void onLogin(UserInfo userInfo) {
        User.set(userInfo.id, userInfo.pozice);
        App.setScene("App");        
        App.setTitle("Tabulka");           
    }

    class LoginThread extends Thread {
        
        public LoginThread() {            
            super(new Task<UserInfo>() {
                @Override
                protected UserInfo call() throws Exception {
                    String username = usernameTF.getText();
                    String password = passwordTF.getText();                    

                    if (username == null || password == null) {
                        throw new NotFoundException("Musíte zadat uživatelské jméno a heslo.");
                    }

                    loginState(true);
                    CallableStatement cStmt = DB.prepareCall("pck_personal.prihlas_personal", 3);
                    cStmt.setString(1, usernameTF.getText());
                    cStmt.setString(2, passwordTF.getText());                    
                    cStmt.registerOutParameter(3, OracleTypes.CLOB);
                    cStmt.execute();
                    JSON.checkStatus(cStmt.getString(3));                    
                    
                    return new UserInfo(JSON.getAsInt("id"), JSON.getAsInt("pozice_id"));
                }

                @Override
                protected void failed() {
                    super.failed(); //To change body of generated methods, choose Tools | Templates.
                    setErrorMessage(exceptionProperty().get().getMessage());
                }

                @Override
                protected void succeeded() {
                    super.succeeded(); //To change body of generated methods, choose Tools | Templates.                    
                    try {
                        onLogin(get());
                    }catch (InterruptedException | ExecutionException e){};                    
                }                
                
            });
            setDaemon(true);             
        }
    }
    
    class UserInfo {
        
        private final int id;
        private final Pozice pozice;

        public UserInfo(int id, int pozice_id) throws NotFoundException {
            this.id = id;
            pozice = Pozice.valueOf(pozice_id);
        }
    }
}
