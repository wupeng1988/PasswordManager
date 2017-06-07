package passwordManager.controleur;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Nico on 07/06/2017.
 */
public class Parametres implements Initializable {
    private App app;

    @FXML private Button bOk;
    @FXML private Button bAnnuler;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        bOk.setDisable(true); // à enlever quand paramètre implémentés
    }

    @FXML
    private void ok() {
        app.finEdition();
    }

    @FXML
    private void annuler() {
        app.finEdition();
    }

    void bindParent(App a) {
        app = a;
    }
}
