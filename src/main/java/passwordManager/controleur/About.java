package passwordManager.controleur;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Nico on 08/06/2017.
 */
public class About implements Initializable {
    private App app;

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
