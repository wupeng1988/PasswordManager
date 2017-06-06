package passwordManager.model;

import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
    public Donnees(ArrayList<Domaine> aDomaines) {
        domaines = FXCollections.observableArrayList(
                domaine -> new Observable[] { domaine.nomProperty(), domaine.domaineProperty(), domaine.iconeLocationProperty() }
        );

        if (aDomaines != null)
            domaines.addAll(aDomaines);

        encrytionLevel = 0;
        autorise = true;
        motDePasse = "";
    }

    public static Donnees read(Scanner scanner) {
        Donnees donnees = new Donnees();

        String line;
        while ((line = scanner.nextLine()) != null && (line.trim().length() == 0 || (line.trim().length() > 1 && line.substring(0, 2).equals("##")))) {} // Avancer jusqu'aux données utiles

        if (!Utils.ligneVide(line))
            donnees.encrytionLevel = Integer.parseInt(line);

        if (donnees.encrytionLevel > 0)
            donnees.setAutorise(false);

        if (donnees.encrytionLevel == 4)
            return donnees;

        int nbDomaines = 0;
        if ((line = scanner.nextLine()) != null && !Utils.ligneVide(line))
            nbDomaines = Integer.parseInt(line);

        while (nbDomaines-- > 0)
            donnees.addDomaine(new Domaine(scanner, donnees.getEncrytionLevel()));

        return donnees;
    }
    public void write(BufferedWriter bufferedWriter) throws IOException {
        bufferedWriter.write(encrytionLevel + "\n");
        bufferedWriter.write(getDomaines().size() + "\n");

        for (Domaine d : getDomaines())
            d.write(bufferedWriter);
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
}
