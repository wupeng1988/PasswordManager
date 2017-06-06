package passwordManager.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import passwordManager.Utils;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Scanner;

/**
 * Nico on 01/06/2017.
 */
public class Compte {
    private StringProperty utilisateur;
    private StringProperty motDePasse;
    private StringProperty notes;

    public Compte(Scanner scanner, int level) {
        this("", "");

        String line;
        if ((line = scanner.nextLine()) != null && !Utils.ligneVide(line) && level < 2)
            setUtilisateur(line.trim());

        if ((line = scanner.nextLine()) != null && !Utils.ligneVide(line) && level < 1)
            setMotDePasse(line.trim());

        int nbLignesNotes = 0;
        if ((line = scanner.nextLine()) != null && !Utils.ligneVide(line))
            nbLignesNotes = Integer.parseInt(line.trim());

        while (nbLignesNotes-- > 0) {
            if ((line = scanner.nextLine()) != null && !Utils.ligneVide(line))
                setNotes(getNotes() + line.trim() + "\n");
            else
                break;
        }
    }
    public Compte(String utilisateur, String motDePasse) {
        this.utilisateur = new SimpleStringProperty(utilisateur);
        this.motDePasse = new SimpleStringProperty(motDePasse);
        this.notes = new SimpleStringProperty("");
    }

    void write(BufferedWriter bufferedWriter) throws IOException {
        bufferedWriter.write("\t" + getUtilisateur() + "\n");
        bufferedWriter.write("\t" + getMotDePasse() + "\n");

        if (!Utils.ligneVide(getNotes().trim())) {
            String notesSplited[] = getNotes().trim().split("\n");
            bufferedWriter.write("\t" + notesSplited.length + "\n");
            for (String s : notesSplited)
                bufferedWriter.write("\t" + s + "\n");
        } else {
            bufferedWriter.write("\t0\n");
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
