package passwordManager;

import passwordManager.model.Compte;
import passwordManager.model.Domaine;
import passwordManager.model.Donnees;

import java.io.*;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Scanner;

/**
 * Nico on 05/06/2017.
 */
public class Utils {
    public static void writeSaveData(Donnees donnees, String location) {
        File f = new File(location);

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(f))) {
            for (Domaine d : donnees.getDomaines()) {
                bw.write(d.getNom() + ";" + d.getDomaine() + ";" + d.getIconeLocation() + ";" + d.getComptes().size() + ";" + d.getNotes() + "\n");
                for (Compte c : d.getComptes()) {
                    bw.write("\t" + c.getUtilisateur() + "//" + c.getMotDePasse() + "//" + c.getNotes() + "\n");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static Donnees readSavedData(String location) {
        ArrayList<Domaine> domaines = new ArrayList<>();
        String line, parts[];
        int nbEntrees;
        Domaine dtmp;
        Compte ctmp;

        File f = new File(location);
        if (!f.exists() || !f.isFile())
            return new Donnees();

        try (Scanner s = new Scanner(f)) {
            while ((line = s.nextLine()) != null && line.charAt(0) == '#') {} // Avancer jusqu'aux données utiles

            while (line != null) {
                parts = line.split(";", 5); // DomaineNom;nbEntrees
                dtmp = new Domaine(parts[0]);
                dtmp.setDomaine(parts[1]);
                dtmp.setNotes(parts[4]);
                dtmp.setIconeLocation(parts[2]);
                domaines.add(dtmp);
                nbEntrees = Integer.parseInt(parts[3]);

                while (nbEntrees-- > 0 && (line = s.nextLine()) != null) {
                    parts = line.split("//", 3); // \tuser//psw
                    ctmp = new Compte(parts[0].trim(), parts[1]);
                    ctmp.setNotes(parts[2]);
                    dtmp.addCompte(ctmp);
                }

                if (line != null) line = s.nextLine();
            }
        }
        catch (NoSuchElementException ignored) {}
        catch (IOException io) {
            io.printStackTrace();
        }

        return new Donnees(domaines);
    }
}
