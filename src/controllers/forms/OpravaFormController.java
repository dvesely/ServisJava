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
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.ScrollPane;
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
    
    @FXML
    private HBox fotkyHBox;
    @FXML
    private ScrollPane scrollPane;
    
    private LinkedList<ImageView> noveFotky = new LinkedList<>();
    private Integer idOpravy;
        
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        App.setTitle(null);
        Image image = new Image("file:/C:/Users/domin/Pictures/IMG_20160908_141722.jpg",
                    MAX_SIRKA, scrollPane.getHeight(), false, false);        
        
        ImageView iw = new ImageView(image);        
        noveFotky.add(iw);
        aktualizujFotky();
    }    
    
    @FXML
    public void nahratFotkuAction(ActionEvent ev) {
        FileChooser fch = new FileChooser();
        fch.getExtensionFilters().add(new FileChooser.ExtensionFilter("Pouze obrázky", "*.jpg", "*.bmp", "*.png"));        
        File file = fch.showOpenDialog(App.getActive());
        if (file != null) {            
            Image image = new Image(file.toURI().toString());                
            ImageView imageView = new ImageView(image);
            imageView.setViewport(Rectangle2D.EMPTY);
            noveFotky.add(imageView);
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

    @Override
    public void setData(Map<String, String> data) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        System.out.println("oke");
    }
    
}
