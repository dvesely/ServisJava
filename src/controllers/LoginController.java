/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import database.OracleConnector;
import exceptions.NotFoundException;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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
import utils.App;

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
    }

    private void onLogin(int userId) {
        App.setScene("Table");
        App.setTitle("Tabulka");
        App.setUserId(userId);
    }

    class LoginThread extends Thread {
        
        public LoginThread() {            
            super(new Task<Integer>() {
                @Override
                protected Integer call() throws Exception {
                    String username = usernameTF.getText();
                    String password = passwordTF.getText();
                    Integer userID = null;

                    if (username == null || password == null) {
                        throw new NotFoundException("Musíte zadat uživatelské jméno a heslo.");
                    }

                    loginState(true);
                    PreparedStatement ps = OracleConnector.getConnection()
                            .prepareStatement("select usr_id from users where usr_name = ? and usr_pwd = md5hash(?)");
                    ps.setString(1, usernameTF.getText());
                    ps.setString(2, passwordTF.getText());
                    ResultSet rs = ps.executeQuery();
                    if (rs.next()) {
                        userID = rs.getObject(1, Integer.class);
                    }
                    ps.close();
                    loginState(false);
                    if (userID == null) {
                        throw new NotFoundException("Zadali jste nesprávné údaje.");
                    }

                    return userID;
                }

                @Override
                protected void failed() {
                    super.failed(); //To change body of generated methods, choose Tools | Templates.
                    setErrorMessage(exceptionProperty().get().getMessage());
                }

                @Override
                protected void succeeded() {
                    super.succeeded(); //To change body of generated methods, choose Tools | Templates.                    
                    App.setScene("App");
                    try {
                        App.setUserId(get());
                    }catch (ExecutionException | InterruptedException e){};                    
                }                
                
            });
            setDaemon(true);             
        }

    }

}
