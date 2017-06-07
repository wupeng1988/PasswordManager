package passwordManager.controleur;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import passwordManager.Crypto;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Nico on 07/06/2017.
 */
public class AutorisationControleur implements Initializable {
    private AppControleur app;

    @FXML private TextField motDePasse;

    @Override
    public void initialize(URL location, ResourceBundle resources) {}

    private void autorisation(String mdp) {
        try {
            Crypto crypto = new Crypto(mdp);

            app.charger(app.fichierOuvert, crypto);
            app.donneesActives.setAutorise(true);
            app.initUi();
            app.finEdition();
        } catch (Exception e) {
            System.out.println("MDP INCORRECT");
        }
    }

    @FXML
    private void ok() {
        autorisation(motDePasse.getText());
    }

    @FXML
    private void annuler() {
        app.finEdition();
    }

    void bindParent(AppControleur a) {
        app = a;
    }
}
