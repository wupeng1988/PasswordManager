package passwordManager;

import java.io.*;
import java.util.*;

/**
 * Nico on 08/06/2017.
 */
public class Preferences {
    private final static String FILENAME = "config.properties";

    public final static String PROP_DERNIER_FICHIER = "dernierFichier";
    public final static String PROP_DEFAUT_LARGEUR = "defautLargeur";
    public final static String PROP_DEFAUT_HAUTEUR = "defautHauteur";

    private Properties properties = new Properties();
    private HashMap<String, Object> list = new HashMap<String, Object>() {{
        put(PROP_DERNIER_FICHIER, "");
        put(PROP_DEFAUT_LARGEUR, "1000");
        put(PROP_DEFAUT_HAUTEUR, "500");
    }};

    Preferences() {
        load();
        write();
    }

    public Object getProperty(String name) {
        return list.get(name);
    }
    public void setProperty(String name, Object value) {
        list.put(name, value);
    }

    private void load() {
        createIfNeeded();

        try (InputStream input = new FileInputStream(FILENAME)) {
            properties.load(input);
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        properties.forEach((k, v) -> list.put((String) k, v));
    }
    void write() {
        list.forEach((k, v) -> properties.setProperty(k, v.toString()));

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
                    System.err.println("Fichier impossible à créer.");
        } catch (IOException io) {
            io.printStackTrace();
        }
    }
}
