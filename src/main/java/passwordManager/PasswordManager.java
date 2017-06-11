package passwordManager;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.stage.Stage;
import passwordManager.controleur.App;

public class PasswordManager extends Application {
    private KeyCodeCombination ctrlS =      new KeyCodeCombination(KeyCode.S, KeyCodeCombination.CONTROL_ANY);
    private KeyCodeCombination ctrlO =      new KeyCodeCombination(KeyCode.O, KeyCodeCombination.CONTROL_ANY);
    private KeyCodeCombination ctrlN =      new KeyCodeCombination(KeyCode.N, KeyCodeCombination.CONTROL_ANY);
    private KeyCodeCombination ctrlZ =      new KeyCodeCombination(KeyCode.Z, KeyCodeCombination.CONTROL_ANY);
    private KeyCodeCombination ctrlP =      new KeyCodeCombination(KeyCode.P, KeyCodeCombination.CONTROL_ANY);
    private KeyCodeCombination ctrlH =      new KeyCodeCombination(KeyCode.H, KeyCodeCombination.CONTROL_ANY);
    private KeyCodeCombination ctrlW =      new KeyCodeCombination(KeyCode.W, KeyCodeCombination.CONTROL_ANY);
    private KeyCodeCombination ctrlD =      new KeyCodeCombination(KeyCode.D, KeyCodeCombination.CONTROL_ANY);
    private KeyCodeCombination ctrlShiftS = new KeyCodeCombination(KeyCode.S, KeyCodeCombination.CONTROL_ANY, KeyCodeCombination.SHIFT_ANY);
    private KeyCodeCombination ctrlShiftZ = new KeyCodeCombination(KeyCode.Z, KeyCodeCombination.CONTROL_ANY, KeyCodeCombination.SHIFT_ANY);

    private Clipboard clipboard = Clipboard.getSystemClipboard();
    private ClipboardContent clipboardContent = new ClipboardContent();

    private Preferences preferences = new Preferences();

    public final static String TITLE =          "Password Manager";
    public final static String SAVE_EXTENSION = ".psw";

    private final static String DOSSIER_RESSOURCES = "/";

    private final static String DOSSIER_FXML =      DOSSIER_RESSOURCES + "fxml/";
    private final static String DOSSIER_CSS =       DOSSIER_RESSOURCES + "stylesheets/";
    public final static String DOSSIER_FONTS =      DOSSIER_RESSOURCES + "fonts/";
    final static String DOSSIER_IMAGES =            DOSSIER_RESSOURCES + "images/";
    private final static String DOSSIER_TEXTE =     DOSSIER_RESSOURCES + "text/";

    private final static String FXML_APP =              DOSSIER_FXML + "App.fxml";
    public final static String FXML_DETAILSIDLE =       DOSSIER_FXML + "DetailsIdle.fxml";
    public final static String FXML_AUTORISATION =      DOSSIER_FXML + "Autorisation.fxml";
    public final static String FXML_FICHIERINFO =       DOSSIER_FXML + "FichierInfo.fxml";
    public final static String FXML_INFOSCOMPTE =       DOSSIER_FXML + "InfosCompte.fxml";
    public final static String FXML_INFOSDOMAINE =      DOSSIER_FXML + "InfosDomaine.fxml";
    public final static String FXML_CONFIRMATION =      DOSSIER_FXML + "Confirmation.fxml";
    public final static String FXML_PARAMETRES =        DOSSIER_FXML + "Parametres.fxml";
    public final static String FXML_IMAGESELECTION =    DOSSIER_FXML + "ImageSelection.fxml";
    public final static String FXML_ABOUT =             DOSSIER_FXML + "About.fxml";
    public final static String FXML_EXPLICATIONS =      DOSSIER_FXML + "Explications.fxml";

    public final static String TEXT_NAMES =             DOSSIER_TEXTE + "names.txt";

    private final static String CSS_BOOTSTRAP =         DOSSIER_CSS + "bootstrap3.css";
    private final static String CSS_PASSWORDMANAGER =   DOSSIER_CSS + "style.css";

    private Stage stage;
    private App app;

    private boolean beforeClose() {
        return app.checkSaveEnregistree();
    }

    @Override
    public void stop() throws Exception {
        preferences.write();
        super.stop();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        stage = primaryStage;

        FXMLLoader l = new FXMLLoader(getClass().getResource(FXML_APP));
        Parent root = l.load();
        app = l.getController();
        app.setMain(this);

        int width = Integer.parseInt(preferences.getPropriete(Preferences.PROP_DEFAUT_LARGEUR));
        int height = Integer.parseInt(preferences.getPropriete(Preferences.PROP_DEFAUT_HAUTEUR));

        Scene s = new Scene(root, width, height);
        s.getStylesheets().addAll(
                getClass().getResource(CSS_BOOTSTRAP).toExternalForm(),
                getClass().getResource(CSS_PASSWORDMANAGER).toExternalForm()
        );

        stage.setTitle(TITLE);
        stage.setScene(s);
        initShortcuts();

        stage.show();
        app.initPhase2();

        s.getWindow().setOnCloseRequest(event -> {
            if (!beforeClose())
                event.consume();
        });
    }

    public void clipboardPut(String s) {
        clipboardContent.putString(s);
        clipboard.setContent(clipboardContent);
    }

    public Preferences getPreferences() {
        return preferences;
    }
    public Stage getStage() {
        return stage;
    }

    private void initShortcuts() {
        stage.getScene().setOnKeyPressed(event -> {
            if (!app.inOptions) {
                if (ctrlS.match(event)) {
                    app.sauvegarderSc();
                } else if (ctrlShiftS.match(event)) {
                    app.sauvegarderDialog();
                } else if (ctrlO.match(event)) {
                    app.chargerDialog();
                } else if (ctrlP.match(event)) {
                    app.montrerParametres();
                } else if (ctrlH.match(event)) {
                    app.montrerExplications();
                } else if (ctrlN.match(event)) {
                    app.nouvelleSauvegarde();
                } else if (ctrlW.match(event)) {
                    app.fermer();
                } else if (ctrlD.match(event)) {
                    app.dupliquerAuFocus();
                } else if (ctrlZ.match(event)) {
                    app.defaire();
                } else if (ctrlShiftZ.match(event)) {
                    app.refaire();
                } else if (event.getCode() == KeyCode.DELETE) {
                    app.supprimerAuFocus();
                } else if (event.getCode() == KeyCode.ADD) {
                    app.ajouterAuFocus();
                } else if (event.getCode() == KeyCode.MULTIPLY) {
                    app.modifierAuFocus();
                } else if (event.getCode() == KeyCode.SUBTRACT) {
                    app.supprimerAuFocus();
                }
            }
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}
