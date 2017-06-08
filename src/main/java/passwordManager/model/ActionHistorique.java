package passwordManager.model;

/**
 * Nico on 08/06/2017.
 */
public class ActionHistorique {
    public enum Action {
        AJOUT, MODIFICATION, SUPPRESSION
    }

    public enum Type {
        DOMAINE, COMPTE
    }

    Object avant;
    Object apres;
    Applicable ref;
    Action action;
    Type type;

    public ActionHistorique(Object avant, Object apres, Applicable ref, Action action, Type type) {
        this.avant = avant;
        this.apres = apres;
        this.ref = ref;
        this.action = action;
        this.type = type;
    }
}
