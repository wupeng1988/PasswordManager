package passwordManager.controleur;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import org.controlsfx.validation.ValidationResult;
import org.controlsfx.validation.ValidationSupport;
import passwordManager.Crypto;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Nico on 07/06/2017.
 */
public class Autorisation implements Initializable {
    private App app;

    @FXML private TextField tfMotDePasse;
    @FXML private Label lStatut;
    @FXML private Button bOk;

    private ValidationSupport validationSupport = new ValidationSupport();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        tfMotDePasse.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                ok();
            }
        });

        validationSupport.registerValidator(tfMotDePasse, ((control, o) -> ValidationResult.fromErrorIf(control, "e", ((String)o).length() < 6)));
        bOk.disableProperty().bind(validationSupport.invalidProperty());
    }

    private void autorisation(String mdp) {
        try {
            Crypto crypto = new Crypto(mdp);

            app.ouvrir(app.getFichierOuvert(), crypto);
            app.getDonneesActives().setAutorise(true);
            app.getDonneesActives().setMotDePasse(mdp);
            app.initUi();
            app.finEdition();
        } catch (Exception e) {
            System.err.println("Mot de passe incorrect");
            lStatut.setText("échec -> veuillez vérifier puis réessayer");
            lStatut.setStyle("-fx-text-fill: red");
        }
    }

    @FXML
    private void ok() {
        autorisation(tfMotDePasse.getText());
    }

    @FXML
    private void annuler() {
        app.finEdition();
    }

    void initDonnees() {
        Platform.runLater(tfMotDePasse::requestFocus);
    }

    void bindParent(App a) {
        app = a;
    }
}
