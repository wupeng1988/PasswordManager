package passwordManager;

import passwordManager.model.Donnees;

import java.io.*;
import java.util.NoSuchElementException;
import java.util.Scanner;

/**
 * Nico on 05/06/2017.
 */
public class Utils {
    public static void writeSaveData(Donnees donnees, String location) {
        File f = new File(location);

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(f))) {
            donnees.write(bw);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static Donnees readSavedData(String location) {
        Donnees donnees = null;

        File f = new File(location);

        if (!f.exists() || !f.isFile())
            return null;

        try (Scanner s = new Scanner(f)) {
            donnees = Donnees.read(s);
        }
        catch (NoSuchElementException ignored) {}
        catch (IOException io) {
            io.printStackTrace();
        }

        return donnees;
    }

    public static boolean ligneVide(String line) {
        return line == null || line.trim().length() == 0 || line.equals("null");
    }
}
