package passwordManager.controleur;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.stage.FileChooser;
import org.controlsfx.validation.ValidationResult;
import org.controlsfx.validation.ValidationSupport;
import org.controlsfx.validation.Validator;
import passwordManager.ImageManager;
import passwordManager.model.Domaine;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Nico on 05/06/2017.
 */
public class InfosDomaine implements Initializable {
    private App app;

    @FXML private Label nomLabel;
    @FXML private Label domaineLabel;

    private Domaine toEdit;
    private String iconeLocation;
    private boolean exists;

    @FXML private Button bOk;

    @FXML private Button bGenD;
    @FXML private Button bAccD;
    @FXML private Button bModI;
    @FXML private Button bSupI;
    @FXML private Button bCopN;
    @FXML private Button bSupN;

    @FXML private TextField tfNom;
    @FXML private TextField tfDomaine;
    @FXML private ImageView ivIcone;
    @FXML private TextArea taNotes;

    private ValidationSupport validationSupport = new ValidationSupport();
    private Validator<String> validator = (control, s) -> {
        boolean condition = ((TextField)control).getText().length() < 6
                || ((TextField)control).getText().length() > 18;
        return ValidationResult.fromErrorIf(control, "control < 6 ou > 18", condition);
    };

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        bGenD.setText(null);
        bAccD.setText(null);
        bModI.setText(null);
        bSupI.setText(null);
        bCopN.setText(null);
        bSupN.setText(null);

        tfNom.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER)
                okEdition();
        });
        tfDomaine.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER)
                okEdition();
        });
        validationSupport.registerValidator(tfNom, true, validator);
        bOk.disableProperty().bind(validationSupport.invalidProperty());
    }

    private void finEdition(boolean ok) {
        if (ok) {
            toEdit.setNom(tfNom.getText());
            toEdit.setDomaine(tfDomaine.getText());
            toEdit.setIconeLocation(iconeLocation);
            toEdit.setNotes(taNotes.getText());

            if (!exists) {
                app.donneesActives.addDomaine(toEdit);
                app.selectionDomaine(toEdit);
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

    void bindParent(App c) {
        app = c;

        bGenD.setGraphic(app.imageManager.constructImageViewFrom(ImageManager.ICONE_REFRESH, 24, 24, true));
        bAccD.setGraphic(app.imageManager.constructImageViewFrom(ImageManager.ICONE_SHARE, 24, 24, true));
        bModI.setGraphic(app.imageManager.constructImageViewFrom(ImageManager.ICONE_EDITION, 24, 24, true));
        bSupI.setGraphic(app.imageManager.constructImageViewFrom(ImageManager.ICONE_REMOVE, 24, 24, true));
        bCopN.setGraphic(app.imageManager.constructImageViewFrom(ImageManager.ICONE_COPY, 24, 24, true));
        bSupN.setGraphic(app.imageManager.constructImageViewFrom(ImageManager.ICONE_REMOVE, 24, 24, true));
    }

    void initDomaine(Domaine d) {
        toEdit = d;
        tfNom.setText(d.getNom());
        nomLabel.setText(d.getNom());
        tfDomaine.setText(d.getDomaine());
        domaineLabel.setText(d.getDomaine());
        iconeLocation = d.getIconeLocation();
        ivIcone.setImage(app.imageManager.getImage(iconeLocation));
        taNotes.setText(d.getNotes());
        exists = true;

        Platform.runLater(() -> {
            tfNom.requestFocus();
            tfNom.selectAll();
        });
    }

    @FXML
    private void genD() {
        tfDomaine.setText("www." + tfNom.getText().toLowerCase().replace(" ", "") + ".com");
    }

    @FXML
    private void accD() {
        app.passwordManager.getHostServices().showDocument("http://" + tfDomaine.getText());
    }

    @FXML
    private void modI() {
        selectionIcone();
    }

    @FXML
    private void supI() {
        iconeLocation = "";
        ivIcone.setImage(null);
    }

    @FXML
    private void copN() {
        app.passwordManager.clipboardPut(taNotes.getText());
    }

    @FXML
    private void supN() {
        taNotes.setText("");
    }

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
                ivIcone.setImage(i);
            }
        }
    }

    void nouveauDomaine() {
        initDomaine(new Domaine("Nouveau domaine"));
        exists = false;
    }
}
