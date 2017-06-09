package passwordManager.controleur;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.DirectoryChooser;
import org.controlsfx.validation.ValidationResult;
import org.controlsfx.validation.ValidationSupport;
import passwordManager.*;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Nico on 07/06/2017.
 */
public class Parametres implements Initializable {
    private App app;

    private ValidationSupport validationSupport = new ValidationSupport();

    @FXML private GridPane gpRoot;

    private IntField ifLargeurDefaut;
    private IntField ifHauteurDefaut;
    private IntField ifBackupAuto;
    @FXML private TextField tfDossierBackup;
    private IntField ifLimiteHistorique;

    @FXML private CheckBox cbChargerDernierFichier;

    @FXML private Slider sLargeurDefaut;
    @FXML private Slider sHauteurDefaut;
    @FXML private Slider sBackupAuto;
    @FXML private Slider sLimiteHistorique;

    @FXML private Label lDefautLargeurDefaut;
    @FXML private Label lDefautHauteurDefaut;
    @FXML private Label lDefautChargerDernierFichier;
    @FXML private Label lDefautBackupAuto;
    @FXML private Label lDefautDossierBackup;
    @FXML private Label lDefautLimiteHistorique;

    @FXML private Button bChoisirDossier;

    @FXML private Button bOk;
    @FXML private Button bAnnuler;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        bOk.disableProperty().bind(validationSupport.invalidProperty()); // à enlever quand paramètre implémentés

        bChoisirDossier.setText(null);
        bChoisirDossier.setTooltip(new Tooltip("Choisir un dossier"));

        ifLargeurDefaut = new IntField(400, 2000, 1000);
        ifHauteurDefaut = new IntField(400, 2000, 1000);
        ifBackupAuto = new IntField(0, 20, 5);
        ifLimiteHistorique = new IntField(0, 20, 10);

        gpRoot.add(ifLargeurDefaut, 2, 1);
        gpRoot.add(ifHauteurDefaut, 2, 2);
        gpRoot.add(ifBackupAuto, 2, 4);
        gpRoot.add(ifLimiteHistorique, 2, 6);

        ifLargeurDefaut.valueProperty().bindBidirectional(sLargeurDefaut.valueProperty());
        ifHauteurDefaut.valueProperty().bindBidirectional(sHauteurDefaut.valueProperty());
        ifBackupAuto.valueProperty().bindBidirectional(sBackupAuto.valueProperty());
        ifLimiteHistorique.valueProperty().bindBidirectional(sLimiteHistorique.valueProperty());

        tfDossierBackup.textProperty().addListener((o, ov, nv) -> tfDossierBackup.setText(Utils.toLocalPath(nv)));
    }

    @FXML
    private void ok() {
        PasswordManager p = app.getPasswordManager();

        p.getPreferences().setPropriete(Preferences.PROP_DEFAUT_LARGEUR, String.valueOf(ifLargeurDefaut.getValue()));
        p.getPreferences().setPropriete(Preferences.PROP_DEFAUT_HAUTEUR, String.valueOf(ifHauteurDefaut.getValue()));
        p.getPreferences().setPropriete(Preferences.PROP_CHARGER_DERNIER_FICHIER, String.valueOf(cbChargerDernierFichier.isSelected()));
        p.getPreferences().setPropriete(Preferences.PROP_BACKUP_AUTO, String.valueOf(ifBackupAuto.getValue()));
        p.getPreferences().setPropriete(Preferences.PROP_DOSSIER_BACKUP_AUTO, tfDossierBackup.getText());
        p.getPreferences().setPropriete(Preferences.PROP_MAX_HISTORIQUE, String.valueOf(ifLimiteHistorique.getValue()));

        app.updateWithPreferences();

        app.finEdition();
    }

    @FXML
    private void annuler() {
        app.finEdition();
    }

    void initDonnees() {
        Preferences p = app.getPasswordManager().getPreferences();

        ifLargeurDefaut.setValue(Integer.parseInt(p.getPropriete(Preferences.PROP_DEFAUT_LARGEUR)));
        ifHauteurDefaut.setValue(Integer.parseInt(p.getPropriete(Preferences.PROP_DEFAUT_HAUTEUR)));
        ifBackupAuto.setValue(Integer.parseInt(p.getPropriete(Preferences.PROP_BACKUP_AUTO)));
        ifLimiteHistorique.setValue(Integer.parseInt(p.getPropriete(Preferences.PROP_MAX_HISTORIQUE)));

        cbChargerDernierFichier.setSelected(Boolean.valueOf(p.getPropriete(Preferences.PROP_CHARGER_DERNIER_FICHIER)));
        tfDossierBackup.setText(p.getPropriete(Preferences.PROP_DOSSIER_BACKUP_AUTO));

        lDefautLargeurDefaut.setText(p.getDefautPropriete(Preferences.PROP_DEFAUT_LARGEUR));
        lDefautHauteurDefaut.setText(p.getDefautPropriete(Preferences.PROP_DEFAUT_HAUTEUR));
        lDefautChargerDernierFichier.setText(p.getDefautPropriete(Preferences.PROP_CHARGER_DERNIER_FICHIER));
        lDefautBackupAuto.setText(p.getDefautPropriete(Preferences.PROP_BACKUP_AUTO));
        lDefautDossierBackup.setText(Utils.toLocalPath(p.getDefautPropriete(Preferences.PROP_DOSSIER_BACKUP_AUTO)));
        lDefautLimiteHistorique.setText(p.getDefautPropriete(Preferences.PROP_MAX_HISTORIQUE));

        validationSupport.registerValidator(tfDossierBackup, ((control, o) -> {
            File f = new File(o.toString());
            return ValidationResult.fromErrorIf(control, "dossier n'existe pas", !f.exists() || !f.isDirectory());
        }));
    }

    @FXML
    private void setDossierBackup() {
        String c = choisirDossier();
        if (!c.equals("")) tfDossierBackup.setText(c);
    }

    private String choisirDossier() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setInitialDirectory(new File("."));
        directoryChooser.setTitle("Choisir un dossier");

        File f = directoryChooser.showDialog(null);
        return f == null ? "" : f.getAbsolutePath();
    }

    void bindParent(App a) {
        app = a;

        bChoisirDossier.setGraphic(app.getImageManager().constructImageViewFrom(ImageManager.ICONE_UPLOAD, 16, 16, true));
    }
}
