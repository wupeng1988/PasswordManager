package passwordManager.model;

import java.util.ArrayList;

/**
 * Nico on 08/06/2017.
 */
public class Historique {
    private Donnees donnees;

    private boolean saved = true;

    private ArrayList<ActionHistorique> historique = new ArrayList<>();
    private int index = -1;

    Historique(Donnees donnees) {
        this.donnees = donnees;
    }

    public void ajoutAction(ActionHistorique actionHistorique) {
        if (isSaved()) setSaved(false);

        ecraserFin();
        historique.add(actionHistorique);
        index++;
    }

    private void ecraserFin() {
        if (!retourAvantPossible()) return;

        while (historique.size() > index + 1) historique.remove(index + 1);
    }

    public boolean retourArrierePossible() {
        return index >= 0;
    }
    public boolean retourAvantPossible() {
        return index < historique.size() - 1;
    }

    public void retourArriere() {
        if (!retourArrierePossible()) return;

        ActionHistorique a = historique.get(index--);
        switch (a.action) {
            case MODIFICATION:
                a.ref.appliquer((Applicable) a.avant);
                break;
            case AJOUT:
                suppression(a);
                break;
            case SUPPRESSION:
                ajout(a);
                break;
        }
    }
    public void retourAvant() {
        if (!retourAvantPossible()) return;

        ActionHistorique a = historique.get(++index);
        switch (a.action) {
            case MODIFICATION:
                a.ref.appliquer((Applicable) a.apres);
                break;
            case AJOUT:
                ajout(a);
                break;
            case SUPPRESSION:
                suppression(a);
                break;
        }
    }

    private void suppression(ActionHistorique a) {
        switch (a.type) {
            case COMPTE:
                ((Domaine) a.avant).getComptes().remove((Compte) a.ref);
                break;
            case DOMAINE:
                donnees.getDomaines().remove((Domaine) a.ref);
                break;
        }
    }
    private void ajout(ActionHistorique a) {
        switch (a.type) {
            case COMPTE:
                ((Domaine) a.avant).getComptes().add((Compte) a.ref);
                break;
            case DOMAINE:
                donnees.getDomaines().add((Domaine) a.ref);
                break;
        }
    }

    public void setSaved(boolean saved) {
        this.saved = saved;
    }
    public boolean isSaved() {
        return saved;
    }
}
