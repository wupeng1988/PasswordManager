package passwordManager;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import passwordManager.controleur.AppControleur;

public class PasswordManager extends Application {
    private static final int WIDTH = 800;
    private static final int HEIGHT = 400;

    public final static String TITLE = "Password Manager";
    public final static String SAVE_EXTENSION = ".psw";

    public Stage stage;

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
        ((AppControleur)l.getController()).setMain(this);

        Scene s = new Scene(root, WIDTH, HEIGHT);
        s.getStylesheets().add(getClass().getResource("/stylesheets/style.css").toExternalForm());

        stage.setTitle(TITLE);
        stage.setScene(s);
        stage.show();

        ((AppControleur)l.getController()).finishLoad();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
