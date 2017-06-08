package passwordManager.controleur;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import org.controlsfx.validation.ValidationResult;
import org.controlsfx.validation.ValidationSupport;
import org.controlsfx.validation.Validator;
import passwordManager.Preferences;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Nico on 07/06/2017.
 */
public class Parametres implements Initializable {
    private App app;

    private ValidationSupport validationSupport = new ValidationSupport();

    @FXML private TextField tfLargeurDefaut;
    @FXML private TextField tfHauteurDefaut;

    @FXML private Button bOk;
    @FXML private Button bAnnuler;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        bOk.disableProperty().bind(validationSupport.invalidProperty()); // à enlever quand paramètre implémentés

        tfLargeurDefaut.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER)
                ok();
        });
        tfHauteurDefaut.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER)
                ok();
        });
    }

    @FXML
    private void ok() {
        app.passwordManager.getPreferences().setProperty(Preferences.PROP_DEFAUT_LARGEUR, tfLargeurDefaut.getText());
        app.passwordManager.getPreferences().setProperty(Preferences.PROP_DEFAUT_HAUTEUR, tfHauteurDefaut.getText());
        app.finEdition();
    }

    @FXML
    private void annuler() {
        app.finEdition();
    }

    void initDonnees() {
        tfLargeurDefaut.setText((String) app.passwordManager.getPreferences().getProperty(Preferences.PROP_DEFAUT_LARGEUR));
        tfHauteurDefaut.setText((String) app.passwordManager.getPreferences().getProperty(Preferences.PROP_DEFAUT_HAUTEUR));

        Validator<String> validator = (control, s) -> {
            Integer val = null;
            if (!s.equals(""))
                val = Integer.parseInt(s);
            return ValidationResult.fromErrorIf(control, "error", val != null && (val < 400 || val > 2000));
        };
        validationSupport.registerValidator(tfHauteurDefaut, validator);
        validationSupport.registerValidator(tfLargeurDefaut, validator);
    }

    void bindParent(App a) {
        app = a;
    }
}
