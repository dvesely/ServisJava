/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import table.Table;
import app.App;
import database.DB;
import database.Query;
import java.util.List;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.FlowPane;
import privileges.Opravneni;
import privileges.Pozice;
import user.User;

/**
 * FXML Controller class
 *
 * @author Dominik
 */
public class AppController implements Initializable {

    @FXML
    private Label usernameLabel;
    @FXML
    private Label positionLabel;    
    @FXML
    private FlowPane container;    
    @FXML
    private Button zakazkaButton;
    @FXML
    private Button pocitacNeopaveneButton;
    @FXML
    private Button pocitacOpraveneButton;
    @FXML
    private Button pocitacVOpraveButton;
    @FXML
    private Button personalButton;
    @FXML
    private Button klientButton;
    @FXML
    private Button opravaButton;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        App.setTitle(null);
        usernameLabel.setText(String.format("%s (%s)", User.getCeleJmeno(), User.getUsername()));
        positionLabel.setText(User.getPoziceString());
        zobrazTlacitkaPodlePrav();        
    }    
    
    @FXML
    public void logoutAction(ActionEvent e) {        
        App.setScene("Login");
    }
    
    @FXML
    public void openZakazkyAction(ActionEvent e) throws SQLException {        
        String select = "select id, klient, vytvoril vyrizovatel, priblizna_cena, cena_za_opravy,"
            + "datum_prijmuti vytvoreno, datum_priblizne_dokonceni priblizne_dokonceni, "
            + "datum_dokonceni dokonceno, vyrizena, personal_id "
            + "from v_zakazky";        
        
        Table table = App.createTable("Zakázky", "Zakazka", select);        
        //table.setDeleteProcedure("pck_zakazky.smazZakazku");
        table.setInsertable(true);
        if (Opravneni.pristupPouze(Pozice.OBCHODNIK)) {
            table.setOnlyOwnRow(true);
        }
        table.show();
    }
    
    @FXML
    public void pocitacOpraveneButton(ActionEvent e) throws SQLException {
        Table table = App.createTable("Opravené počítače", "Pocitac", 
                "select * from v_pocitace_opravene");         
        if (Opravneni.pristupPouze(Pozice.MANAGER)) {//mazat muze jenom manazer
            table.setDeleteProcedure("pck_pocitace.smaz_pocitac");            
        }
        table.show();
    }  
    
    @FXML
    public void openPocitaceNeopaveneAction(ActionEvent e) throws SQLException {
        Table table = App.createTable("Neopravené počítače", "Pocitac", 
                "select * from v_pocitace_neopravene");        
        if (Opravneni.pristupPouze(Pozice.MANAGER)) {//mazat muze a upravovat jenom manazer
            table.setDeleteProcedure("pck_pocitace.smaz_pocitac");
            table.setUpdateQuery(Query.POCITACE_FORM);    //povoleni upravy    
        }
        table.setMenuItems(true, false);
        table.show();        
    }
    
    @FXML
    public void openPocitaceVOpraveAction(ActionEvent e) throws SQLException {
        String select = "select id,zakazky_id,popis_zavady,konecna_cena,priblizna_cena,technik"
                + " from v_pocitace_v_oprave";
        if (Opravneni.pristupPouze(Pozice.TECHNIK)) {//omezit na vlastni opravy
            select += " where personal_id = "+User.getId();
        }        
        Table table = App.createTable("Počítače v opravě", "Pocitac", 
                select);
        table.setUpdateQuery(Query.POCITACE_FORM);    //povoleni upravy    
        
        if (Opravneni.pristupPouze(Pozice.MANAGER))//mazat muze jenom manazer
            table.setDeleteProcedure("pck_pocitace.smaz_pocitac");
        table.setMenuItems(false, true);
        table.show();        
    }
    
    @FXML
    public void openPersonalAction(ActionEvent e) throws SQLException {
        Table table = App.createTable("Personál", "Personal", "select * from v_personal");
        table.setUpdateQuery(Query.PERSONAL_FORM);
        table.setDeleteProcedure("pck_personal.smaz_personal");
        table.setInsertable(true);
        table.show();
    }
    
    @FXML
    public void openKlientiAction(ActionEvent e) throws SQLException {
        Table table = App.createTable("Klienti", "Klient", "select * from v_klienti");
        table.setUpdateQuery(Query.KLIENT_FORM);
        table.setDeleteProcedure("pck_klienti.smaz_klienta");
        table.show();
    }
    
    @FXML
    public void openOpravyAction(ActionEvent e) throws SQLException {
        Table table = App.createTable("Opravy", "Oprava", 
                "select id,pocitace_id pocitace, typ_komponenty, popis_opravy,"
                    + "cena from v_opravy"
        );        
        table.setUpdateQuery(Query.OPRAVY_FORM);
        table.setDeleteProcedure("pck_opravy.smaz_opravu");
        table.show();
    }
    
    
    private void zobrazTlacitkaPodlePrav() {        
        if (Opravneni.pristupPouze(Pozice.TECHNIK)) {
            //nikdo z techniku nevidi zakazky, klienty, personal
            removeButtons(zakazkaButton, klientButton,personalButton);
            pocitacVOpraveButton.setText("Moje opravy");
        }else if (Opravneni.pristupPouze(Pozice.OBCHODNIK)) {
            //nikdo z obchoniku neuvidi pocitace, opravy a personal
            removeButtons(pocitacNeopaveneButton, pocitacOpraveneButton, pocitacVOpraveButton
                    , opravaButton,personalButton);
        }        
    }    
    
    private void removeButtons(Button... buttons) {
        List<Node> list = container.getChildren();
        for (Button btn : buttons) {
            list.remove(btn);
        }
    }
    
}
