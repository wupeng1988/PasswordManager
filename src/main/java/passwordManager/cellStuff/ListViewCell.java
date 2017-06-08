package passwordManager.cellStuff;

import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.GridPane;
import passwordManager.ImageManager;
import passwordManager.controleur.App;
import passwordManager.model.Domaine;

import java.util.Collections;

/**
 * Nico on 05/06/2017.
 * From https://www.billmann.de/2013/07/03/javafx-custom-listcell/
 */
public class ListViewCell extends ListCell<Domaine> {
    private final static String CLASS = "listViewCell";
    private final static String CLASS_NOM = "nom";
    private final static String CLASS_DOMAINE = "domaine";

    private App app;
    private ImageManager imageManager;

    private static final DataFormat customFormat = new DataFormat("passwordmanager.model.Domaine");

    private GridPane gridPane = new GridPane();
    private ImageView icone = new ImageView();
    private Label nom = new Label();
    private Label domaine = new Label();

    public ListViewCell(App app) {
        this.app = app;
        this.imageManager = app.getImageManager();

        configureGridPane();
        configureIcone();
        configureNom();
        configureDomaine();
        ajoutControlsGridPane();

        ListViewCell thisCell = this;

        setOnDragDetected(event -> {
            if (getItem() == null) {
                return;
            }

            Dragboard dragboard = startDragAndDrop(TransferMode.MOVE);
            ClipboardContent content = new ClipboardContent();
            content.put(customFormat, getItem());
            if (!getItem().getIconeLocation().equals(""))
                dragboard.setDragView(imageManager.getImage(getItem().getIconeLocation()));
            dragboard.setContent(content);

            event.consume();
        });

        setOnDragOver(event -> {
            if (event.getGestureSource() != thisCell &&
                    event.getDragboard().hasContent(customFormat)) {
                event.acceptTransferModes(TransferMode.MOVE);
            }

            event.consume();
        });

        setOnDragEntered(event -> {
            if (event.getGestureSource() != thisCell &&
                    event.getDragboard().hasContent(customFormat)) {
                setOpacity(0.3);
            }
        });

        setOnDragExited(event -> {
            if (event.getGestureSource() != thisCell &&
                    event.getDragboard().hasContent(customFormat)) {
                setOpacity(1);
            }
        });

        setOnDragDropped(event -> {
            if (getItem() == null) {
                return;
            }

            Dragboard db = event.getDragboard();
            boolean success = false;

            if (db.hasContent(customFormat)) {
                ObservableList<Domaine> items = app.getDonneesActives().getDomaines();
                int draggedIdx = items.indexOf(db.getContent(customFormat));
                int thisIdx = items.indexOf(getItem());

                Collections.swap(app.getDonneesActives().getDomaines(), draggedIdx, thisIdx);

                success = true;
            }
            event.setDropCompleted(success);

            event.consume();
        });

        setOnDragDone(DragEvent::consume);
    }

    private void configureGridPane() {
        gridPane.setHgap(10);
        gridPane.setVgap(2);
        gridPane.setPadding(new Insets(0, 10, 0, 10));
    }

    private void configureNom() {
        nom.getStyleClass().add(CLASS_NOM);
    }
    private void configureDomaine() {
        domaine.getStyleClass().add(CLASS_DOMAINE);
    }
    private void configureIcone() {
        icone.setFitHeight(32);
        icone.setFitWidth(32);
    }

    private void ajoutControlsGridPane() {
        gridPane.getStyleClass().add(CLASS);
        gridPane.add(icone, 0, 0, 1, 2);
        gridPane.add(nom, 1, 0);
        gridPane.add(domaine, 1, 1);
    }

    private void ajoutContenu(Domaine d) {
        setText(null);
        if (d.getIconeLocation() != null && !d.getIconeLocation().equals("")) { // location est set
            icone.setImage(imageManager.getImage(d.getIconeLocation()));
        } else {
            icone.setImage(null);
        }

        if (icone.getImage() == null)
            icone.setImage(imageManager.getImage(ImageManager.ICONE_PLACEHOLDER, true));

        nom.setText(d.getNom());
        domaine.setText(d.getDomaine());

        setGraphic(gridPane);
    }

    private void clearContent() {
        setText(null);
        setGraphic(null);
    }

    @Override
    protected void updateItem(Domaine d, boolean empty) {
        super.updateItem(d, empty);
        if (empty) {
            clearContent();
        } else {
            ajoutContenu(d);
        }
    }
}
