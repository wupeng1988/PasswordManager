package passwordManager.model;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import passwordManager.Crypto;
import passwordManager.Utils;

import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

/**
 * Nico on 01/06/2017.
 */
public class Compte implements Externalizable, Applicable {
    public final static String DATE_FORMAT = "yyyy/MM/dd";

    private StringProperty utilisateur;
    private StringProperty motDePasse;
    private StringProperty notes;
    private ObjectProperty<LocalDate> dateCreation;

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(getUtilisateur());
        out.writeObject(getMotDePasse());
        out.writeObject(getNotes());
        out.writeObject(getDateCreation());
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        setUtilisateur((String) in.readObject());
        setMotDePasse((String) in.readObject());
        setNotes((String) in.readObject());
        setDateCreation((LocalDate) in.readObject());
    }

    public Compte() {
        this("", "");
    }
    public Compte(Scanner scanner, int level, Crypto crypto) {
        this("", "");

        setUtilisateur(Utils.decryptFinal(scanner.nextLine(), level, 2, crypto));
        setMotDePasse(Utils.decryptFinal(scanner.nextLine(), level, 1, crypto));

        String date = Utils.decryptFinal(scanner.nextLine(), level, 1, crypto);
        setDateCreation(date.equals("") ? null : LocalDate.parse(date, DateTimeFormatter.ofPattern(DATE_FORMAT)));

        int nbLignesNotes = Integer.parseInt(Utils.decryptFinal(scanner.nextLine(), level, 4, crypto));

        while (nbLignesNotes-- > 0)
            setNotes(getNotes() + Utils.decryptFinal(scanner.nextLine(), level, 2, crypto) + "\n");
    }
    public Compte(String utilisateur, String motDePasse) {
        this(utilisateur, motDePasse, "", LocalDate.now());
    }
    public Compte(String utilisateur, String motDePasse, String notes, LocalDate dateCreation) {
        this.utilisateur = new SimpleStringProperty(this.utilisateur, "utilisateur", utilisateur);
        this.motDePasse = new SimpleStringProperty(this.motDePasse, "motDePasse", motDePasse);
        this.notes = new SimpleStringProperty(this.notes, "notes", notes);
        this.dateCreation = new SimpleObjectProperty<>(this.dateCreation, "dateCreation", dateCreation);
    }
    public Compte(Compte c) {
        this(c.getUtilisateur(), c.getMotDePasse(), c.getNotes(), c.getDateCreation());
    }

    void write(BufferedWriter bufferedWriter, int level, Crypto crypto) throws IOException {
        bufferedWriter.write("\t" + Utils.encryptFinal(getUtilisateur(), level, 2, crypto) + "\n");
        bufferedWriter.write("\t" + Utils.encryptFinal(getMotDePasse(), level, 1, crypto) + "\n");
        bufferedWriter.write("\t" + Utils.encryptFinal(getDateCreationFormatted(), level, 1, crypto) + "\n");

        if (!Utils.ligneVide(getNotes().trim())) {
            String notesSplited[] = getNotes().trim().split("\n");
            bufferedWriter.write("\t" + Utils.encryptFinal(notesSplited.length, level, 4, crypto) + "\n");
            for (String s : notesSplited)
                bufferedWriter.write("\t" + Utils.encryptFinal(s, level, 2, crypto) + "\n");
        } else {
            bufferedWriter.write("\t" + Utils.encryptFinal(0, level, 4, crypto) + "\n");
        }
    }

    public StringProperty utilisateurProperty() {
        return utilisateur;
    }
    public StringProperty motDePasseProperty() {
        return motDePasse;
    }
    public StringProperty notesProperty() {
        return notes;
    }
    public ObjectProperty<LocalDate> dateCreationProperty() {
        return dateCreation;
    }

    public void setDateCreationFormatted(String date) {
        this.dateCreation.set(LocalDate.parse(date, DateTimeFormatter.ofPattern(DATE_FORMAT)));
    }
    private void setDateCreation(LocalDate dateCreation) {
        this.dateCreation.set(dateCreation);
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

    public String getDateCreationFormatted() {
        if (dateCreation.get() == null) return "????/??/??";
        return dateCreation.get().format(DateTimeFormatter.ofPattern(DATE_FORMAT));
    }
    public LocalDate getDateCreation() {
        return dateCreation.get();
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
    public String toString() {
        return "Compte{" +
                "utilisateur=" + getUtilisateur() +
                ", motDePasse=" + getMotDePasse() +
                ", notes=" + getNotes() +
                ", dateCreation=" + getDateCreationFormatted() +
                '}';
    }

    @Override
    public void appliquer(Applicable applicable) {
        if (!(applicable instanceof Compte)) return;

        Compte c = (Compte) applicable;

        setUtilisateur(c.getUtilisateur());
        setMotDePasse(c.getMotDePasse());
        setDateCreation(c.getDateCreation());
        setNotes(c.getNotes());
    }

    @Override
    public Compte snap() {
        Compte c = new Compte(getUtilisateur(), getMotDePasse());
        c.setNotes(getNotes());
        c.setDateCreation(getDateCreation());
        return c;
    }
}
