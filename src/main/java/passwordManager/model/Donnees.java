package passwordManager.model;

import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import passwordManager.Crypto;
import passwordManager.Utils;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Nico on 05/06/2017.
 */
public class Donnees {
    private int encrytionLevel;
    private ObservableList<Domaine> domaines;

    private String motDePasse;
    private boolean autorise;

    public Donnees() {
        this(null);
    }
    private Donnees(ArrayList<Domaine> aDomaines) {
        domaines = FXCollections.observableArrayList(
                domaine -> new Observable[] {
                        domaine.nomProperty(),
                        domaine.domaineProperty(),
                        domaine.iconeLocationProperty(),
                        domaine.iconeLocationProperty()
                }
        );

        if (aDomaines != null)
            domaines.addAll(aDomaines);

        encrytionLevel = 0;
        autorise = true;
        motDePasse = "";
    }

    public static Donnees read(Scanner scanner, Crypto crypto) {
        Donnees donnees = new Donnees();

        String line;
        while ((line = scanner.nextLine()) != null && (line.trim().length() == 0 || (line.trim().length() > 1 && line.substring(0, 2).equals("##")))); // Avancer jusqu'aux données utiles

        if (!Utils.ligneVide(line))
            donnees.encrytionLevel = Integer.parseInt(line);

        if (donnees.encrytionLevel > 0 && crypto == null)
            donnees.setAutorise(false);

        if (donnees.encrytionLevel == 3 && crypto == null)
            return donnees;

        int nbDomaines = Integer.parseInt(Utils.decryptFinal(scanner.nextLine(), donnees.getEncrytionLevel(), 3, crypto));

        while (nbDomaines-- > 0)
            donnees.addDomaine(new Domaine(scanner, donnees.getEncrytionLevel(), crypto));

        return donnees;
    }
    public void write(BufferedWriter bufferedWriter, Crypto crypto) throws IOException {
        bufferedWriter.write(encrytionLevel + "\n");
        bufferedWriter.write(Utils.encryptFinal(getDomaines().size(), encrytionLevel, 3, crypto) + "\n");

        for (Domaine d : getDomaines())
            d.write(bufferedWriter, encrytionLevel, crypto);
    }

    public void setMotDePasse(String motDePasse) {
        this.motDePasse = motDePasse;
    }
    public void setEncrytionLevel(int encrytionLevel) {
        this.encrytionLevel = encrytionLevel;
    }
    public void setAutorise(boolean autorise) {
        this.autorise = autorise;
    }

    public String getMotDePasse() {
        return motDePasse;
    }
    public int getEncrytionLevel() {
        return encrytionLevel;
    }
    public boolean isAutorise() {
        return autorise;
    }

    public ObservableList<Domaine> getDomaines() {
        return domaines;
    }
    public void addDomaine(Domaine d) {
        if (!domaines.contains(d)) domaines.add(d);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        Donnees donnees = (Donnees) o;

        if (getEncrytionLevel() != donnees.getEncrytionLevel()) return false;
        if (isAutorise() != donnees.isAutorise()) return false;
        if (!(donnees.getDomaines().size() == getDomaines().size())) return false;
        for (Domaine d : getDomaines())
            for (Domaine od : donnees.getDomaines())
                if (!od.equals(d)) return false;
        return getMotDePasse() != null ? getMotDePasse().equals(donnees.getMotDePasse()) : donnees.getMotDePasse() == null;
    }

    @Override
    public String toString() {
        return "Donnees{" +
                "encrytionLevel=" + getEncrytionLevel() +
                ", domaines=" + getDomaines() +
                ", motDePasse='" + getMotDePasse() + '\'' +
                ", autorise=" + isAutorise() +
                '}';
    }
}
