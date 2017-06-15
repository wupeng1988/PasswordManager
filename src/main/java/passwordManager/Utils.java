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
    private final static String CHARSET = "UTF-8";

    public static void writeSaveData(Donnees donnees, String location, Crypto crypto) {
        try (BufferedWriter bw = new BufferedWriter(
                new OutputStreamWriter(
                    new FileOutputStream(location), CHARSET
                )
        )) {
            donnees.write(bw, crypto);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static Donnees readSavedData(File f, Crypto crypto) {
        Donnees donnees = null;

        if (!f.exists() || !f.isFile())
            return null;

        try (Scanner s = new Scanner(f, CHARSET)) {
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

        File[] files;
        if (dossier.exists() && dossier.isDirectory() && (files = dossier.listFiles((dir, name) -> name.endsWith(extension))) != null)
            for (File f : files)
                fichiers.add(f.getAbsolutePath());

        return fichiers;
    }

    public static String toLocalPath(String path) {
        String localPath = new File(".").getAbsolutePath();
        localPath = localPath.substring(0 , localPath.length() - 1);

        String toRet = path
                .replace(localPath, "." + File.separator)
                .replace("/", File.separator)
                .replace("." + File.separator, "");

        return toRet;
    }
}