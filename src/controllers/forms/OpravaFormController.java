/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers.forms;

import alerts.ErrorAlert;
import database.OracleConnector;
import java.io.File;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.BoundingBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import utils.App;

/**
 * FXML Controller class
 *
 * @author Dominik
 */
public class OpravaFormController implements Initializable, IFormController {

    private static final int MAX_SIRKA = 300;   
    private static final int MAX_VYSKA = 500;
    
    @FXML
    private TextArea popisZavadyTA;
    @FXML
    private HBox fotkyHBox;
    @FXML
    private ScrollPane scrollPane;
    
    private LinkedList<ImageView> noveFotky = new LinkedList<>();
    private Integer idOpravy;
    private double vyskaScrollPanelu;
        
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        App.setTitle(null);        
        scrollPane.viewportBoundsProperty().addListener((ObservableValue<? extends Object> observable, Object oldValue, Object newValue) -> {
            BoundingBox bounds = (BoundingBox)newValue;
            vyskaScrollPanelu = bounds.getHeight();
            zmenMaximalniVyskuFotek();
        });        
        aktualizujFotky();
    }    
    
    @Override
    public void setData(Map<String, String> data) {
        popisZavadyTA.setText(data.get("popis_zavady"));
    }
    
    @FXML
    public void nahratFotkuAction(ActionEvent ev) {
        FileChooser fch = new FileChooser();
        fch.getExtensionFilters().add(new FileChooser.ExtensionFilter("Pouze obrázky", "*.jpg", "*.bmp", "*.png"));        
        fch.setInitialDirectory(new File("d:\\Obrázky\\wallpapers\\"));
        File file = fch.showOpenDialog(App.getActive());
        if (file != null) {            
            Image image = new Image(file.toURI().toString());                
            pridejObrazek(image);
            aktualizujFotky();
        }
    }
    
    private void aktualizujFotky() {
        if (idOpravy != null) {
            //oprava jiz existuje....
            try {
                PreparedStatement ps = OracleConnector.getConnection()
                        .prepareStatement("select nazev, data from v_obrazky where opravy_id = ?");
                ps.setInt(1, 0);
            }catch (SQLException ex) {
                ErrorAlert.show("Chyba při aktualizaci fotek.", ex);            
            }
            
        }
        //obrazky ktere nejsou jeste v databazi pridame na konec
        fotkyHBox.getChildren().clear();
        for (ImageView obrazek : noveFotky) {            
            fotkyHBox.getChildren().add((obrazek));
        }
    }
    
    private void zmenMaximalniVyskuFotek() {
        for (ImageView fotka : noveFotky) {                        
            fotka.setFitHeight(vyskaScrollPanelu);            
        }
    }

    private void pridejObrazek(Image image) {
        ImageView iw = new ImageView(image);
        iw.setPreserveRatio(true);
        iw.setFitHeight(vyskaScrollPanelu < MAX_VYSKA ? vyskaScrollPanelu : MAX_SIRKA);
        noveFotky.add(iw);
    }    
}
