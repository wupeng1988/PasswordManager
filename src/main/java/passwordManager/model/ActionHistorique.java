package passwordManager.model;

import javafx.collections.ObservableList;

import java.util.Collections;
import java.util.List;

/**
 * Nico on 08/06/2017.
 */

/*
On enregistre:
    place avant, place après:
        si place avant == null, c'est un ajout,
        si place après == null, c'est une suppression
        si place avant == place après, c'est une mod
        si place avant != place après, déplacement
    copie de l'objet avant, et de l'objet après UNIQUEMENT si c'est une mod
    domaineRéférent SI c'est un compte.
        si domaine == null, il s'agit donc d'un domaine
        sinon, un compte
    reference l'objet en mémoire
 */
public class ActionHistorique {
    private enum Action {
        AJOUT, MODIFICATION, SUPPRESSION, DEPLACEMENT
    }
    private enum Objet {
        DOMAINE, COMPTE
    }

    private Integer pAvant;
    private Integer pApres;

    private Object oAvant;
    private Object oApres;
    private Applicable oRef;

    private Object objetReferent;

    private Action action;
    private Objet objet;

    private ActionHistorique() {
        this.pAvant = null;
        this.pApres = null;
        this.oAvant = null;
        this.oApres = null;
        this.oRef = null;
        this.objetReferent = null;
    }

    private ActionHistorique setpAvant(Integer pAvant) {
        this.pAvant = pAvant;
        return this;
    }
    private ActionHistorique setpApres(Integer pApres) {
        this.pApres = pApres;
        return this;
    }
    private ActionHistorique setoAvant(Object oAvant) {
        this.oAvant = oAvant;
        return this;
    }
    private ActionHistorique setoApres(Object oApres) {
        this.oApres = oApres;
        return this;
    }
    private ActionHistorique setoRef(Applicable oRef) {
        this.oRef = oRef;
        return this;
    }
    private ActionHistorique setObjetReferent(Object objetReferent) {
        this.objetReferent = objetReferent;
        return this;
    }
    private ActionHistorique setAction(Action action) {
        this.action = action;
        return this;
    }
    private ActionHistorique setObjet(Objet objet) {
        this.objet = objet;
        return this;
    }

    public static ActionHistorique ajoutCompte(int place, Domaine domaine, Compte compte) {
        return ajout(place, domaine, compte)
                .setObjet(Objet.COMPTE);
    }
    public static ActionHistorique ajoutDomaine(int place, Donnees donnees, Domaine domaine) {
        return ajout(place, donnees, domaine)
                .setObjet(Objet.DOMAINE);
    }
    private static ActionHistorique ajout(int place, Object referent, Applicable ref) {
        return new ActionHistorique()
                .setAction(Action.AJOUT)
                .setpApres(place)
                .setoRef(ref)
                .setObjetReferent(referent);
    }

    public static ActionHistorique modificationCompte(Compte avant, Compte apres) {
        return modification(avant, apres)
                .setObjet(Objet.COMPTE);
    }
    public static ActionHistorique modificationDomaine(Domaine avant, Domaine apres) {
        return modification(avant, apres)
                .setObjet(Objet.DOMAINE);
    }
    private static ActionHistorique modification(Applicable avant, Applicable apres) {
        return new ActionHistorique()
                .setAction(Action.MODIFICATION)
                .setoAvant(avant)
                .setoApres(apres.snap())
                .setoRef(apres);
    }

    public static ActionHistorique suppressionCompte(int place, Domaine domaine, Compte compte) {
        return suppression(place, domaine, compte)
                .setObjet(Objet.COMPTE);
    }
    public static ActionHistorique suppressionDomaine(int place, Donnees donnees, Domaine domaine) {
        return suppression(place, donnees, domaine)
                .setObjet(Objet.DOMAINE);
    }
    private static ActionHistorique suppression(int place, Object referent, Applicable ref) {
        return new ActionHistorique()
                .setAction(Action.SUPPRESSION)
                .setpAvant(place)
                .setoRef(ref)
                .setObjetReferent(referent);
    }

    public static ActionHistorique deplacementCompte(int pAvant, int pApres, Domaine domaine) {
        return deplacement(pAvant, pApres, domaine)
                .setObjet(Objet.COMPTE);
    }
    public static ActionHistorique deplacementDomaine(int pAvant, int pApres, Donnees donnees) {
        return deplacement(pAvant, pApres, donnees)
                .setObjet(Objet.DOMAINE);
    }
    private static ActionHistorique deplacement(int pAvant, int pApres, Object referent) {
        return new ActionHistorique()
                .setAction(Action.DEPLACEMENT)
                .setpAvant(pAvant)
                .setpApres(pApres)
                .setObjetReferent(referent);
    }

    private void executerAjout(List l, Applicable ref, int pApres) {
        l.add(ref);
        int pAvant = l.indexOf(ref);
        if (pApres >= 0) executerDeplacement(l, pAvant, pApres);
    }
    private void executerSuppression(List l, Applicable ref) {
        l.remove(ref);
    }
    private void executerModification(Applicable a1, Object a2) {
        a1.appliquer((Applicable) a2);
    }
    private void executerDeplacement(List l, int avant, int apres) {
        Collections.swap(l, avant, apres);
    }

    private void executerCompte(boolean reverse) {
        ObservableList<Compte> l = null;
        if (objetReferent != null)
            l = ((Domaine) objetReferent).getComptes();

        Compte c = (Compte) oRef;

        executerAction(l, c, reverse);
    }
    private void executerDomaine(boolean reverse) {
        ObservableList<Domaine> l = null;
        if (objetReferent != null)
            l = ((Donnees) objetReferent).getDomaines();

        Domaine d = (Domaine) oRef;

        executerAction(l, d, reverse);
    }

    private void executerAction(List l, Applicable a, boolean reverse) {
        switch (action) {
            case AJOUT:
                if (!reverse) executerAjout(l, a, pApres);
                else executerSuppression(l, a);

                break;
            case MODIFICATION:
                if (!reverse) executerModification(oRef, oApres);
                else executerModification(oRef, oAvant);

                break;
            case SUPPRESSION:
                if (!reverse) executerSuppression(l, a);
                else executerAjout(l, a, pAvant);

                break;
            case DEPLACEMENT:
                if (!reverse) executerDeplacement(l, pAvant, pApres);
                else executerDeplacement(l, pApres, pAvant);

                break;
        }
    }

    void executer() {
        switch (objet) {
            case COMPTE:
                executerCompte(false);
                break;
            case DOMAINE:
                executerDomaine(false);
                break;
        }
    }
    void deExecuter() {
        switch (objet) {
            case COMPTE:
                executerCompte(true);
                break;
            case DOMAINE:
                executerDomaine(true);
                break;
        }
    }
}
