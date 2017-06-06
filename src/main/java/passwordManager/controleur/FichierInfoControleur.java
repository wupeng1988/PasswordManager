package passwordManager.controleur;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import passwordManager.model.Donnees;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Nico on 06/06/2017.
 */
public class FichierInfoControleur implements Initializable {
    private AppControleur app;

    @FXML private ComboBox<Integer> encryptionLevel;
    @FXML private Label autorise;
    @FXML private TextField motDePasse;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ObservableList<Integer> items = FXCollections.observableArrayList(
                0,
                1,
                2,
                3
        );

        encryptionLevel.setItems(items);
    }

    @FXML
    private void okEdition() {
        app.donneesActives.setEncrytionLevel(encryptionLevel.getSelectionModel().getSelectedItem());
        app.finEdition();
    }

    void initData(Donnees donnees) {
        autorise.setText((donnees.isAutorise() ? "Oui" : "Non"));
        encryptionLevel.getSelectionModel().select((Integer)donnees.getEncrytionLevel());
        motDePasse.setText(donnees.getMotDePasse());
    }

    void bindParent(AppControleur a) {
        app = a;
    }
}
