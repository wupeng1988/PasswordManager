package passwordManager.model;

/**
 * Nico on 08/06/2017.
 */
public interface Applicable {
    void appliquer(Applicable applicable); // appliquer un objet à lui même
    Applicable snap(); // prendre un "snap" de lui: une copie
}
