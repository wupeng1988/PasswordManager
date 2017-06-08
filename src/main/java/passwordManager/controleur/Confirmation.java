package passwordManager.controleur;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import passwordManager.model.ActionHistorique;
import passwordManager.model.Applicable;
import passwordManager.model.Compte;
import passwordManager.model.Domaine;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Nico on 07/06/2017.
 */
public class Confirmation implements Initializable {
    private App app;

    private Domaine wrapper;
    private Applicable toDelete;
    private int type;

    @FXML private Label lObjetType;
    @FXML private Label lNom;

    @Override
    public void initialize(URL location, ResourceBundle resources) {}

    @FXML
    private void ok() {
        if (type == 0)
            app.donneesActives.getHistorique().ajoutAction(new ActionHistorique(null, null, toDelete, ActionHistorique.Action.SUPPRESSION, ActionHistorique.Type.DOMAINE));
        else
            app.donneesActives.getHistorique().ajoutAction(new ActionHistorique(wrapper, null, toDelete, ActionHistorique.Action.SUPPRESSION, ActionHistorique.Type.COMPTE));

        app.suppression(toDelete);
        app.finEdition();
    }
    @FXML
    private void annuler() {
        app.finEdition();
    }

    void initObject(Domaine d) {
        toDelete = d;
        type = 0;

        lObjetType.setText("Domaine");
        lNom.setText(d.getNom());
    }
    void initObject(Compte c, Domaine d) { // o est soit un compte, soit un domaine
        toDelete = c;
        wrapper = d;
        type = 1;

        lObjetType.setText("Compte");
        lNom.setText(c.getUtilisateur());

    }

    void bindParent(App a) {
        app = a;
    }
}
