package passwordManager.model;

import javafx.beans.Observable;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Nico on 01/06/2017.
 */
public class Domaine {
    private StringProperty nom;
    private StringProperty domaine;
    private StringProperty notes;
    private StringProperty iconeLocation;
    private ObservableList<Compte> comptes;

    public Domaine(String nom) {
        this.nom = new SimpleStringProperty(nom);
        this.domaine = new SimpleStringProperty("");
        this.notes = new SimpleStringProperty("");
        this.iconeLocation = new SimpleStringProperty("");
        this.comptes = FXCollections.observableArrayList(
                compte -> new Observable[] { compte.utilisateurProperty(), compte.motDePasseProperty() }
        );
    }

    public void addCompte(Compte c) {
        if (!this.comptes.contains(c)) this.comptes.add(c);
    }

    StringProperty domaineProperty() {
        return domaine;
    }
    StringProperty nomProperty() {
        return nom;
    }
    StringProperty notesProperty() {
        return notes;
    }
    StringProperty iconeLocationProperty() {
        return iconeLocation;
    }

    public void setNotes(String notes) {
        this.notes.set(notes);
    }
    public void setIconeLocation(String iconeLocation) {
        this.iconeLocation.set(iconeLocation);
    }
    public void setDomaine(String domaine) {
        this.domaine.set(domaine);
    }
    public void setNom(String nom) {
        this.nom.set(nom);
    }

    public String getNotes() {
        return notes.get();
    }
    public String getIconeLocation() {
        return iconeLocation.get();
    }
    public String getDomaine() {
        return domaine.get();
    }
    public String getNom() {
        return nom.get();
    }
    public ObservableList<Compte> getComptes() {
        return comptes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Domaine domaine = (Domaine) o;

        return getNom().equals(domaine.getNom()) && getComptes().equals(domaine.getComptes());
    }

    @Override
    public int hashCode() {
        int result = getNom().hashCode();
        result = 31 * result + getComptes().hashCode();
        return result;
    }
}
