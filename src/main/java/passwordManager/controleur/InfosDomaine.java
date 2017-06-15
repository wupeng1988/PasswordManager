package passwordManager.controleur;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.StackPane;
import org.controlsfx.control.textfield.TextFields;
import org.controlsfx.validation.ValidationResult;
import org.controlsfx.validation.ValidationSupport;
import passwordManager.ImageManager;
import passwordManager.PasswordManager;
import passwordManager.model.ActionHistorique;
import passwordManager.model.Domaine;
import passwordManager.model.Historique;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.ResourceBundle;

/**
 * Nico on 05/06/2017.
 */
public class InfosDomaine implements Initializable {
    App app;

    @FXML private Label nomLabel;
    @FXML private Label domaineLabel;

    private Domaine toEdit;
    private String iconeLocation;
    private boolean exists;

    private ImageSelection imageSelectionControleur;
    private Parent imageSelection;

    @FXML private StackPane root;

    @FXML private Button bOk;

    @FXML private Button bGenD;
    @FXML private Button bAccD;
    @FXML private Button bModI;
    @FXML private Button bSupI;
    @FXML private Button bCopN;
    @FXML private Button bSupN;

    @FXML private TextField tfNom;
    @FXML private TextField tfDomaine;
    @FXML private TextField tfCategorie;
    @FXML private ImageView ivIcone;
    @FXML private TextArea taNotes;

    private ValidationSupport validationSupport = new ValidationSupport();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        bGenD.setText(null);
        bAccD.setText(null);
        bModI.setText(null);
        bSupI.setText(null);
        bCopN.setText(null);
        bSupN.setText(null);

        bGenD.setTooltip(new Tooltip("Générer le nom de domaine le plus proche"));
        bAccD.setTooltip(new Tooltip("Accéder à l'url (navigateur par défaut)"));
        bModI.setTooltip(new Tooltip("Modifier l'icone associée"));
        bSupI.setTooltip(new Tooltip("Supprimer l'icone associée"));
        bCopN.setTooltip(new Tooltip("Copier le texte dans le presse-papier"));
        bSupN.setTooltip(new Tooltip("Supprimer les notes contenues"));

        initImageSelection();

        tfNom.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER)
                okEdition();
        });
        tfDomaine.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER)
                okEdition();
        });
        tfCategorie.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER)
                okEdition();
        });

        validationSupport.registerValidator(
                tfNom,
                true,
                (control, s) -> ValidationResult.fromErrorIf(control, "error", ((String)s).length() < 2 || ((String)s).length() > 18));
        bOk.disableProperty().bind(validationSupport.invalidProperty());
    }

    private void initImageSelection() {
        FXMLLoader l = new FXMLLoader(getClass().getResource(PasswordManager.FXML_IMAGESELECTION));

        try {
            imageSelection = l.load();
            imageSelectionControleur = l.getController();
        } catch (Exception ignored) {}
    }

    private void montrerImageSelection() {
        root.getChildren().add(imageSelection);
        imageSelectionControleur.setApp(this, iconeLocation);
    }

    void cacherImageSelection(String nouvelIcone) {
        root.getChildren().remove(imageSelection);

        if (!nouvelIcone.equals("")) {
            iconeLocation = nouvelIcone;
            ivIcone.setImage(app.getImageManager().getImage(iconeLocation));
        }
    }

    private void finEdition(boolean ok) {
        if (ok) {
            Historique h = app.getDonneesActives().getHistorique();
            Domaine avant = toEdit.snap();

            toEdit.setNom(tfNom.getText());
            toEdit.setDomaine(tfDomaine.getText());
            toEdit.setCategorie(tfCategorie.getText());
            toEdit.setIconeLocation(iconeLocation);
            toEdit.setNotes(taNotes.getText());

            if (!exists) {
                h.ajoutAction(ActionHistorique.ajoutDomaine(-1, app.getDonneesActives(), toEdit));
                app.selectionDomaine(toEdit);
            } else {
                h.ajoutAction(ActionHistorique.modificationDomaine(avant, toEdit));
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

        ImageManager im = app.getImageManager();

        bGenD.setGraphic(im.constructImageViewFrom(ImageManager.ICONE_REFRESH, 16, 16, true));
        bAccD.setGraphic(im.constructImageViewFrom(ImageManager.ICONE_SHARE, 16, 16, true));
        bModI.setGraphic(im.constructImageViewFrom(ImageManager.ICONE_EDITION, 16, 16, true));
        bSupI.setGraphic(im.constructImageViewFrom(ImageManager.ICONE_REMOVE, 16, 16, true));
        bCopN.setGraphic(im.constructImageViewFrom(ImageManager.ICONE_COPY, 16, 16, true));
        bSupN.setGraphic(im.constructImageViewFrom(ImageManager.ICONE_REMOVE, 16, 16, true));
    }

    void initDomaine(Domaine d) {
        toEdit = d;
        tfNom.setText(d.getNom());
        nomLabel.setText(d.getNom());
        tfDomaine.setText(d.getDomaine());
        tfCategorie.setText(d.getCategorie());
        domaineLabel.setText(d.getDomaine());
        iconeLocation = d.getIconeLocation();
        ivIcone.setImage(app.getImageManager().getImage(iconeLocation));
        taNotes.setText(d.getNotes());
        exists = true;

        ArrayList<String> completion = new ArrayList<>(
                Arrays.asList(
                        "Réseaux Sociaux",
                        "News",
                        "High Tech",
                        "Achats",
                        "Divertissement",
                        "Sport"
                )
        );
        for (Domaine dm : app.getDonneesActives().getDomaines())
            if (!completion.contains(dm.getCategorie())) completion.add(dm.getCategorie());
        TextFields.bindAutoCompletion(tfCategorie, completion);

        Platform.runLater(() -> {
            tfNom.requestFocus();
            tfNom.selectAll();
        });
    }

    @FXML
    private void genD() {
        String gen = tfNom.getText()
                .toLowerCase()
                .trim()
                .replace(" ", "")
                .replace("'", "");

        tfDomaine.setText("www." + gen + ".com");
    }

    @FXML
    private void accD() {
        app.getPasswordManager().getHostServices().showDocument("http://" + tfDomaine.getText());
    }

    @FXML
    private void modI() {
        montrerImageSelection();
    }

    @FXML
    private void supI() {
        iconeLocation = "";
        ivIcone.setImage(null);
    }

    @FXML
    private void copN() {
        app.getPasswordManager().clipboardPut(taNotes.getText());
    }

    @FXML
    private void supN() {
        taNotes.setText("");
    }

    void nouveauDomaine() {
        initDomaine(new Domaine("Nouveau domaine"));
        exists = false;
    }
}
