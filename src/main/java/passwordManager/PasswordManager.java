package passwordManager;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.stage.Stage;
import passwordManager.controleur.AppControleur;

public class PasswordManager extends Application {
    private static final int WIDTH = 800;
    private static final int HEIGHT = 400;

    public final static String TITLE = "Password Manager";
    public final static String SAVE_EXTENSION = ".psw";

    private KeyCodeCombination ctrlS = new KeyCodeCombination(KeyCode.S, KeyCodeCombination.CONTROL_ANY);
    private KeyCodeCombination ctrlShiftS = new KeyCodeCombination(KeyCode.S, KeyCodeCombination.CONTROL_ANY, KeyCodeCombination.SHIFT_ANY);
    private KeyCodeCombination ctrlO = new KeyCodeCombination(KeyCode.O, KeyCodeCombination.CONTROL_ANY);
    private KeyCodeCombination ctrlN = new KeyCodeCombination(KeyCode.N, KeyCodeCombination.CONTROL_ANY);

    public Stage stage;
    private AppControleur app;

    @Override
    public void stop() throws Exception {
        System.out.println("Stopping...");
        super.stop();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        stage = primaryStage;

        FXMLLoader l = new FXMLLoader(getClass().getResource("/fxml/AppVue.fxml"));
        Parent root = l.load();
        app = l.getController();
        app.setMain(this);

        Scene s = new Scene(root, WIDTH, HEIGHT);
        s.getStylesheets().add(getClass().getResource("/stylesheets/style.css").toExternalForm());

        stage.setTitle(TITLE);
        stage.setScene(s);
        initShortcuts();

        stage.show();
        app.finishLoad();
    }

    private void initShortcuts() {
        stage.getScene().setOnKeyPressed(event -> {
            if (ctrlS.match(event)) {
                app.sauvegarderSc();
            } else if (ctrlShiftS.match(event)) {
                app.sauvegarderDialog();
            } else if (ctrlO.match(event)) {
                app.chargerDialog();
            } else if (ctrlN.match(event)) {
                app.nouvelleSauvegarde();
            }
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}
