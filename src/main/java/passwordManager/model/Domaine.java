package passwordManager.model;

import javafx.beans.Observable;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import passwordManager.Crypto;
import passwordManager.Utils;

import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Nico on 01/06/2017.
 */
public class Domaine implements Externalizable, Applicable {
    private StringProperty nom;
    private StringProperty domaine;
    private StringProperty categorie;
    private StringProperty notes;
    private StringProperty iconeLocation;
    private ObservableList<Compte> comptes;

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(getNom());
        out.writeObject(getDomaine());
        out.writeObject(getCategorie());
        out.writeObject(getNotes());
        out.writeObject(getIconeLocation());
        out.writeObject(new ArrayList<>(getComptes()));
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        setNom((String) in.readObject());
        setDomaine((String) in.readObject());
        setCategorie((String) in.readObject());
        setNotes((String) in.readObject());
        setIconeLocation((String) in.readObject());
        setComptes(FXCollections.observableArrayList((ArrayList<Compte>) in.readObject()));
    }

    public Domaine() {
        this("");
    }
    public Domaine(String nom) {
        this(nom, "", "");
    }
    public Domaine(String nom, String domaine) {
        this(nom, domaine, "");
    }
    public Domaine(String nom, String domaine, String categorie) {
        this(nom, domaine, categorie, "", "");
    }
    public Domaine(String nom, String domaine, String categorie, String notes, String iconeLocation) {
        this.nom = new SimpleStringProperty(this.nom, "nom", nom);
        this.domaine = new SimpleStringProperty(this.domaine, "domaine", domaine);
        this.categorie = new SimpleStringProperty(this.categorie, "categorie", categorie);
        this.notes = new SimpleStringProperty(this.notes, "notes", notes);
        this.iconeLocation = new SimpleStringProperty(this.iconeLocation, "iconeLocation", iconeLocation);

        this.comptes = FXCollections.observableArrayList(
                compte -> new Observable[] {
                        compte.utilisateurProperty(),
                        compte.motDePasseProperty()
                }
        );
    }
    public Domaine(Scanner scanner, int level, Crypto crypto) {
        this();

        setNom(Utils.decryptFinal(scanner.nextLine(), level, 3, crypto));
        setDomaine(Utils.decryptFinal(scanner.nextLine(), level, 3, crypto));
        setCategorie(Utils.decryptFinal(scanner.nextLine(), level, 3, crypto));
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
    public Domaine(Domaine d) {
        this(d.getNom(), d.getDomaine(), d.getCategorie(), d.getNotes(), d.getIconeLocation());
        for (Compte c : d.getComptes())
            addCompte(new Compte(c));
    }

    void write(BufferedWriter bufferedWriter, int level, Crypto crypto) throws IOException {
        String nom = (getNom().equals("") ? "null" : getNom());
        String domaine = (getDomaine().equals("") ? "null" : getDomaine());
        String categorie = (getCategorie().equals("") ? "null" : getCategorie());
        String iconeLocation = (getIconeLocation().equals("") ? "null" : getIconeLocation());

        bufferedWriter.write(Utils.encryptFinal(nom, level, 3, crypto) + "\n");
        bufferedWriter.write(Utils.encryptFinal(domaine, level, 3, crypto) + "\n");
        bufferedWriter.write(Utils.encryptFinal(categorie, level, 3, crypto) + "\n");
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

    private void addCompte(Compte c) {
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
    public StringProperty categorieProperty() {
        return categorie;
    }

    public void setCategorie(String categorie) {
        this.categorie.set(categorie);
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
    private void setComptes(ObservableList<Compte> comptes) {
        this.comptes = comptes;
    }

    public String getCategorie() {
        return categorie.get();
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
    public String toString() {
        return "Domaine{" +
                "nom=" + getNom() +
                ", domaine=" + getDomaine() +
                ", categorie=" + getCategorie() +
                ", notes=" + getNotes() +
                ", iconeLocation=" + getIconeLocation() +
                ", comptes=" + getComptes() +
                '}';
    }

    @Override
    public void appliquer(Applicable applicable) {
        if (!(applicable instanceof Domaine)) return;

        Domaine d = (Domaine) applicable;

        setNom(d.getNom());
        setDomaine(d.getDomaine());
        setCategorie(d.getCategorie());
        setNotes(d.getNotes());
        setIconeLocation(d.getIconeLocation());
    }

    @Override
    public Domaine snap() {
        Domaine d = new Domaine(getNom(), getDomaine(), getCategorie());
        d.setNotes(getNotes());
        d.setIconeLocation(getIconeLocation());
        return d;
    }
}
