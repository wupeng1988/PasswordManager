package passwordManager.controleur;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import passwordManager.model.Domaine;
import passwordManager.model.Donnees;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Nico on 07/06/2017.
 */
public class DetailsIdle implements Initializable {
    private Donnees donneesAffichees;

    @FXML private Label lDomaineTaille;
    @FXML private Label lCompteTaille;
    @FXML private Label lMoyenne;

    @Override
    public void initialize(URL location, ResourceBundle resources) {}

    void initDonnees(Donnees donnees) {
        if (donneesAffichees != null && donnees.equals(donneesAffichees)) return;

        donneesAffichees = donnees;

        int domaineTaille = donnees.getDomaines().size();
        int compteTaille = 0;

        for (Domaine d : donnees.getDomaines())
            compteTaille += d.getComptes().size();

        lDomaineTaille.setText(String.valueOf(domaineTaille));
        lCompteTaille.setText(String.valueOf(compteTaille));
        lMoyenne.setText(String.valueOf((domaineTaille > 0 ? (double)compteTaille / domaineTaille : 0)));
    }
}
