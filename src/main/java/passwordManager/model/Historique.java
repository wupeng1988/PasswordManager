package passwordManager.model;

import java.util.ArrayList;

/**
 * Nico on 08/06/2017.
 */
public class Historique {
    private Donnees donnees;

    private boolean saved = true;

    private int maxSize = 20;

    private ArrayList<ActionHistorique> historique = new ArrayList<>();
    private int index = -1;

    Historique(Donnees donnees) {
        this.donnees = donnees;
    }

    public void ajoutAction(ActionHistorique actionHistorique) {
        if (isSaved()) setSaved(false);

        ecraserFin();
        historique.add(actionHistorique);
        actionHistorique.executer();

        if (!verifierMax()) index++;
    }
    private boolean verifierMax() {
        while (historique.size() > maxSize) {
            historique.remove(0);
            return true;
        }

        return false;
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
        a.deExecuter();
    }
    public void retourAvant() {
        if (!retourAvantPossible()) return;

        ActionHistorique a = historique.get(++index);
        a.executer();
    }

    public void setSaved(boolean saved) {
        this.saved = saved;
    }
    public boolean isSaved() {
        return index == -1 || saved;
    }
    public void setMaxSize(int maxSize) {
        this.maxSize = maxSize;
        verifierMax();
    }
}
