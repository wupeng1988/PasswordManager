package passwordManager.controleur;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import passwordManager.model.*;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Nico on 07/06/2017.
 */
public class Confirmation implements Initializable {
    private App app;

    private Object wrapper;
    private Applicable toDelete;
    private int type;

    @FXML private AnchorPane root;

    @FXML private Label lObjetType;
    @FXML private Label lNom;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        root.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER)
                ok();
            else if (event.getCode() == KeyCode.ESCAPE)
                annuler();
        });
    }

    @FXML
    private void ok() {
        Historique h = app.getDonneesActives().getHistorique();

        if (type == 0) {
            Donnees d = (Donnees) wrapper;
            Domaine dom = (Domaine) toDelete;
            int place = d.getDomaines().indexOf(toDelete);
            h.ajoutAction(ActionHistorique.suppressionDomaine(place, d, dom));
        } else {
            Domaine d = (Domaine) wrapper;
            Compte c = (Compte) toDelete;
            int place = d.getComptes().indexOf(toDelete);

            h.ajoutAction(ActionHistorique.suppressionCompte(place, d, c));
        }

        app.finEdition();
    }
    @FXML
    private void annuler() {
        app.finEdition();
    }

    void initObject(Domaine d) {
        toDelete = d;
        wrapper = app.getDonneesActives();
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

        root.requestFocus();
    }

    void bindParent(App a) {
        app = a;
    }
}
