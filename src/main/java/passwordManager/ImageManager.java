package passwordManager;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.File;
import java.util.HashMap;

/**
 * Nico on 05/06/2017.
 */
public class ImageManager {
    public static final String ICONE_ADD = "add.png";
    public static final String ICONE_REMOVE = "remove.png";
    public static final String ICONE_INFO = "information.png";
    public static final String ICONE_PLACEHOLDER = "question-sign.png";
    public static final String ICONE_UP = "arrow-up.png";
    public static final String ICONE_DOWN = "arrow-down.png";

    private HashMap<String, Image> cache = new HashMap<>();

    public ImageManager() {}

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
                    if (f.exists() && f.isFile())
                        i = new Image(f.toURI().toURL().toExternalForm());
                } else {
                    i = new Image(getClass().getResourceAsStream("/images/" + location));
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
}
