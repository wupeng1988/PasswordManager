package passwordManager.controleur;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import passwordManager.model.Compte;
import passwordManager.model.Domaine;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Nico on 07/06/2017.
 */
public class Confirmation implements Initializable {
    private App app;

    private Object toDelete;

    @FXML private Label lObjetType;
    @FXML private Label lNom;

    @Override
    public void initialize(URL location, ResourceBundle resources) {}

    @FXML
    private void ok() {
        app.suppression(toDelete);
        app.finEdition();
    }
    @FXML
    private void annuler() {
        app.finEdition();
    }

    void initObject(Object o) { // o est soit un compte, soit un domaine
        int oType = -1; // -1 = rien, 0 = compte et 1 = domaine
        if (o instanceof Compte) oType = 0;
        else if (o instanceof Domaine) oType = 1;

        if (oType < 0) return;

        toDelete = o;

        switch (oType) {
            case 0:
                lObjetType.setText("Compte");
                lNom.setText(((Compte) o).getUtilisateur());
                break;
            case 1:
                lObjetType.setText("Domaine");
                lNom.setText(((Domaine) o).getNom());
                break;
            default:
                break;
        }
    }

    void bindParent(App a) {
        app = a;
    }
}
