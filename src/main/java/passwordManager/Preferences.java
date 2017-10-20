package passwordManager;

import java.io.*;
import java.util.*;

/**
 * Nico on 08/06/2017.
 */
public class Preferences {
    private final static String FILENAME = "config.properties";

    public final static String PROP_DERNIER_FICHIER_CHEMIN = "dernierFichier";
    public final static String PROP_DERNIER_FICHIER_DRIVE = "dernierFichierDrive";
    public final static String PROP_DEFAUT_LARGEUR = "defautLargeur";
    public final static String PROP_DEFAUT_HAUTEUR = "defautHauteur";
    public final static String PROP_CHARGER_DERNIER_FICHIER = "chargerDernierFichier";
    public final static String PROP_BACKUP_AUTO = "backupAuto";
    public final static String PROP_DOSSIER_BACKUP_AUTO = "dossierBackup";
    public final static String PROP_MAX_HISTORIQUE = "maxHistorique";

    private Properties properties = new Properties();
    private HashMap<String, String> defautProprietes = new HashMap<String, String>() {{
        put(PROP_DERNIER_FICHIER_CHEMIN, "");
        put(PROP_DERNIER_FICHIER_DRIVE, "false");
        put(PROP_DEFAUT_LARGEUR, "1000");
        put(PROP_DEFAUT_HAUTEUR, "500");
        put(PROP_CHARGER_DERNIER_FICHIER, "true");
        put(PROP_BACKUP_AUTO, "5");
        put(PROP_DOSSIER_BACKUP_AUTO, "./backups");
        put(PROP_MAX_HISTORIQUE, "10");
    }};
    private HashMap<String, String> proprietes = new HashMap<>();

    Preferences() {
        properties.putAll(defautProprietes);

        load();
        write();
    }

    public String getDefautPropriete(String name) {
        return defautProprietes.get(name);
    }
    public String getPropriete(String name) {
        return proprietes.get(name);
    }
    public void setPropriete(String name, String value) {
        proprietes.put(name, value);
    }

    private void load() {
        createIfNeeded();

        try (InputStream input = new FileInputStream(FILENAME)) {
            properties.load(input);
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        properties.forEach((k, v) -> proprietes.put((String) k, v.toString()));
    }
    void write() {
        proprietes.forEach((k, v) -> properties.setProperty(k, v));

        try (OutputStream output = new FileOutputStream(FILENAME)) {
            properties.store(output, null);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void createIfNeeded() {
        File f = new File(FILENAME);

        try {
            if (!f.exists())
                if (!f.createNewFile())
                    System.err.println("File cannot create !");
        } catch (IOException io) {
            io.printStackTrace();
        }
    }
}
