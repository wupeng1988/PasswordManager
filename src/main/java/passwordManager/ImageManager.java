package passwordManager;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.File;
import java.util.HashMap;

/**
 * Nico on 05/06/2017.
 */
public class ImageManager {
    private HashMap<String, Image> cache = new HashMap<>();

    public ImageManager() {}

    public Image getImage(String location) {
        return getImage(location, false);
    }
    public Image getImage(String location, boolean resource) {
        Image i = cache.get(location);
        if (i == null) {
            try {
                if (!resource)
                    i = new Image(new File(location).toURI().toURL().toExternalForm());
                else
                    i = new Image(getClass().getResourceAsStream("/images/" + location));
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
