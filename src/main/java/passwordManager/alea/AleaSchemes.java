package passwordManager.alea;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.util.Random;

/**
 * Nico on 15/06/2017.
 */
public class AleaSchemes {
    private final static String CHEMIN = "./aleaSchemes.xml";

    private final ObservableList<AleaScheme> schemes = FXCollections.observableArrayList();
    private Random random = new Random();

    public AleaSchemes() {
        if (!readFromFile()) addScheme(new AleaScheme()); // add default scheme
    }

    public ObservableList<AleaScheme> getSchemes() {
        return schemes;
    }
    public AleaScheme getScheme(String nom) {
        for (AleaScheme a : getSchemes())
            if (a.getNom().equals(nom))
                return a;
        return null;
    }

    public void addScheme(AleaScheme as) {
        as.registerRandom(random);
        schemes.add(as);
    }

    public void saveToFile() {
        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

            Document doc = docBuilder.newDocument();
            Element rootElement = doc.createElement("aleaSchemes");
            doc.appendChild(rootElement);

            for (AleaScheme a : schemes)
                a.toXML(doc, rootElement);

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new File(CHEMIN));

            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

            transformer.transform(source, result);
        } catch (ParserConfigurationException | TransformerException pce) {
            pce.printStackTrace();
        }
    }

    public boolean readFromFile() {
        try {
            File file = new File(CHEMIN);

            DocumentBuilder dBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document doc = dBuilder.parse(file);
            NodeList nl = doc.getElementsByTagName("aleaScheme");

            for (int i = 0; i < nl.getLength(); i++)
                addScheme(new AleaScheme(nl.item(i)));

            return true;
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return false;
    }
}
