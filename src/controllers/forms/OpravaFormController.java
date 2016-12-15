/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers.forms;

import alerts.ErrorAlert;
import database.DB;
import database.OracleConnector;
import exceptions.NoWindowToClose;
import exceptions.ValidException;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javax.imageio.ImageIO;
import oracle.jdbc.OracleTypes;
import utils.App;
import utils.JSON;
import utils.Validator;

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

    @FXML
    private TextArea popisOpravy;

    @FXML
    private ComboBox<String> typKomponent;

    @FXML
    private TextField cenaZaOpravu;

    private LinkedList<Obrazek> fotky = new LinkedList<>();
    private Integer idOpravy;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        App.setTitle(null);
        typKomponent.getItems().clear();
        naplnComboBox();
        try {
            aktualizujFotky();
        } catch (IOException ex) {
            Logger.getLogger(OpravaFormController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    public void dokoncitOpravuAction(ActionEvent ev) throws SQLException, ValidException, NoWindowToClose {
        Validator valid = new Validator();
        CallableStatement cStmt = DB.prepareCall("pck_opravy.uprav_opravu", 5);
        if (idOpravy == null) {//insert
            cStmt.setNull("p_id", OracleTypes.NUMBER);
        } else {//update
            cStmt.setInt("p_id", idOpravy);
        }

        cStmt.setString("p_popis_opravy", popisOpravy.getText());
        cStmt.setInt("p_cena", valid.toInteger(cenaZaOpravu.getText(), "Cena"));
        cStmt.setInt("p_pocitace_id", 1);
        cStmt.setInt("p_typy_komponent_id", typKomponent.getSelectionModel().getSelectedIndex() + 1);

        valid.validate();

        cStmt.execute();
        JSON.checkStatus(cStmt.getString("p_result"));
        cStmt.close();
        OracleConnector.getConnection().commit();
        App.setComboItem(JSON.getAsInt("id"));
        App.closeActive();

        //vlozeni obrazků do databaze
        for (Obrazek o : fotky) {
            //BufferedImage img = SwingFXUtils.fromFXImage(o.obr.getImage(), null);.
            // InputStream is = new FileInputStream(o.obr.getImage());
        }

    }

    @FXML
    public void nahratFotkuAction(ActionEvent ev) throws IOException {
        FileChooser fch = new FileChooser();
        fch.getExtensionFilters().add(new FileChooser.ExtensionFilter("Pouze obrázky", "*.jpg", "*.bmp", "*.png"));
        File file = fch.showOpenDialog(App.getActive());

        if (file != null) {
            Image image = new Image(file.toURI().toString(), MAX_SIRKA, scrollPane.getHeight(), false, false);
            ImageView imageView = new ImageView(image);
            imageView.setViewport(Rectangle2D.EMPTY);
            String format = file.getName().substring(file.getName().length() - 3);
            int velikost = Math.round(file.length() / 1024); // v Kb
            Date datum = new Date();
            imageView.setOnMouseEntered(prejetiMysi(format, velikost, datum, imageView));

            fotky.add(new Obrazek(format, velikost, datum, imageView));
            aktualizujFotky();
        }
    }

    private EventHandler<MouseEvent> prejetiMysi(String format, int velikost, Date datum, ImageView imageView) {
        return new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                Tooltip tp = new Tooltip("Formát: " + format + "\n"
                        + "Velikost: " + velikost + " Kb\n"
                        + "Datum přidání: " + new SimpleDateFormat("dd.MM.yyyy").format(datum));
                Tooltip.install(imageView.getParent(), tp);
            }
        };
    }

    private void naplnComboBox() {
        try {
            PreparedStatement ps = OracleConnector.getConnection()
                    .prepareStatement("select nazev from typy_komponent");
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                typKomponent.getItems().add(rs.getString(1));
            }
            typKomponent.getSelectionModel().selectFirst();
        } catch (SQLException ex) {
            ErrorAlert.show("Chyba při naplňování combo boxu.", ex);
        }
    }

    private void aktualizujFotky() throws IOException {
        if (idOpravy != null) {
            //oprava jiz existuje....
            try {
                PreparedStatement ps = OracleConnector.getConnection()
                        .prepareStatement("select data, format, velikost, datum_nahrani"
                                + " from v_obrazky where opravy_id = ?");
                ps.setInt(1, idOpravy);

                ResultSet rs = ps.executeQuery();
                Blob blob = OracleConnector.getConnection().createBlob();
                ImageView img;

                while (rs.next()) {                  
                    img = new ImageView(SwingFXUtils.toFXImage(ImageIO.read(rs.getBlob(1).getBinaryStream()), null));
                    fotky.add(new Obrazek(rs.getString(2), rs.getInt(3), rs.getDate(4), img));
                }
            } catch (SQLException ex) {
                ErrorAlert.show("Chyba při aktualizaci fotek.", ex);
            }

        }
        //obrazky ktere nejsou jeste v databazi pridame na konec
        fotkyHBox.getChildren().clear();
        for (Obrazek obr : fotky) {
            fotkyHBox.getChildren().add(obr.obr);
            prejetiMysi(obr.format, obr.velikost, obr.datumNahrani, obr.obr);
        }
    }

    @Override
    public void setData(Map<String, String> data) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        idOpravy = new Integer(data.get("id"));
    }

    private class Obrazek {

        String format;
        int velikost;
        Date datumNahrani;
        ImageView obr;

        public Obrazek(String format, int velikost, Date datumNahrani, ImageView obr) {
            this.format = format;
            this.velikost = velikost;
            this.datumNahrani = datumNahrani;
            this.obr = obr;
        }
    }
}
