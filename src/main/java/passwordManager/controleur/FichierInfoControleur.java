package passwordManager.controleur;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import org.controlsfx.validation.Severity;
import org.controlsfx.validation.ValidationResult;
import org.controlsfx.validation.ValidationSupport;
import org.controlsfx.validation.Validator;
import passwordManager.model.Donnees;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Nico on 06/06/2017.
 */
public class FichierInfoControleur implements Initializable {
    private AppControleur app;

    private ValidationSupport validationSupport = new ValidationSupport();
    private Validator<String> validator;

    @FXML private ComboBox<Integer> encryptionLevel;
    @FXML private Label autorise;
    @FXML private TextField motDePasse;

    @FXML private Button ok;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ObservableList<Integer> items = FXCollections.observableArrayList(
                0,
                1,
                2,
                3
        );

        encryptionLevel.setItems(items);
        encryptionLevel.getSelectionModel().selectedItemProperty().addListener((o, ov, nv) -> setValidators());
        ok.disableProperty().bind(validationSupport.invalidProperty());
        validator = (control, s) -> {
            boolean condition =
                    encryptionLevel.getSelectionModel().getSelectedItem() != null
                    && encryptionLevel.getSelectionModel().getSelectedItem() > 0
                    && s.length() < 6;

            return ValidationResult.fromMessageIf(control, "mot de passe < 6", Severity.ERROR, condition);
        };
    }

    private void setValidators() {
        if (encryptionLevel.getSelectionModel().getSelectedItem() > 0) { // besoin d'un mot de passe
            motDePasse.setDisable(false);
        } else {
            motDePasse.setDisable(true);
        }
        validationSupport.registerValidator(motDePasse, false, validator);
    }

    @FXML
    private void okEdition() {
        app.donneesActives.setEncrytionLevel(encryptionLevel.getSelectionModel().getSelectedItem());
        app.donneesActives.setMotDePasse(motDePasse.getText());
        app.finEdition();
    }

    @FXML
    private void annulerEdition() {
        app.finEdition();
    }

    void initData(Donnees donnees) {
        autorise.setText((donnees.isAutorise() ? "Oui" : "Non"));
        encryptionLevel.getSelectionModel().select((Integer)donnees.getEncrytionLevel());
        motDePasse.setText(donnees.getMotDePasse());
    }

    void bindParent(AppControleur a) {
        app = a;
    }
}
