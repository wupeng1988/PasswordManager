package passwordManager.controleur;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import passwordManager.model.Compte;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Nico on 05/06/2017.
 */
public class EditionCompteControleur implements Initializable {
    private AppControleur app;

    private Compte toEdit;
    private boolean exists;

    @FXML Label titre;
    @FXML TextField utilisateur;
    @FXML TextField motDePasse;
    @FXML TextArea notes;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("Init compte ctrl");
    }

    void bindParent(AppControleur c) {
        app = c;
    }

    private void finEdition(boolean ok) {
        if (ok) {
            toEdit.setUtilisateur(utilisateur.getText());
            toEdit.setMotDePasse(motDePasse.getText());
            toEdit.setNotes(notes.getText());

            if (!exists) {
                app.domaineSelectionne.addCompte(toEdit);
            }
        }

        app.finEdition();
    }

    @FXML
    private void okEdition() {
        finEdition(true);
    }
    @FXML
    private void annulerEdition() {
        finEdition(false);
    }

    void initCompte(Compte c) {
        toEdit = c;
        titre.setText(c.getUtilisateur());
        utilisateur.setText(c.getUtilisateur());
        motDePasse.setText(c.getMotDePasse());
        notes.setText(c.getNotes());
        exists = true;
    }

    void nouveauCompte() {
        toEdit = new Compte("Nouveau compte", "");
        initCompte(toEdit);
        exists = false;
    }
}
