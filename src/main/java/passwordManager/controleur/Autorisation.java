package passwordManager.controleur;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import passwordManager.Crypto;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Nico on 07/06/2017.
 */
public class Autorisation implements Initializable {
    private App app;

    @FXML private TextField tfMotDePasse;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        tfMotDePasse.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                ok();
            }
        });
    }

    private void autorisation(String mdp) {
        try {
            Crypto crypto = new Crypto(mdp);

            app.charger(app.fichierOuvert, crypto);
            app.donneesActives.setAutorise(true);
            app.donneesActives.setMotDePasse(mdp);
            app.initUi();
            app.finEdition();
        } catch (Exception e) {
            System.out.println("MDP INCORRECT");
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

    void bindParent(App a) {
        app = a;
    }
}
