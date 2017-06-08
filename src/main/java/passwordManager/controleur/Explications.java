package passwordManager.controleur;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Nico on 08/06/2017.
 */
public class Explications implements Initializable {
    private App app;

    @FXML private Button bOk;

    @Override
    public void initialize(URL location, ResourceBundle resources) {}

    @FXML
    private void ok() {
        app.finEdition();
    }

    void bindParent(App app) {
        this.app = app;
    }
}
