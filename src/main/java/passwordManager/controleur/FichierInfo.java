package passwordManager.controleur;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
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
public class FichierInfo implements Initializable {
    private App app;

    private ValidationSupport validationSupport = new ValidationSupport();
    private Validator<String> validator;

    @FXML private ComboBox<Integer> cbEncryptionLevel;
    @FXML private Label lAutorise;
    @FXML private TextField tfMotDePasse;

    @FXML private Button bOk;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ObservableList<Integer> items = FXCollections.observableArrayList(
                0,
                1,
                2,
                3
        );

        cbEncryptionLevel.setItems(items);
        cbEncryptionLevel.getSelectionModel().selectedItemProperty().addListener((o, ov, nv) -> setValidators());

        tfMotDePasse.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                okEdition();
            }
        });

        validator = (control, s) -> {
            boolean condition =
                    cbEncryptionLevel.getSelectionModel().getSelectedItem() != null
                    && cbEncryptionLevel.getSelectionModel().getSelectedItem() > 0
                    && s.length() < 6;

            return ValidationResult.fromMessageIf(control, "mot de passe < 6", Severity.ERROR, condition);
        };
        bOk.disableProperty().bind(validationSupport.invalidProperty());
    }

    private void setValidators() {
        if (cbEncryptionLevel.getSelectionModel().getSelectedItem() > 0) { // besoin d'un mot de passe
            tfMotDePasse.setDisable(false);
        } else {
            tfMotDePasse.setDisable(true);
        }
        validationSupport.registerValidator(tfMotDePasse, false, validator);
    }

    @FXML
    private void okEdition() {
        app.getDonneesActives().setEncrytionLevel(cbEncryptionLevel.getSelectionModel().getSelectedItem());
        app.getDonneesActives().setMotDePasse(tfMotDePasse.getText());
        app.finEdition();
    }

    @FXML
    private void annulerEdition() {
        app.finEdition();
    }

    void initData(Donnees donnees) {
        lAutorise.setText((donnees.isAutorise() ? "Oui" : "Non"));
        cbEncryptionLevel.getSelectionModel().select((Integer)donnees.getEncrytionLevel());
        cbEncryptionLevel.setDisable(!donnees.isAutorise());
        tfMotDePasse.setText(donnees.getMotDePasse());
    }

    void bindParent(App a) {
        app = a;
    }
}
