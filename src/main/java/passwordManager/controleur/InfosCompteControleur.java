package passwordManager.controleur;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import passwordManager.model.Compte;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Nico on 05/06/2017.
 */
public class InfosCompteControleur implements Initializable {
    private AppControleur app;

    private Compte toEdit;
    private boolean exists;

    @FXML private Label titre;
    @FXML private TextField utilisateur;
    @FXML private TextField motDePasse;
    @FXML private TextArea notes;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        utilisateur.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER)
                okEdition();
        });
        motDePasse.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER)
                okEdition();
        });
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
        int level = app.donneesActives.getEncrytionLevel();
        boolean autorise = app.donneesActives.isAutorise();
        toEdit = c;
        titre.setText((level > 1 && !autorise ? "Non autorisé" : c.getUtilisateur()));
        utilisateur.setText((level > 1 && !autorise ? "*********" : c.getUtilisateur()));
        motDePasse.setText((level > 0 && !autorise ? "*********" : c.getMotDePasse()));
        notes.setText((level > 1 && !autorise ? "*********" : c.getNotes()));
        exists = true;

        Platform.runLater(utilisateur::requestFocus);
    }

    void nouveauCompte() {
        toEdit = new Compte("Nouveau compte", "");
        initCompte(toEdit);
        exists = false;
    }
}