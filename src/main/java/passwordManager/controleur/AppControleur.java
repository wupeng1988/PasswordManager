package passwordManager.controleur;

import javafx.application.Platform;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.util.Callback;
import passwordManager.cellStuff.TableViewCell;
import passwordManager.cellStuff.ListViewCell;
import passwordManager.ImageManager;
import passwordManager.PasswordManager;
import passwordManager.Utils;
import passwordManager.model.Compte;
import passwordManager.model.Domaine;
import passwordManager.model.Donnees;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Nico on 05/06/2017.
 */
public class AppControleur implements Initializable {
    PasswordManager passwordManager;
    ImageManager im = new ImageManager();
    private File fileOpened;

    @FXML private Button bEcraserFiltre;

    @FXML private Button bAjoutDomaine;
    @FXML private Button bAjoutCompte;

    @FXML private Button bSuppressionDomaine;
    @FXML private Button bSuppressionCompte;

    @FXML private Button bModificationDomaine;
    @FXML private Button bModificationCompte;

    @FXML private AnchorPane root;
    @FXML private AnchorPane detailsRoot;
    Donnees donnees;
    Domaine domaineSelectionne;
    private EditionCompteControleur editionCompte;
    private EditionDomaineControleur editionDomaine;
    private FichierInfoControleur fichierInfo;
    private Parent editionCompteVue;
    private Parent editionDomaineVue;
    private Parent detailsIdle;
    private Parent fichierInfoVue;

    @FXML private TextField filter;

    @FXML private ScrollPane sp;
    @FXML private ListView<Domaine> list;

    @FXML private TableView<Compte> detailsTable;
    @FXML private TableColumn<Compte, String> detailsColumnNom;
    @FXML private TableColumn<Compte, String> detailsColumnMdp;
    @FXML private Label detailsTitre;

    @FXML private Label domaineLabel;
    @FXML private Label nbComptesLabel;
    @FXML private Label notesLabel;

    @FXML private ImageView icone;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initDetailsTable();
        initList();
        initEditionVues();
        initIdle();
        initBoutons();

        detailsRoot.getChildren().add(detailsIdle);

        charger(new File("dataRead" + PasswordManager.SAVE_EXTENSION));
        sauvegarder(new File("dataWrite" + PasswordManager.SAVE_EXTENSION));

        // List bind
        list.prefWidthProperty().bind(sp.widthProperty().subtract(2));
        list.prefHeightProperty().bind(sp.heightProperty().subtract(2));
    }

    @FXML
    private void modifierFichierInfo() {
        fichierInfo.initData(donnees);
        root.getChildren().add(fichierInfoVue);
    }

    @FXML
    public void nouvelleSauvegarde() {
        fileOpened = null;
        bModificationDomaine.setDisable(true);
        bSuppressionDomaine.setDisable(true);
        donnees = new Donnees();
        initUi();
        detailsRoot.getChildren().add(detailsIdle);
        passwordManager.stage.setTitle(PasswordManager.TITLE);
    }

    @FXML
    private void editDomaine() {
        Domaine edit = list.getSelectionModel().getSelectedItem();
        if (edit == null) return;

        editionDomaine.initDomaine(edit);
        root.getChildren().add(editionDomaineVue);
    }
    @FXML
    private void editCompte() {
        Compte edit = detailsTable.getSelectionModel().getSelectedItem();
        if (edit == null) return;

        editionCompte.initCompte(edit);
        root.getChildren().add(editionCompteVue);
    }

    @FXML
    private void ajoutDomaine() {
        editionDomaine.nouveauDomaine();
        root.getChildren().add(editionDomaineVue);
    }
    @FXML
    private void ajoutCompte() {
        editionCompte.nouveauCompte();
        root.getChildren().add(editionCompteVue);
    }

    @FXML
    private void supprimerDomaine() {
        Domaine domaine = list.getSelectionModel().getSelectedItem();
        if (domaine == null) return;

        donnees.getDomaines().remove(domaine);
    }
    @FXML
    private void supprimerCompte() {
        Compte compte = detailsTable.getSelectionModel().getSelectedItem();
        if (compte == null) return;

        list.getSelectionModel().getSelectedItem().getComptes().remove(compte);
    }

    @FXML
    public void sauvegarderDialog() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choisir un emplacement de sauvegarde");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Sauvegarde", "*" + PasswordManager.SAVE_EXTENSION));
        fileChooser.setInitialDirectory(new File("."));

        File selectedFile = fileChooser.showSaveDialog(passwordManager.stage);
        if (selectedFile != null) {
            sauvegarder(selectedFile);
            fileOpened = selectedFile;
            finishLoad();
        }
    }
    @FXML
    public void chargerDialog() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choisir un fichier de sauvegarde");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Sauvegarde", "*" + PasswordManager.SAVE_EXTENSION));
        fileChooser.setInitialDirectory(new File("."));

        File selectedFile = fileChooser.showOpenDialog(passwordManager.stage);
        if (selectedFile != null) {
            charger(selectedFile);
        }
    }

    @FXML
    public void sauvegarderSc() { // shortcut
        if (fileOpened == null) sauvegarderDialog();

        sauvegarder(fileOpened);
    }

    @FXML
    private void ecraserFiltre() {
        filter.setText("");
    }

    private void sauvegarder(File file) {
        Utils.writeSaveData(donnees, file.getAbsolutePath());
    }
    private void charger(File file) {
        donnees = Utils.readSavedData(file.getAbsolutePath());

        initUi();
        fileOpened = file;
    }

    void initUi() {
        // Filter (http://code.makery.ch/blog/javafx-8-tableview-sorting-filtering/)
        FilteredList<Domaine> filteredList = new FilteredList<>(donnees.getDomaines(), p -> true);
        filter.textProperty().addListener((observable, oldValue, newValue) -> filteredList.setPredicate(domaine -> {
            if (newValue == null || newValue.isEmpty()) {
                return true;
            }

            String lowerCaseFilter = newValue.toLowerCase();

            if (domaine.getNom().toLowerCase().contains(lowerCaseFilter)) {
                return true; // Filter matches first name.
            } else if (domaine.getDomaine().toLowerCase().contains(lowerCaseFilter)) {
                return true; // Filter matches last name.
            }

            return false; // Does not match.
        }));

        //SortedList<Domaine> sortedList = new SortedList<Domaine>(filteredList);
        //sortedList.comparatorProperty().bind(truc.comparatorProperty());

        list.setItems(filteredList);
    }

    void finEdition() {
        root.getChildren().removeAll(editionCompteVue, editionDomaineVue, fichierInfoVue);
    }

    private void updateDomaine(Domaine d) {
        if (d == null || d.equals(domaineSelectionne)) return;

        detailsRoot.getChildren().remove(detailsIdle);

        detailsTable.setItems(d.getComptes());
        detailsTitre.textProperty().bind(d.nomProperty());
        domaineLabel.textProperty().bind(d.domaineProperty());
        nbComptesLabel.setText("" + d.getComptes().size());
        notesLabel.textProperty().bind(d.notesProperty());

        icone.setImage(im.getImage(d.getIconeLocation()));

        domaineSelectionne = d;

        bModificationDomaine.setDisable(false);
        bSuppressionDomaine.setDisable(false);
        bAjoutCompte.setDisable(false);

        bSuppressionCompte.setDisable(true);
        bModificationCompte.setDisable(true);
    }

    private void initEditionVues() {
        FXMLLoader l1 = new FXMLLoader(getClass().getResource("/fxml/EditionCompteVue.fxml"));
        FXMLLoader l2 = new FXMLLoader(getClass().getResource("/fxml/EditionDomaineVue.fxml"));
        FXMLLoader l3 = new FXMLLoader(getClass().getResource("/fxml/FichierInfo.fxml"));

        try {
            editionCompteVue = l1.load();
            editionCompte = l1.getController();

            editionDomaineVue = l2.load();
            editionDomaine = l2.getController();

            fichierInfoVue = l3.load();
            fichierInfo = l3.getController();

            editionCompte.bindParent(this);
            editionDomaine.bindParent(this);
            fichierInfo.bindParent(this);

            AnchorPane.setTopAnchor(editionCompteVue, 0.0);
            AnchorPane.setRightAnchor(editionCompteVue, 0.0);
            AnchorPane.setBottomAnchor(editionCompteVue, 0.0);
            AnchorPane.setLeftAnchor(editionCompteVue, 0.0);

            AnchorPane.setTopAnchor(editionDomaineVue, 0.0);
            AnchorPane.setRightAnchor(editionDomaineVue, 0.0);
            AnchorPane.setBottomAnchor(editionDomaineVue, 0.0);
            AnchorPane.setLeftAnchor(editionDomaineVue, 0.0);

            AnchorPane.setTopAnchor(fichierInfoVue, 0.0);
            AnchorPane.setRightAnchor(fichierInfoVue, 0.0);
            AnchorPane.setBottomAnchor(fichierInfoVue, 0.0);
            AnchorPane.setLeftAnchor(fichierInfoVue, 0.0);
        } catch (IOException io) {
            io.printStackTrace();
        }
    }
    private void initList() {
        list.setCellFactory(param -> new ListViewCell(im));
        list.getSelectionModel().selectedItemProperty().addListener(e -> {
            Domaine selectedDomaine = list.getSelectionModel().getSelectedItem();
            updateDomaine(selectedDomaine);
        });
    }
    private void initDetailsTable() {
        detailsColumnNom.setCellValueFactory(new PropertyValueFactory<>("utilisateur"));
        detailsColumnMdp.setCellValueFactory(new PropertyValueFactory<>("motDePasse"));

        detailsTable.setEditable(false);
        Callback<TableColumn<Compte, String>, TableCell<Compte, String>> cellFactory
                = (TableColumn<Compte, String> p) -> new TableViewCell();
        detailsTable.getSelectionModel().selectedItemProperty().addListener((l, ov, nv) -> {
            if (nv != null) {
                bModificationCompte.setDisable(false);
                bSuppressionCompte.setDisable(false);
            }
        });

        detailsColumnNom.setCellFactory(cellFactory);
        detailsColumnNom.setOnEditCommit(
                (TableColumn.CellEditEvent<Compte, String> t) -> (
                        t.getTableView().getItems().get(t.getTablePosition().getRow())
                ).setUtilisateur(t.getNewValue()));
        detailsColumnMdp.setCellFactory(cellFactory);
        detailsColumnMdp.setOnEditCommit(
                (TableColumn.CellEditEvent<Compte, String> t) -> (
                        t.getTableView().getItems().get(t.getTablePosition().getRow())
                ).setMotDePasse(t.getNewValue()));
    }
    private void initBoutons() {
        bAjoutDomaine.setText(null);
        bAjoutCompte.setText(null);
        bModificationDomaine.setText(null);
        bModificationCompte.setText(null);
        bSuppressionDomaine.setText(null);
        bSuppressionCompte.setText(null);

        bAjoutCompte.setDisable(true);
        bModificationDomaine.setDisable(true);
        bModificationCompte.setDisable(true);
        bSuppressionDomaine.setDisable(true);
        bSuppressionCompte.setDisable(true);

        bAjoutDomaine.setGraphic(im.constructImageViewFrom("plus.png", 24, 24, true));
        bAjoutCompte.setGraphic(im.constructImageViewFrom("plus.png", 24, 24, true));
        bModificationDomaine.setGraphic(im.constructImageViewFrom("file.png", 24, 24, true));
        bModificationCompte.setGraphic(im.constructImageViewFrom("file.png", 24, 24, true));
        bSuppressionDomaine.setGraphic(im.constructImageViewFrom("error.png", 24, 24, true));
        bSuppressionCompte.setGraphic(im.constructImageViewFrom("error.png", 24, 24, true));

        bEcraserFiltre.setText(null);
        bEcraserFiltre.setGraphic(im.constructImageViewFrom("error.png", 16, 16, true));
    }
    private void initIdle() {
        FXMLLoader l = new FXMLLoader(getClass().getResource("/fxml/DetailsIdle.fxml"));
        try {
            detailsIdle = l.load();
            AnchorPane.setTopAnchor(detailsIdle, 0.0);
            AnchorPane.setRightAnchor(detailsIdle, 0.0);
            AnchorPane.setBottomAnchor(detailsIdle, 0.0);
            AnchorPane.setLeftAnchor(detailsIdle, 0.0);
        } catch (IOException ignored) {}
    }

    public void setMain(PasswordManager m) {
        passwordManager = m;
    }

    public void finishLoad() {
        passwordManager.stage.setTitle(PasswordManager.TITLE + " - " + fileOpened.getAbsolutePath());
        list.getSelectionModel().selectFirst();
    }

    @FXML
    private void exit() {
        Platform.exit();
    }
}

