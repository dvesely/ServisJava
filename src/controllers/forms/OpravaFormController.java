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
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
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
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javax.imageio.ImageIO;
import javax.imageio.stream.ImageOutputStream;
import oracle.jdbc.OracleTypes;
import utils.App;
import utils.ItemIdValue;
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
    private ComboBox<ItemIdValue> typKomponent;

    @FXML
    private TextField cenaZaOpravu;

    private LinkedList<Obrazek> fotky = new LinkedList<>();
    private Integer idOpravy;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        App.setTitle(null);
        typKomponent.getItems().clear();
        naplnComboBox();
    }

    @FXML
    public void dokoncitOpravuAction(ActionEvent ev) throws SQLException, ValidException, NoWindowToClose, IOException {
        Validator valid = new Validator();
        CallableStatement cStmt = DB.prepareCall("pck_opravy.uprav_opravu", 6);
        cStmt.registerOutParameter("p_result", OracleTypes.CLOB);
        cStmt.registerOutParameter("p_id", OracleTypes.NUMBER);
        if (idOpravy == null) {//insert
            cStmt.setNull("p_id", OracleTypes.NUMBER);
        } else {//update
            cStmt.setInt("p_id", idOpravy);
        }

        cStmt.setString("p_popis_opravy", popisOpravy.getText());
        cStmt.setInt("p_cena", valid.toInteger(cenaZaOpravu.getText(), "Cena"));
        cStmt.setInt("p_pocitace_id", 1);
        cStmt.setInt("p_typy_komponent_id", typKomponent.getSelectionModel().getSelectedItem().getId());

        valid.validate();

        cStmt.execute();
        JSON.checkStatus(cStmt.getString("p_result"));
        int opravaId = cStmt.getInt("p_id");
        cStmt.close();

        //vlozeni obrazků do databaze
        CallableStatement cs;
        for (Obrazek o : fotky) {
            if (o.neniZDatabaze == true) { // fotky ktere se maji uploadnout
                cs = DB.prepareCall("pck_opravy.uprav_obrazek", 5);
                cs.registerOutParameter("p_result", OracleTypes.CLOB);
                BufferedImage bImage = SwingFXUtils.fromFXImage(o.obr.getImage(), null);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ImageIO.write(bImage, o.format, baos);
                baos.close();
                ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());

                cs.setBlob("p_data", bais);
                cs.setInt("p_oprava_id", opravaId);
                cs.setString("p_format", o.format);
                cs.setInt("p_velikost", o.velikost);
                cs.execute();
                JSON.checkStatus(cs.getString("p_result"));
                cs.close();
            } else if (o.smazano == true) { // fotky ktere se maji smazat z db
                cs = DB.prepareCall("pck_opravy.smaz_obrazek", 2);
                cs.registerOutParameter("p_result", OracleTypes.CLOB);
                cs.setInt("p_id", o.id);
                cs.execute();
                JSON.checkStatus(cs.getString("p_result"));
                cs.close();
            }
        }
        DB.commit();
        App.closeActive();
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
            Obrazek o = new Obrazek(format, velikost, datum, imageView);
            imageView.setOnMouseEntered(prejetiMysi(o));
            imageView.setOnMouseClicked(praveKlikMysi(o));

            fotky.add(o);
            aktualizujFotky();
        }
    }

    private EventHandler<MouseEvent> prejetiMysi(Obrazek o) {
        return new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                Tooltip tp = new Tooltip("Formát: " + o.format + "\n"
                        + "Velikost: " + o.velikost + " Kb\n"
                        + "Datum přidání: " + new SimpleDateFormat("dd.MM.yyyy").format(o.datumNahrani));
                Tooltip.install(o.obr.getParent(), tp);
            }
        };
    }

    private EventHandler<MouseEvent> praveKlikMysi(Obrazek o) {
        return new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (event.getButton() == MouseButton.SECONDARY) {
                    ContextMenu cmenu = new ContextMenu();
                    MenuItem deleteImg = new MenuItem("Vymaž obrázek");
                    deleteImg.setOnAction(new EventHandler<ActionEvent>() {
                        @Override
                        public void handle(ActionEvent event) {

                            if (o.neniZDatabaze == true) {
                                fotky.remove(o);
                            } else {
                                o.smazano = true;
                            }
                            fotkyHBox.getChildren().remove(o.obr);
                        }
                    });
                    cmenu.getItems().add(deleteImg);
                    cmenu.show(o.obr, event.getScreenX(), event.getScreenY());
                }
            }
        };
    }

    private void naplnComboBox() {
        try {
            PreparedStatement ps = OracleConnector.getConnection()
                    .prepareStatement("select id, nazev from typy_komponent order by id");
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                typKomponent.getItems().add(new ItemIdValue(rs.getInt(1), rs.getString(2)));
            }
            typKomponent.getSelectionModel().selectFirst();
        } catch (SQLException ex) {
            ErrorAlert.show("Chyba při naplňování combo boxu.", ex);
        }
    }

    private void aktualizujFotky() throws IOException {
        fotkyHBox.getChildren().clear();
        for (Obrazek obr : fotky) {
            if (obr.smazano != true) {
                fotkyHBox.getChildren().add(obr.obr);
            }
        }
    }

    private void nactiFotkyZDatabaze() throws IOException, SQLException {
        if (idOpravy != null) {
            //oprava jiz existuje....
            PreparedStatement ps = OracleConnector.getConnection()
                    .prepareStatement("select id, data, format, velikost, datum_nahrani"
                            + " from obrazky where opravy_id = ?");
            ps.setInt(1, idOpravy);

            ResultSet rs = ps.executeQuery();
            ImageView img;

            while (rs.next()) {
                img = new ImageView(SwingFXUtils.toFXImage(ImageIO.read(rs.getBlob(2).getBinaryStream()), null));
                Obrazek o = new Obrazek(rs.getInt(1), rs.getString(3), rs.getInt(4), rs.getDate(5), img, false);
                img.setOnMouseEntered(prejetiMysi(o));
                img.setOnMouseClicked(praveKlikMysi(o));
                fotky.add(o);
            }

        }
    }

    @Override
    public void setData(Map<String, String> data) {
        idOpravy = new Integer(data.get("id"));
        popisOpravy.setText(data.get("popis_opravy"));
        cenaZaOpravu.setText(data.get("cena"));
        typKomponent.getSelectionModel().select(new ItemIdValue(data.get("typy_komponent_id")));
        try {
            nactiFotkyZDatabaze();
            aktualizujFotky();
        } catch (IOException | SQLException ex) {
            System.out.println(ex);
            ErrorAlert.show("Fotky se nepovedlo načíst z databáze.");
        }
    }

    private class Obrazek {

        String format;
        int velikost;
        Date datumNahrani;
        ImageView obr;
        boolean neniZDatabaze = true;
        boolean smazano = false;
        int id = 0;

        public Obrazek(String format, int velikost, Date datumNahrani, ImageView obr) {
            this.format = format;
            this.velikost = velikost;
            this.datumNahrani = datumNahrani;
            this.obr = obr;
        }

        public Obrazek(int id, String format, int velikost, Date datumNahrani, ImageView obr, boolean bol) {
            this.id = id;
            this.format = format;
            this.velikost = velikost;
            this.datumNahrani = datumNahrani;
            this.obr = obr;
            this.neniZDatabaze = bol;
        }
    }
}
