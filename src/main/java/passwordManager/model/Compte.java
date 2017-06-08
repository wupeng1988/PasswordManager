package passwordManager.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import passwordManager.Crypto;
import passwordManager.Utils;

import java.io.*;
import java.util.Scanner;

/**
 * Nico on 01/06/2017.
 */
public class Compte implements Externalizable {
    private StringProperty utilisateur;
    private StringProperty motDePasse;
    private StringProperty notes;

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(getUtilisateur());
        out.writeObject(getMotDePasse());
        out.writeObject(getNotes());
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        setUtilisateur((String)in.readObject());
        setMotDePasse((String)in.readObject());
        setNotes((String)in.readObject());
    }

    public Compte() {
        this("", "");
    }
    public Compte(Scanner scanner, int level, Crypto crypto) {
        this("", "");

        setUtilisateur(Utils.decryptFinal(scanner.nextLine(), level, 2, crypto));
        setMotDePasse(Utils.decryptFinal(scanner.nextLine(), level, 1, crypto));

        int nbLignesNotes = Integer.parseInt(Utils.decryptFinal(scanner.nextLine(), level, 4, crypto));

        while (nbLignesNotes-- > 0)
            setNotes(getNotes() + Utils.decryptFinal(scanner.nextLine(), level, 2, crypto) + "\n");
    }
    public Compte(String utilisateur, String motDePasse) {
        this.utilisateur = new SimpleStringProperty(utilisateur);
        this.motDePasse = new SimpleStringProperty(motDePasse);
        this.notes = new SimpleStringProperty("");
    }

    void write(BufferedWriter bufferedWriter, int level, Crypto crypto) throws IOException {
        bufferedWriter.write("\t" + Utils.encryptFinal(getUtilisateur(), level, 2, crypto) + "\n");
        bufferedWriter.write("\t" + Utils.encryptFinal(getMotDePasse(), level, 1, crypto) + "\n");

        if (!Utils.ligneVide(getNotes().trim())) {
            String notesSplited[] = getNotes().trim().split("\n");
            bufferedWriter.write("\t" + Utils.encryptFinal(notesSplited.length, level, 4, crypto) + "\n");
            for (String s : notesSplited)
                bufferedWriter.write("\t" + Utils.encryptFinal(s, level, 2, crypto) + "\n");
        } else {
            bufferedWriter.write("\t" + Utils.encryptFinal(0, level, 4, crypto) + "\n");
        }
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
        if (o == null || getClass() != o.getClass()) return false;

        Compte compte = (Compte) o;

        return getUtilisateur().equals(compte.getUtilisateur()) && getMotDePasse().equals(compte.getMotDePasse());
    }

    @Override
    public String toString() {
        return "Compte{" +
                "utilisateur=" + utilisateur +
                ", motDePasse=" + motDePasse +
                '}';
    }
}
