package passwordManager.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Nico on 01/06/2017.
 */
public class Compte {
    private StringProperty utilisateur;
    private StringProperty motDePasse;
    private StringProperty notes;

    public Compte(String utilisateur, String motDePasse) {
        this.utilisateur = new SimpleStringProperty(utilisateur);
        this.motDePasse = new SimpleStringProperty(motDePasse);
        this.notes = new SimpleStringProperty("");
    }

    StringProperty utilisateurProperty() {
        return utilisateur;
    }
    StringProperty motDePasseProperty() {
        return motDePasse;
    }
    StringProperty notesProperty() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes.set(notes);
    }
    public void setUtilisateur(String utilisateur) {
        this.utilisateur.set(utilisateur);
    }
    public void setMotDePasse(String motDePasse) {
        this.motDePasse.set(motDePasse);
    }

    public String getNotes() {
        return notes.get();
    }
    public String getUtilisateur() {
        return utilisateur.get();
    }
    public String getMotDePasse() {
        return motDePasse.get();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Compte compte = (Compte) o;

        return getUtilisateur().equals(compte.getUtilisateur()) && getMotDePasse().equals(compte.getMotDePasse());
    }

    @Override
    public int hashCode() {
        int result = getUtilisateur().hashCode();
        result = 31 * result + getMotDePasse().hashCode();
        return result;
    }
}
