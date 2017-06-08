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
    private KeyCodeCombination ctrlS = new KeyCodeCombination(KeyCode.S, KeyCodeCombination.CONTROL_ANY);
    private KeyCodeCombination ctrlShiftS = new KeyCodeCombination(KeyCode.S, KeyCodeCombination.CONTROL_ANY, KeyCodeCombination.SHIFT_ANY);
    private KeyCodeCombination ctrlO = new KeyCodeCombination(KeyCode.O, KeyCodeCombination.CONTROL_ANY);
    private KeyCodeCombination ctrlN = new KeyCodeCombination(KeyCode.N, KeyCodeCombination.CONTROL_ANY);

    private Clipboard clipboard = Clipboard.getSystemClipboard();
    private ClipboardContent clipboardContent = new ClipboardContent();

    private static final int WIDTH =    800;
    private static final int HEIGHT =   400;

    public final static String TITLE =          "Password Manager";
    public final static String SAVE_EXTENSION = ".psw";

    private final static String DOSSIER_RESSOURCES = "/";

    public final static String DOSSIER_FXML =       DOSSIER_RESSOURCES + "fxml/";
    public final static String DOSSIER_CSS =        DOSSIER_RESSOURCES + "stylesheets/";
    public final static String DOSSIER_FONTS =      DOSSIER_RESSOURCES + "fonts/";
    public final static String DOSSIER_IMAGES =     DOSSIER_RESSOURCES + "images/";
    public final static String DOSSIER_TEXTE =      DOSSIER_RESSOURCES + "text/";

    public final static String FXML_DETAILSIDLE =       DOSSIER_FXML + "DetailsIdle.fxml";
    public final static String FXML_AUTORISATION =      DOSSIER_FXML + "Autorisation.fxml";
    public final static String FXML_FICHIERINFO =       DOSSIER_FXML + "FichierInfo.fxml";
    public final static String FXML_INFOSCOMPTE =       DOSSIER_FXML + "InfosCompte.fxml";
    public final static String FXML_INFOSDOMAINE =      DOSSIER_FXML + "InfosDomaine.fxml";
    public final static String FXML_CONFIRMATION =      DOSSIER_FXML + "Confirmation.fxml";
    public final static String FXML_PARAMETRES =        DOSSIER_FXML + "Parametres.fxml";
    public final static String FXML_APP =               DOSSIER_FXML + "App.fxml";
    public final static String FXML_IMAGESELECTION =    DOSSIER_FXML + "ImageSelection.fxml";
    public final static String FXML_ABOUT =             DOSSIER_FXML + "About.fxml";
    public final static String FXML_EXPLICATIONS =      DOSSIER_FXML + "Explications.fxml";

    public final static String TEXT_NAMES =             DOSSIER_TEXTE + "names.txt";

    public final static String CSS_BOOTSTRAP =          DOSSIER_CSS + "bootstrap3.css";
    public final static String CSS_PASSWORDMANAGER =    DOSSIER_CSS + "style.css";

    public Stage stage;
    private App app;

    @Override
    public void stop() throws Exception {
        System.out.println("Stopping...");
        super.stop();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        stage = primaryStage;

        FXMLLoader l = new FXMLLoader(getClass().getResource(FXML_APP));
        Parent root = l.load();
        app = l.getController();
        app.setMain(this);

        Scene s = new Scene(root, WIDTH, HEIGHT);
        s.getStylesheets().addAll(
                getClass().getResource(CSS_BOOTSTRAP).toExternalForm(),
                getClass().getResource(CSS_PASSWORDMANAGER).toExternalForm()
        );

        stage.setTitle(TITLE);
        stage.setScene(s);
        initShortcuts();

        stage.show();
        app.initPhase2();
    }

    public void clipboardPut(String s) {
        clipboardContent.putString(s);
        clipboard.setContent(clipboardContent);
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
                } else if (ctrlN.match(event)) {
                    app.nouvelleSauvegarde();
                }
            }
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}
