package passwordManager.controleur;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import passwordManager.IntField;
import passwordManager.alea.AleaScheme;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

/**
 * Nico on 15/06/2017.
 */
public class AleaSchemes implements Initializable {
    private App app;
    private passwordManager.alea.AleaSchemes aleaSchemes;

    @FXML private ListView<AleaScheme> lvElements;
    @FXML private ListView<String> lvGenerated;
    @FXML private GridPane gpElement;

    @FXML private TextField tfNom;
    @FXML private ComboBox<AleaScheme.Type> cbType;
    private IntField ifTailleMin;
    private IntField ifTailleMax;
    private IntField ifMinMaj;
    private IntField ifMinMin;
    private IntField ifMinChiffres;
    private IntField ifMinCharSpe;
    @FXML private TextField tfAlphabet;

    @FXML private HBox hbGen;
    private IntField ifNombreTests;
    @FXML private Button bGenerate;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        lvElements.setCellFactory(lv -> new ListCell<AleaScheme>() {
            @Override
            protected void updateItem(AleaScheme item, boolean empty) {
                super.updateItem(item, empty);

                if (empty)
                    setText(null);
                else {
                    setText(item.getNom());
                }
            }
        });
        cbType.setItems(FXCollections.observableArrayList(AleaScheme.Type.values()));
        ifTailleMin = new IntField(0, 30, 0);
        ifTailleMax = new IntField(0, 30, 0);
        ifMinMaj = new IntField(0, 30, 0);
        ifMinMin = new IntField(0, 30, 0);
        ifMinChiffres = new IntField(0, 30, 0);
        ifMinCharSpe = new IntField(0, 30, 0);
        ifNombreTests = new IntField(2, 20, 10);

        gpElement.add(ifTailleMin, 1, 2);
        gpElement.add(ifTailleMax, 1, 3);
        gpElement.add(ifMinMaj, 1, 4);
        gpElement.add(ifMinMin, 1, 5);
        gpElement.add(ifMinChiffres, 1, 6);
        gpElement.add(ifMinCharSpe, 1, 7);

        hbGen.getChildren().add(ifNombreTests);
        ifNombreTests.setMaxWidth(50);

        bGenerate.setDisable(true);
        lvElements.getSelectionModel().selectedItemProperty().addListener((o, ov, nv) -> {
            bGenerate.setDisable(nv == null);
            updateElement(ov, nv);
        });
    }

    private void updateElement(AleaScheme ov, AleaScheme nv) {
        saveElement(ov);
        if (nv == null) return;

        tfNom.setText(nv.getNom());
        cbType.getSelectionModel().select(nv.getType());
        ifTailleMin.setValue(nv.getTailleMin());
        ifTailleMax.setValue(nv.getTailleMax());
        ifMinMaj.setValue(nv.getMinMaj());
        ifMinMin.setValue(nv.getMinMin());
        ifMinChiffres.setValue(nv.getMinChiffres());
        ifMinCharSpe.setValue(nv.getMinCharSpe());
        tfAlphabet.setText(nv.getAlphabet());
    }

    private void saveElement(AleaScheme a) {
        if (a == null) return;

        a.setNom(tfNom.getText());
        a.setType(cbType.getValue());
        a.setTailleMin(ifTailleMin.getValue());
        a.setTailleMax(ifTailleMax.getValue());
        a.setMinMaj(ifMinMaj.getValue());
        a.setMinMin(ifMinMin.getValue());
        a.setMinChiffres(ifMinChiffres.getValue());
        a.setMinCharSpe(ifMinCharSpe.getValue());
        a.setAlphabet(tfAlphabet.getText());
    }

    @FXML private void add() {
        aleaSchemes.addScheme(new AleaScheme("new scheme"));
    }
    @FXML private void delete() {
        AleaScheme a = lvElements.getSelectionModel().getSelectedItem();
        if (a == null) return;

        aleaSchemes.getSchemes().remove(a);
    }

    @FXML private void ok() {
        saveElement(lvElements.getSelectionModel().getSelectedItem());
        aleaSchemes.saveToFile();
        app.finEdition();
    }

    void initSchemes(passwordManager.alea.AleaSchemes a) {
        aleaSchemes = a;
        lvElements.setItems(a.getSchemes());
    }

    @FXML private void genererTest() {
        AleaScheme aleaScheme = lvElements.getSelectionModel().getSelectedItem();
        if (aleaScheme == null) return;

        saveElement(aleaScheme);

        ArrayList<String> gen = new ArrayList<>();
        for (int i = 0; i < ifNombreTests.getValue(); i++)
            gen.add(aleaScheme.generate());

        lvGenerated.setItems(FXCollections.observableArrayList(gen));
    }

    void bindParent(App a) {
        app = a;
    }
}
