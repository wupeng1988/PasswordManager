package passwordManager.cellStuff;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import passwordManager.ImageManager;
import passwordManager.model.Domaine;

/**
 * Nico on 05/06/2017.
 * From https://www.billmann.de/2013/07/03/javafx-custom-listcell/
 */
public class ListViewCell extends ListCell<Domaine> {
    private final static String CLASS = "listViewCell";
    private final static String CLASS_NOM = "nom";
    private final static String CLASS_DOMAINE = "domaine";

    private ImageManager im;

    private GridPane gridPane = new GridPane();
    private ImageView icone = new ImageView();
    private Label nom = new Label();
    private Label domaine = new Label();

    public ListViewCell(ImageManager im) {
        this.im = im;

        configureGridPane();
        configureIcone();
        configureNom();
        configureDomaine();
        ajoutControlsGridPane();
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
            icone.setImage(im.getImage(d.getIconeLocation()));
        }

        if (icone.getImage() == null)
            icone.setImage(im.getImage("question.png", true));

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
