package passwordManager.cellStuff;

import javafx.collections.ObservableList;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableRow;
import javafx.scene.input.*;
import passwordManager.controleur.App;
import passwordManager.model.ActionHistorique;
import passwordManager.model.Compte;
import passwordManager.model.Historique;

import java.util.Collections;

/**
 * Nico on 08/06/2017.
 */
public class TableViewRow extends TableRow<Compte> {
    private static final DataFormat customFormat = new DataFormat("passwordmanager.model.Compte");

    private App app;

    private ContextMenu contextMenu = new ContextMenu();
    private MenuItem modifierItem = new MenuItem();
    private MenuItem supprimerItem = new MenuItem();

    public TableViewRow(App app) {
        this.app = app;

        configureDragNDrop();
        configureMenu();
    }

    private void configureMenu() {
        modifierItem.setOnAction(event -> app.modificationCompte());
        supprimerItem.setOnAction(event -> app.suppressionCompte());
        contextMenu.getItems().addAll(modifierItem, supprimerItem);
    }

    private void configureDragNDrop() {
        TableViewRow thisCell = this;

        setOnDragDetected(event -> {
            if (getItem() == null) {
                return;
            }

            Dragboard dragboard = startDragAndDrop(TransferMode.MOVE);
            ClipboardContent content = new ClipboardContent();
            content.put(customFormat, getItem());
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
                ObservableList<Compte> items = getTableView().getItems();
                Compte dragged = (Compte) db.getContent(customFormat);
                int draggedIdx = items.indexOf(dragged);
                int thisIdx = items.indexOf(getItem());

                Historique h = app.getDonneesActives().getHistorique();
                h.ajoutAction(ActionHistorique.deplacementCompte(draggedIdx, thisIdx, app.getDomaineSelectionne()));

                success = true;
            }
            event.setDropCompleted(success);

            event.consume();
        });

        setOnDragDone(DragEvent::consume);
    }

    @Override
    protected void updateItem(Compte item, boolean empty) {
        super.updateItem(item, empty);

        if (empty) {
            setContextMenu(null);
            setOnMouseClicked(null);
        } else {
            modifierItem.setText("Modifier " + getItem().getUtilisateur());
            supprimerItem.setText("Supprimer " + getItem().getUtilisateur());
            setContextMenu(contextMenu);
            setOnMouseClicked(event -> {
                if (event.getClickCount() == 2)
                    app.modificationCompte();
            });
        }
    }
}
