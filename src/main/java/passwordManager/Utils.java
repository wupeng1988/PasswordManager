package passwordManager;

import passwordManager.model.Donnees;

import java.io.*;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Scanner;

/**
 * Nico on 05/06/2017.
 */
public class Utils {
    public static void writeSaveData(Donnees donnees, String location, Crypto crypto) {
        File f = new File(location);

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(f))) {
            donnees.write(bw, crypto);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static Donnees readSavedData(File f, Crypto crypto) {
        Donnees donnees = null;

        if (!f.exists() || !f.isFile())
            return null;

        try (Scanner s = new Scanner(f)) {
            donnees = Donnees.read(s, crypto);
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
    public static String decryptFinal(String line, int level, int levelRequis, Crypto crypto) {
        String un = "";
        if (!Utils.ligneVide(line)) {
            if (level >= levelRequis && crypto != null)
                un = crypto.decrypt(line.trim());
            else if (level < levelRequis)
                un = line.trim();
        }
        return un;
    }
    public static String encryptFinal(String line, int level, int levelRequis, Crypto crypto) {
        String un = "";
        if (line != null && line.trim().length() > 0) {
            if (level >= levelRequis && crypto != null)
                un = crypto.encrypt(line.trim());
            else if (level < levelRequis)
                un = line.trim();
        }
        return un;
    }
    public static String encryptFinal(int data, int level, int levelRequis, Crypto crypto) {
        return encryptFinal(String.valueOf(data), level, levelRequis, crypto);
    }

    static ArrayList<String> getFichiersDansDossier(String chemin, String extension) {
        return getFichiersDansDossier(new File(chemin), extension);
    }
    private static ArrayList<String> getFichiersDansDossier(File dossier, String extension) {
        ArrayList<String> fichiers = new ArrayList<>();

        if (dossier.exists() && dossier.isDirectory())
            for (File f : dossier.listFiles((dir, name) -> name.endsWith(extension)))
                fichiers.add(f.getAbsolutePath());

        return fichiers;
    }
}