package passwordManager.model;

import javafx.beans.Observable;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import passwordManager.Utils;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Scanner;

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
    public Domaine(Scanner scanner, int level) {
        this("");

        String line;
        if ((line = scanner.nextLine()) != null && !Utils.ligneVide(line))
            setNom(line);

        if ((line = scanner.nextLine()) != null && !Utils.ligneVide(line))
            setDomaine(line);

        if ((line = scanner.nextLine()) != null && !Utils.ligneVide(line))
            setIconeLocation(line);

        int nbLignesNotes = 0;
        if ((line = scanner.nextLine()) != null && !Utils.ligneVide(line))
            nbLignesNotes = Integer.parseInt(line);

        while (nbLignesNotes-- > 0) {
            if ((line = scanner.nextLine()) != null && !Utils.ligneVide(line))
                setNotes(getNotes() + line + "\n");
            else
                break;
        }

        int nbComptes = 0;
        if ((line = scanner.nextLine()) != null && !Utils.ligneVide(line))
            nbComptes = Integer.parseInt(line);

        while (nbComptes-- > 0)
            if (level < 3)
                addCompte(new Compte(scanner, level));
            else
                new Compte(scanner, level);
    }

    void write(BufferedWriter bufferedWriter) throws IOException {
        bufferedWriter.write((getNom().equals("") ? "null" : getNom()) + "\n");
        bufferedWriter.write((getDomaine().equals("") ? "null" : getDomaine()) + "\n");
        bufferedWriter.write((getIconeLocation().equals("") ? "null" : getIconeLocation()) + "\n");

        if (!Utils.ligneVide(getNotes().trim())) {
            String notesSplited[] = getNotes().trim().split("\n");
            bufferedWriter.write(notesSplited.length + "\n");
            bufferedWriter.write(getNotes().trim() + "\n");
        } else {
            bufferedWriter.write("0\n");
        }

        bufferedWriter.write(getComptes().size() + "\n");
        for (Compte c : getComptes())
            c.write(bufferedWriter);
    }

    public void addCompte(Compte c) {
        if (!this.comptes.contains(c)) this.comptes.add(c);
    }

    public StringProperty domaineProperty() {
        return domaine;
    }
    public StringProperty nomProperty() {
        return nom;
    }
    public StringProperty notesProperty() {
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
