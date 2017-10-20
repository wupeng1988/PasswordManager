package passwordManager.controleur;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import org.controlsfx.validation.ValidationResult;
import org.controlsfx.validation.ValidationSupport;
import org.controlsfx.validation.Validator;
import passwordManager.ImageManager;
import passwordManager.PasswordManager;
import passwordManager.alea.AleaScheme;
import passwordManager.model.*;

import java.net.URL;
import java.util.ArrayList;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.Scanner;

/**
 * Nico on 05/06/2017.
 */
public class InfosCompte implements Initializable {
    private App app;

    private Compte toEdit;
    private boolean exists;

    private ArrayList<String> listeNoms;

    @FXML private Button bOk;

    @FXML private Button bAlea;
    @FXML private Button bAleaU;
    @FXML private Button bAleaM;
    @FXML private Button bCopyU;
    @FXML private Button bCopyM;

    @FXML private Label lDateCreation;

    @FXML private Label lTitre;
    @FXML private TextField tfUtilisateur;
    @FXML private TextField tfMotDePasse;
    @FXML private TextArea taNotes;

    private ValidationSupport validationSupport = new ValidationSupport();
    private Validator<String> validator = (control, s) -> {
        boolean condition = s.length() < 2 || s.length() > 50;
        return ValidationResult.fromErrorIf(control, "error", condition);
    };

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        listeNoms = new ArrayList<>();
        try (Scanner s = new Scanner(getClass().getResourceAsStream(PasswordManager.TEXT_NAMES))) {
            String line;
            while ((line = s.nextLine()) != null) {
                listeNoms.add(line);
            }
        } catch (Exception ignored) {}

        bAleaU.setText(null);
        bAleaM.setText(null);
        bCopyU.setText(null);
        bCopyM.setText(null);

        bAlea.setTooltip(new Tooltip("Generate randomly"));
        bAleaU.setTooltip(new Tooltip("Generate a user name randomly"));
        bAleaM.setTooltip(new Tooltip("Generate a password randomly"));
        bCopyU.setTooltip(new Tooltip("Copy the user name to the clipboard"));
        bCopyM.setTooltip(new Tooltip("Copy the password to the clipboard"));

        tfUtilisateur.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER)
                okEdition();
        });
        tfMotDePasse.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER)
                okEdition();
        });

        validationSupport.registerValidator(tfUtilisateur, true, validator);
        validationSupport.registerValidator(tfMotDePasse, true, validator);
        bOk.disableProperty().bind(validationSupport.invalidProperty());
    }

    void bindParent(App c) {
        app = c;

        ImageManager im = app.getImageManager();

        bAlea.setGraphic(im.constructImageViewFrom(ImageManager.ICONE_REFRESH, 16, 16, true));
        bAleaU.setGraphic(im.constructImageViewFrom(ImageManager.ICONE_REFRESH, 16, 16, true));
        bAleaM.setGraphic(im.constructImageViewFrom(ImageManager.ICONE_REFRESH, 16, 16, true));
        bCopyU.setGraphic(im.constructImageViewFrom(ImageManager.ICONE_COPY, 16, 16, true));
        bCopyM.setGraphic(im.constructImageViewFrom(ImageManager.ICONE_COPY, 16, 16, true));
    }

    private void finEdition(boolean ok) {
        if (ok) {
            Historique h = app.getDonneesActives().getHistorique();
            Compte avant = toEdit.snap();

            toEdit.setUtilisateur(tfUtilisateur.getText());
            toEdit.setMotDePasse(tfMotDePasse.getText());
            toEdit.setNotes(taNotes.getText());

            if (!exists) {
                h.ajoutAction(ActionHistorique.ajoutCompte(-1, app.getDomaineSelectionne(), toEdit));
                app.selectionCompte(toEdit);
            } else {
                h.ajoutAction(ActionHistorique.modificationCompte(avant, toEdit));
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

    @FXML
    private void alea() {
        aleaU();
        aleaM();
    }

    @FXML
    private void aleaU() {
        tfUtilisateur.setText(genNomAlea());
    }
    @FXML
    private void aleaM() {
        tfMotDePasse.setText(genMdpAlea());
    }

    @FXML
    private void copyU() {
        app.getPasswordManager().clipboardPut(tfUtilisateur.getText());
    }
    @FXML
    private void copyM() {
        app.getPasswordManager().clipboardPut(tfMotDePasse.getText());
    }

    private String genNomAlea() {
        Random r = app.getRandom();

        char sep = '-';

        int lengthVar = 10;
        int lengthMin = 6;
        int length = r.nextInt(lengthVar) + lengthMin;

        StringBuilder gen = new StringBuilder();

        gen.append(listeNoms.get(r.nextInt(listeNoms.size())));
        while (gen.length() < length)
            gen.append(sep).append(listeNoms.get(r.nextInt(listeNoms.size())));

        return gen.toString();
    }
    private String genMdpAlea() {
        return app.getAleaSchemes().getScheme("default").generate();
    }

    void initCompte(Compte c) {
        int level = app.getDonneesActives().getEncrytionLevel();
        boolean autorise = app.getDonneesActives().isAutorise();
        toEdit = c;
        lTitre.setText((level > 1 && !autorise ? "Non autorisé" : c.getUtilisateur()));
        lDateCreation.setText(c.getDateCreationFormatted());
        tfUtilisateur.setText((level > 1 && !autorise ? "*********" : c.getUtilisateur()));
        tfMotDePasse.setText((level > 0 && !autorise ? "*********" : c.getMotDePasse()));
        taNotes.setText((level > 1 && !autorise ? "*********" : c.getNotes()));
        exists = true;

        ContextMenu contextMenu = new ContextMenu();
        MenuItem mi;
        for (AleaScheme a : app.getAleaSchemes().getSchemes()) {
            mi = new MenuItem(a.getNom());
            mi.setOnAction((action) -> tfMotDePasse.setText(a.generate()));
            contextMenu.getItems().add(mi);
        }
        bAleaM.setContextMenu(contextMenu);

        Platform.runLater(() -> {
            tfUtilisateur.requestFocus();
            tfUtilisateur.selectAll();
        });
    }

    void nouveauCompte() {
        initCompte(new Compte("New account", ""));
        exists = false;
    }
}
