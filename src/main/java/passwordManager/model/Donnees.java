package passwordManager.model;

import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;

/**
 * Nico on 05/06/2017.
 */
public class Donnees {
    private ObservableList<Domaine> domaines;

    public Donnees() {
        this(null);
    }
    public Donnees(ArrayList<Domaine> aDomaines) {
        domaines = FXCollections.observableArrayList(
                domaine -> new Observable[] { domaine.nomProperty(), domaine.domaineProperty(), domaine.iconeLocationProperty() }
        );

        if (aDomaines != null)
            domaines.addAll(aDomaines);
    }

    public ObservableList<Domaine> getDomaines() {
        return domaines;
    }

    public void addDomaine(Domaine d) {
        if (!domaines.contains(d)) domaines.add(d);
    }
}
