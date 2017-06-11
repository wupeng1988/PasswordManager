package passwordManager;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Nico on 05/06/2017.
 */
public class ImageManager {
    public static final String ICONE_ADD =          PasswordManager.DOSSIER_IMAGES + "add.png";
    public static final String ICONE_REMOVE =       PasswordManager.DOSSIER_IMAGES + "remove.png";
    public static final String ICONE_INFO =         PasswordManager.DOSSIER_IMAGES + "information.png";
    public static final String ICONE_PLACEHOLDER =  PasswordManager.DOSSIER_IMAGES + "question-sign.png";
    public static final String ICONE_UP =           PasswordManager.DOSSIER_IMAGES + "arrow-up.png";
    public static final String ICONE_DOWN =         PasswordManager.DOSSIER_IMAGES + "arrow-down.png";
    public static final String ICONE_COPY =         PasswordManager.DOSSIER_IMAGES + "copy.png";
    public static final String ICONE_REFRESH =      PasswordManager.DOSSIER_IMAGES + "refresh.png";
    public static final String ICONE_SHARE =        PasswordManager.DOSSIER_IMAGES + "share.png";
    public static final String ICONE_EDITION =      PasswordManager.DOSSIER_IMAGES + "edition.png";
    public static final String ICONE_UPLOAD =       PasswordManager.DOSSIER_IMAGES + "upload.png";
    public static final String ICONE_DOUBLE_UP =    PasswordManager.DOSSIER_IMAGES + "double-up.png";
    public static final String ICONE_DOUBLE_DOWN =  PasswordManager.DOSSIER_IMAGES + "double-down.png";
    public static final String ICONE_RECT_CLOSE =   PasswordManager.DOSSIER_IMAGES + "rect-close.png";

    private static final String CHEMIN_ICONES = "./icones";

    private HashMap<String, Image> cache = new HashMap<>();
    private ArrayList<String> userImages = new ArrayList<>();

    public ImageManager() {
        File f = new File(CHEMIN_ICONES);
        if (!f.exists()) {
            if (!f.mkdir()) System.err.println("mkdir failed! check write perm");
            return;
        }

        ArrayList<String> arrayList = Utils.getFichiersDansDossier(CHEMIN_ICONES, ".png");
        userImages.addAll(arrayList);
    }

    public Image getImage(String location) {
        return getImage(location, false);
    }
    public Image getImage(String location, boolean resource) {
        Image i = cache.get(location);

        if (i == null) {
            try {
                File f;
                if (!resource) {
                    f = new File(location);
                    if (f.exists() && f.isFile()) {
                        i = new Image(f.toURI().toURL().toExternalForm());
                        if (i.getHeight() > 512 || i.getWidth() > 512) i = null;
                        userImages.add(f.getAbsolutePath());
                    }
                } else {
                    i = new Image(getClass().getResourceAsStream(location));
                }

                if (i != null)
                    cache.put(location, i);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return i;
    }
    public ImageView constructImageViewFrom(String location, int width, int height) {
        return constructImageViewFrom(location, width, height, false);
    }
    public ImageView constructImageViewFrom(String location, int width, int height, boolean resource) {
        Image i = getImage(location, resource);
        ImageView iv = new ImageView(i);
        iv.setFitWidth(width);
        iv.setFitHeight(height);
        return iv;
    }

    public ArrayList<String> getUserImages() {
        return userImages;
    }
}
