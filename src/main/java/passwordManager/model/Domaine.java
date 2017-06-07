package passwordManager.model;

import javafx.beans.Observable;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import passwordManager.Crypto;
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
    public Domaine(Scanner scanner, int level, Crypto crypto) {
        this("");

        setNom(Utils.decryptFinal(scanner.nextLine(), level, 3, crypto));
        setDomaine(Utils.decryptFinal(scanner.nextLine(), level, 3, crypto));
        setIconeLocation(Utils.decryptFinal(scanner.nextLine(), level, 3, crypto));

        int nbLignesNotes = Integer.parseInt(Utils.decryptFinal(scanner.nextLine(), level, 4, crypto));

        while (nbLignesNotes-- > 0)
            setNotes(getNotes() + Utils.decryptFinal(scanner.nextLine(), level, 3, crypto) + "\n");

        int nbComptes = Integer.parseInt(Utils.decryptFinal(scanner.nextLine(), level, 4, crypto));

        while (nbComptes-- > 0)
            if (level < 3 || crypto != null)
                addCompte(new Compte(scanner, level, crypto));
            else
                new Compte(scanner, level, null);
    }

    void write(BufferedWriter bufferedWriter, int level, Crypto crypto) throws IOException {
        String nom = (getNom().equals("") ? "null" : getNom());
        String domaine = (getDomaine().equals("") ? "null" : getDomaine());
        String iconeLocation = (getIconeLocation().equals("") ? "null" : getIconeLocation());

        bufferedWriter.write(Utils.encryptFinal(nom, level, 3, crypto) + "\n");
        bufferedWriter.write(Utils.encryptFinal(domaine, level, 3, crypto) + "\n");
        bufferedWriter.write(Utils.encryptFinal(iconeLocation, level, 3, crypto) + "\n");

        if (!Utils.ligneVide(getNotes().trim())) {
            String notesSplited[] = getNotes().trim().split("\n");
            bufferedWriter.write(Utils.encryptFinal(notesSplited.length, level, 4, crypto) + "\n");
            for (String s : notesSplited)
                bufferedWriter.write(Utils.encryptFinal(s, level, 3, crypto) + "\n");
        } else {
            bufferedWriter.write(Utils.encryptFinal(0, level, 4, crypto) + "\n");
        }

        bufferedWriter.write(Utils.encryptFinal(getComptes().size(), level, 4, crypto) + "\n");
        for (Compte c : getComptes())
            c.write(bufferedWriter, level, crypto);
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
