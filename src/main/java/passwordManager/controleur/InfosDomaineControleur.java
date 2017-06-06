package passwordManager.controleur;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.stage.FileChooser;
import passwordManager.model.Domaine;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Nico on 05/06/2017.
 */
public class InfosDomaineControleur implements Initializable {
    private AppControleur app;

    @FXML private Label nomLabel;
    @FXML private Label domaineLabel;

    private Domaine toEdit;
    private String iconeLocation;
    private boolean exists;

    @FXML private TextField nom;
    @FXML private TextField domaine;
    @FXML private ImageView icone;
    @FXML private TextArea notes;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        nom.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER)
                okEdition();
        });
        domaine.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER)
                okEdition();
        });
    }

    private void finEdition(boolean ok) {
        if (ok) {
            toEdit.setNom(nom.getText());
            toEdit.setDomaine(domaine.getText());
            toEdit.setIconeLocation(iconeLocation);
            toEdit.setNotes(notes.getText());

            if (!exists) {
                app.donneesActives.addDomaine(toEdit);
            }
        }

        app.finEdition();
    }

    @FXML
    private void annulerEdition() {
        finEdition(false);
    }

    @FXML
    private void okEdition() {
        finEdition(true);
    }

    void bindParent(AppControleur c) {
        app = c;
    }

    void initDomaine(Domaine d) {
        toEdit = d;
        nom.setText(d.getNom());
        nomLabel.setText(d.getNom());
        domaine.setText(d.getDomaine());
        domaineLabel.setText(d.getDomaine());
        iconeLocation = d.getIconeLocation();
        icone.setImage(app.imageManager.getImage(iconeLocation));
        notes.setText(d.getNotes());
        exists = true;

        Platform.runLater(nom::requestFocus);
    }

    @FXML
    private void selectionIcone() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File("."));
        fileChooser.setTitle("Choisir une icone");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Images", "*.png")
        );
        File fileSelected = fileChooser.showOpenDialog(app.passwordManager.stage);
        if (fileSelected != null) {
            Image i = app.imageManager.getImage(fileSelected.getAbsolutePath());
            if (i != null) {
                iconeLocation = fileSelected.getAbsolutePath();
                icone.setImage(i);
            }
        }
    }

    void nouveauDomaine() {
        toEdit = new Domaine("Nouveau domaine");
        initDomaine(toEdit);
        exists = false;
    }
}
