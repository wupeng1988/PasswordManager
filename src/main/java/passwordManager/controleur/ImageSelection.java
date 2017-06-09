package passwordManager.controleur;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import javafx.util.Callback;
import org.controlsfx.control.GridCell;
import org.controlsfx.control.GridView;
import passwordManager.ImageManager;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Nico on 08/06/2017.
 */
public class ImageSelection implements Initializable {
    private InfosDomaine infosDomaine;
    private ImageManager imageManager;

    @FXML private BorderPane root;

    @FXML private ImageView ivIcone;
    @FXML private Button bAjout;
    @FXML private Button bSuppression;

    @FXML private Button bOk;
    @FXML private Button bAnnuler;

    private String iconeLocation;

    @Override
    public void initialize(URL location, ResourceBundle resources) {}

    private void init() {
        GridView<String> gridView = new GridView<>(FXCollections.observableArrayList(imageManager.getUserImages()));
        gridView.setCellFactory(new Callback<GridView<String>, GridCell<String>>() {
            @Override
            public GridCell<String> call(GridView<String> param) {
                return new GridCell<String>() {
                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);

                        if (item == null || empty) {
                            setGraphic(null);
                            setOnMouseClicked(null);
                            setCursor(Cursor.DEFAULT);
                            setOnMouseEntered(null);
                            setOnMouseExited(null);
                        } else {
                            setGraphic(imageManager.constructImageViewFrom(item, 64, 64));
                            setOnMouseClicked(event -> setIcone(item));
                            setCursor(Cursor.HAND);
                            setOnMouseEntered(event -> setStyle("-fx-border-radius: 1; -fx-border-color: black"));
                            setOnMouseExited(event -> setStyle(""));
                        }
                    }
                };
            }
        });

        root.setCenter(gridView);
        BorderPane.setMargin(gridView, new Insets(4, 10, 4, 10));
    }

    void setApp(InfosDomaine infosDomaine, String l) {
        this.infosDomaine = infosDomaine;
        this.imageManager = infosDomaine.app.getImageManager();

        bAjout.setText(null);
        bSuppression.setText(null);

        bAjout.setTooltip(new Tooltip("Icone depuis l'ordinateur..."));
        bSuppression.setTooltip(new Tooltip("Déselectionner l'icone sélectionnée"));

        bAjout.setGraphic(imageManager.constructImageViewFrom(ImageManager.ICONE_UPLOAD, 32, 32, true));
        bSuppression.setGraphic(imageManager.constructImageViewFrom(ImageManager.ICONE_RECT_CLOSE, 32, 32, true));

        setIcone(l);
        init();
    }

    @FXML
    void ok() {
        infosDomaine.cacherImageSelection(iconeLocation);
    }
    @FXML
    void annuler() {
        infosDomaine.cacherImageSelection("");
    }

    @FXML
    private void selectionIcone() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File("."));
        fileChooser.setTitle("Choisir une icone");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Images", "*.png")
        );
        File fileSelected = fileChooser.showOpenDialog(infosDomaine.app.getPasswordManager().getStage());
        if (fileSelected != null) {
            Image i = imageManager.getImage(fileSelected.getAbsolutePath());
            if (i != null) {
                setIcone(fileSelected.getAbsolutePath());
            }
        }
    }

    private void setIcone(String s) {
        iconeLocation = s;
        ivIcone.setImage(imageManager.getImage(s));
    }

    @FXML
    private void suppressionIcone() {
        setIcone("");
    }
}
