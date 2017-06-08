package passwordManager.cellStuff;

import javafx.collections.ObservableList;
import javafx.scene.control.TableRow;
import javafx.scene.input.*;
import passwordManager.model.Compte;

import java.util.Collections;

/**
 * Nico on 08/06/2017.
 */
public class TableViewRow extends TableRow<Compte> {
    private static final DataFormat customFormat = new DataFormat("passwordmanager.model.Compte");

    public TableViewRow() {
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
                int draggedIdx = items.indexOf(db.getContent(customFormat));
                int thisIdx = items.indexOf(getItem());

                Collections.swap(items, draggedIdx, thisIdx);

                success = true;
            }
            event.setDropCompleted(success);

            event.consume();
        });

        setOnDragDone(DragEvent::consume);
    }
}
