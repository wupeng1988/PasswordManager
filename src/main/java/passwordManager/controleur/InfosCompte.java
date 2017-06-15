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

    private final static String MDP_ALPHABET_LETTRE_MAJ = "ABCDEFGHIJKLMNOPQRSTUVWYZ";
    private final static String MDP_ALPHABET_LETTRE_MIN = MDP_ALPHABET_LETTRE_MAJ.toLowerCase();
    private final static String MDP_ALPHABET_CHIFFRE = "0123456789";
    private final static String MDP_ALPHABET_SEPARATEUR = "-_";
    private final static String MDP_ALPHABET_SPECIAL = "$*";

    private final static String MDP_ALPHABET =
                                    MDP_ALPHABET_LETTRE_MIN
                                    + MDP_ALPHABET_LETTRE_MAJ
                                    + MDP_ALPHABET_CHIFFRE
                                    + MDP_ALPHABET_SEPARATEUR
                                    + MDP_ALPHABET_SPECIAL;

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
        boolean condition = s.length() < 2 || s.length() > 30;
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

        bAlea.setTooltip(new Tooltip("Tout générer aléatoirement"));
        bAleaU.setTooltip(new Tooltip("Générer un nom d'utilisateur aléatoirement"));
        bAleaM.setTooltip(new Tooltip("Générer un mot de passe aléatoirement"));
        bCopyU.setTooltip(new Tooltip("Copier le nom d'utilisateur dans le presse-papier"));
        bCopyM.setTooltip(new Tooltip("Copier le mot de passe dans le presse-papier"));

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

        char sep = MDP_ALPHABET_SEPARATEUR.charAt(r.nextInt(MDP_ALPHABET_SEPARATEUR.length()));

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
        Random r = app.getRandom();

        int lengthVar = 6;
        int lengthMin = 6;
        int length = r.nextInt(lengthVar) + lengthMin;

        StringBuilder gen = new StringBuilder();

        for (int i = 0; i < length; i++)
            gen.append(MDP_ALPHABET.charAt(r.nextInt(MDP_ALPHABET.length())));

        return gen.toString();
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

        Platform.runLater(() -> {
            tfUtilisateur.requestFocus();
            tfUtilisateur.selectAll();
        });
    }

    void nouveauCompte() {
        initCompte(new Compte("Nouveau compte", ""));
        exists = false;
    }
}
