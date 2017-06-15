package passwordManager.alea;

import org.w3c.dom.*;

import java.util.Random;

/**
 * Nico on 15/06/2017.
 */
public class AleaScheme {
    private final static String MDP_ALPHABET_LETTRE_MAJ = "ABCDEFGHIJKLMNOPQRSTUVWYZ";
    private final static String MDP_ALPHABET_LETTRE_MIN = MDP_ALPHABET_LETTRE_MAJ.toLowerCase();
    private final static String MDP_ALPHABET_CHIFFRE = "0123456789";
    private final static String MDP_ALPHABET_SEPARATEUR = "-_";
    private final static String MDP_ALPHABET_SPECIAL = "$*";

    private final static String MDP_ALPHABET =
            MDP_ALPHABET_LETTRE_MIN
                    + MDP_ALPHABET_LETTRE_MAJ
                    + MDP_ALPHABET_CHIFFRE
                    + MDP_ALPHABET_SEPARATEUR
                    + MDP_ALPHABET_SPECIAL;

    private Random random;

    public enum Type {LETTRE_UNIQUEMENT, CHIFFRES_UNIQUEMENT, TOUS}
    private String nom;
    private int tailleMax, tailleMin;
    private int minMaj, minChiffres, minCharSpe, minMin;
    private String alphabet;
    private Type type;

    public AleaScheme() {
        this("default", 12, 6, 0, 0, 0, 0, MDP_ALPHABET, Type.TOUS);
    }
    public AleaScheme(String nom) {
        this(nom, 12, 6, 0, 0, 0, 0, MDP_ALPHABET, Type.TOUS);
    }
    public AleaScheme(Node n) {
        if (n.getNodeType() == Node.ELEMENT_NODE) {
            nom = ((Element) n).getElementsByTagName("nom").item(0).getTextContent();
            type = AleaScheme.Type.valueOf(((Element) n).getElementsByTagName("type").item(0).getTextContent());
            tailleMin = Integer.parseInt(((Element) n).getElementsByTagName("tailleMin").item(0).getTextContent());
            tailleMax = Integer.parseInt(((Element) n).getElementsByTagName("tailleMax").item(0).getTextContent());
            minMaj = Integer.parseInt(((Element) n).getElementsByTagName("minMaj").item(0).getTextContent());
            minMin = Integer.parseInt(((Element) n).getElementsByTagName("minMin").item(0).getTextContent());
            minChiffres = Integer.parseInt(((Element) n).getElementsByTagName("minChiffres").item(0).getTextContent());
            minCharSpe = Integer.parseInt(((Element) n).getElementsByTagName("minCharSpe").item(0).getTextContent());
            alphabet = ((Element) n).getElementsByTagName("alphabet").item(0).getTextContent();
        }
    }
    public AleaScheme(String nom, int tailleMax, int tailleMin, int minMaj, int minChiffres, int minCharSpe, int minMin, String alphabet, Type type) {
        this.nom = nom;
        this.tailleMax = tailleMax;
        this.tailleMin = tailleMin;
        this.minMaj = minMaj;
        this.minChiffres = minChiffres;
        this.minCharSpe = minCharSpe;
        this.minMin = minMin;
        this.alphabet = alphabet;
        this.type = type;
    }

    public String generate() { // generate against this scheme
        if (!isValid()) return "";

        int lengthVar = tailleMax - tailleMin;
        int lengthMin = tailleMin;
        int length = lengthVar > 0 ? random.nextInt(lengthVar) + lengthMin : lengthMin;
        String alphabet = "";
        switch (type) {
            case CHIFFRES_UNIQUEMENT:
                for (char a : this.alphabet.toCharArray())
                    if (Character.isDigit(a))
                        alphabet += a;
                break;
            case LETTRE_UNIQUEMENT:
                for (char a : this.alphabet.toCharArray())
                    if (!Character.isDigit(a))
                        alphabet += a;
                break;
            case TOUS:
                alphabet = this.alphabet;
                break;
        }

        StringBuilder gen = new StringBuilder();
        while (!isValid(gen.toString())) {
            gen = new StringBuilder();
            for (int i = 0; i < length; i++)
                gen.append(alphabet.charAt(random.nextInt(alphabet.length())));
        }

        return gen.toString();
    }

    private boolean isValid(String mdp) {
        int nbMaj = 0, nbMin = 0, nbCharSpe = 0, nbChiffres = 0;
        for (char a : mdp.toCharArray()) {
            if (Character.isDigit(a))
                nbChiffres++;
            else if (Character.isLetter(a)) {
                if (Character.isUpperCase(a))
                    nbMaj++;
                else
                    nbMin++;
            }
            else
                nbCharSpe++;
        }

        return (isBetween(mdp.length(), tailleMin, tailleMax) && nbMaj >= minMaj && nbMin >= minMin && nbChiffres >= minChiffres && nbCharSpe >= minCharSpe);
    }
    public boolean isValid() {
        int sMin = minMin + minMaj + minChiffres + minCharSpe;
        if (
                minMin >= 0 &&
                        minMaj >= 0 &&
                        minChiffres >= 0 &&
                        minCharSpe >= 0 &&
                        isBetween(tailleMin, 0, tailleMax) &&
                        isBetween(tailleMax, 4, 30) &&
                        isBetween(sMin, 0, tailleMax)
                ) {
            return true;
        } else
            return false;
    }

    private boolean isBetween(int x, int min, int max) {
        return min <= x && x <= max;
    }

    public String getNom() {
        return nom;
    }
    public int getTailleMax() {
        return tailleMax;
    }
    public int getTailleMin() {
        return tailleMin;
    }
    public int getMinMaj() {
        return minMaj;
    }
    public int getMinChiffres() {
        return minChiffres;
    }
    public int getMinCharSpe() {
        return minCharSpe;
    }
    public int getMinMin() {
        return minMin;
    }
    public String getAlphabet() {
        return alphabet;
    }
    public Type getType() {
        return type;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }
    public void setTailleMax(int tailleMax) {
        this.tailleMax = tailleMax;
    }
    public void setTailleMin(int tailleMin) {
        this.tailleMin = tailleMin;
    }
    public void setMinMaj(int minMaj) {
        this.minMaj = minMaj;
    }
    public void setMinChiffres(int minChiffres) {
        this.minChiffres = minChiffres;
    }
    public void setMinCharSpe(int minCharSpe) {
        this.minCharSpe = minCharSpe;
    }
    public void setMinMin(int minMin) {
        this.minMin = minMin;
    }
    public void setAlphabet(String alphabet) {
        this.alphabet = alphabet;
    }
    public void setType(Type type) {
        this.type = type;
    }

    public void registerRandom(Random r) {
        this.random = r;
    }

    void toXML(Document doc, Element root) {
        Element r = doc.createElement("aleaScheme");
        root.appendChild(r);

        Element nom = doc.createElement("nom");
        nom.appendChild(doc.createTextNode(getNom()));
        r.appendChild(nom);

        Element type = doc.createElement("type");
        type.appendChild(doc.createTextNode(getType().toString()));
        r.appendChild(type);

        Element tailleMax = doc.createElement("tailleMax");
        tailleMax.appendChild(doc.createTextNode(String.valueOf(getTailleMax())));
        r.appendChild(tailleMax);

        Element tailleMin = doc.createElement("tailleMin");
        tailleMin.appendChild(doc.createTextNode(String.valueOf(getTailleMin())));
        r.appendChild(tailleMin);

        Element minMaj = doc.createElement("minMaj");
        minMaj.appendChild(doc.createTextNode(String.valueOf(getMinMaj())));
        r.appendChild(minMaj);

        Element minMin = doc.createElement("minMin");
        minMin.appendChild(doc.createTextNode(String.valueOf(getMinMin())));
        r.appendChild(minMin);

        Element minChiffres = doc.createElement("minChiffres");
        minChiffres.appendChild(doc.createTextNode(String.valueOf(getMinChiffres())));
        r.appendChild(minChiffres);

        Element minCharSpe = doc.createElement("minCharSpe");
        minCharSpe.appendChild(doc.createTextNode(String.valueOf(getMinCharSpe())));
        r.appendChild(minCharSpe);

        Element alphabet = doc.createElement("alphabet");
        alphabet.appendChild(doc.createTextNode(String.valueOf(getAlphabet())));
        r.appendChild(alphabet);
    }
}
