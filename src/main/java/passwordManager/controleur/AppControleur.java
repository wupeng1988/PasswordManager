package passwordManager.controleur;

import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
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
import java.util.Collections;
import java.util.ResourceBundle;

/**
 * Nico on 05/06/2017.
 */
public class AppControleur implements Initializable {
    PasswordManager passwordManager;
    ImageManager imageManager = new ImageManager();
    private File fichierOuvert = null;

    Donnees donneesActives = null;
    Domaine domaineSelectionne = null;
    Compte compteSelectionne = null;

    private InfosCompteControleur infosCompte;
    private InfosDomaineControleur infosDomaine;
    private FichierInfoControleur fichierInfo;
    private Parent infosCompteVue;
    private Parent infosDomaineVue;
    private Parent detailsIdle;
    private Parent fichierInfoVue;

    @FXML private MenuItem miNouveau;
    @FXML private MenuItem miOuvrir;
    @FXML private MenuItem miSauvegarder;
    @FXML private MenuItem miSauvegarderVers;
    @FXML private MenuItem miAutoriser;
    @FXML private MenuItem miInformations;
    @FXML private MenuItem miQuitter;

    @FXML private MenuItem miAjouter;
    @FXML private MenuItem miModifier;
    @FXML private MenuItem miSupprimer;
    @FXML private MenuItem miParametres;

    @FXML private MenuItem miExplications;
    @FXML private MenuItem miAPropos;

    @FXML private Button bEcraserFiltre;

    @FXML private Button bAjoutDomaine;
    @FXML private Button bAjoutCompte;

    @FXML private Button bSuppressionDomaine;
    @FXML private Button bSuppressionCompte;

    @FXML private Button bModificationDomaine;
    @FXML private Button bModificationCompte;

    @FXML private Button bMonterDomaine;
    @FXML private Button bMonterCompte;

    @FXML private Button bDescendreDomaine;
    @FXML private Button bDescendreCompte;

    @FXML private AnchorPane root;
    @FXML private AnchorPane detailsRoot;

    @FXML private TextField tfFiltre;

    @FXML private ScrollPane spDomaines;
    @FXML private ListView<Domaine> lvDomaines;

    @FXML private TableView<Compte> tvComptes;
    @FXML private TableColumn<Compte, String> tvComptesUtilisateur;
    @FXML private TableColumn<Compte, String> tvComptesMotDePasse;
    @FXML private TableColumn<Compte, String> tvComptesNumero;

    @FXML private Label lDetailsTitre;
    @FXML private Label lDetailsDomaine;
    @FXML private Label lDetailsComptesTaille;
    @FXML private Label lDetailsNotes;

    @FXML private ImageView ivDetailsIcone;

    // Phase 1: initUi
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initPhase1();
    }

    private void initPhase1() { // Phase 1: pré-init de l'application
        initInfosVue(); // details
        initList();
        initDetailsTable();
        initBoutons();
        initIdle();

        miSauvegarder.setDisable(true);
        miAutoriser.setDisable(true);

        miAjouter.setDisable(true);
        miModifier.setDisable(true);
        miSupprimer.setDisable(true);

        detailsRoot.getChildren().add(detailsIdle);
    }

    private void initInfosVue() {
        FXMLLoader fxmlLoaderInfosCompte = new FXMLLoader(getClass().getResource("/fxml/InfosCompteVue.fxml"));
        FXMLLoader fxmlLoaderInfosDomaine = new FXMLLoader(getClass().getResource("/fxml/InfosDomaineVue.fxml"));
        FXMLLoader fxmlLoaderFichierInfo = new FXMLLoader(getClass().getResource("/fxml/FichierInfo.fxml"));

        try {
            infosCompteVue = fxmlLoaderInfosCompte.load();
            infosCompte = fxmlLoaderInfosCompte.getController();

            infosDomaineVue = fxmlLoaderInfosDomaine.load();
            infosDomaine = fxmlLoaderInfosDomaine.getController();

            fichierInfoVue = fxmlLoaderFichierInfo.load();
            fichierInfo = fxmlLoaderFichierInfo.getController();

            infosCompte.bindParent(this);
            infosDomaine.bindParent(this);
            fichierInfo.bindParent(this);

            setAnchor(infosCompteVue, .0, .0, .0, .0);
            setAnchor(infosDomaineVue, .0, .0, .0, .0);
            setAnchor(fichierInfoVue, .0, .0, .0, .0);
        } catch (IOException io) {
            io.printStackTrace();
        }
    }
    private void initList() {
        lvDomaines.prefWidthProperty().bind(spDomaines.widthProperty().subtract(2));
        lvDomaines.prefHeightProperty().bind(spDomaines.heightProperty().subtract(2));

        lvDomaines.setCellFactory(param -> new ListViewCell(imageManager));
        lvDomaines.getSelectionModel().selectedItemProperty().addListener((l, ov, nv) -> updateDomaine(nv));
        lvDomaines.setOnMouseClicked(e -> {
            if (e.getClickCount() == 2 && domaineSelectionne != null) {
                modificationDomaine();
            }
        });
    }
    private void initDetailsTable() {
        tvComptesUtilisateur.setCellValueFactory(new PropertyValueFactory<>("utilisateur"));
        tvComptesMotDePasse.setCellValueFactory(new PropertyValueFactory<>("motDePasse"));

        tvComptes.setEditable(false);
        Callback<TableColumn<Compte, String>, TableCell<Compte, String>> cellFactory
                = (TableColumn<Compte, String> p) -> new TableViewCell();

        tvComptesUtilisateur.setCellFactory(cellFactory);
        tvComptesUtilisateur.setOnEditCommit(
                (TableColumn.CellEditEvent<Compte, String> t) -> (
                        t.getTableView().getItems().get(t.getTablePosition().getRow())
                ).setUtilisateur(t.getNewValue()));
        tvComptesMotDePasse.setCellFactory(cellFactory);
        tvComptesMotDePasse.setOnEditCommit(
                (TableColumn.CellEditEvent<Compte, String> t) -> (
                        t.getTableView().getItems().get(t.getTablePosition().getRow())
                ).setMotDePasse(t.getNewValue()));
        tvComptesNumero.setCellValueFactory(p -> new ReadOnlyObjectWrapper((tvComptes.getItems().indexOf(p.getValue()) + 1) + ""));

        tvComptes.getSelectionModel().selectedItemProperty().addListener((o, ov, nv) -> updateCompte(nv));
        tvComptes.setOnMouseClicked(e -> {
            if (e.getClickCount() == 2 && compteSelectionne != null) {
                modificationCompte();
            }
        });
    }
    private void initBoutons() {
        // Retirer les textes mis sur Scene Builder
        bMonterDomaine.setText(null);
        bMonterCompte.setText(null);
        bDescendreDomaine.setText(null);
        bDescendreCompte.setText(null);
        bAjoutDomaine.setText(null);
        bAjoutCompte.setText(null);
        bModificationDomaine.setText(null);
        bModificationCompte.setText(null);
        bSuppressionDomaine.setText(null);
        bSuppressionCompte.setText(null);

        // Gestion des activations de boutons
        bMonterDomaine.setDisable(true);
        bMonterCompte.setDisable(true);
        bDescendreDomaine.setDisable(true);
        bDescendreCompte.setDisable(true);
        bAjoutCompte.setDisable(true);
        bModificationDomaine.setDisable(true);
        bModificationCompte.setDisable(true);
        bSuppressionDomaine.setDisable(true);
        bSuppressionCompte.setDisable(true);

        // Setup les images
        bMonterDomaine.setGraphic(imageManager.constructImageViewFrom("arrow-up.png", 16, 16, true));
        bMonterCompte.setGraphic(imageManager.constructImageViewFrom("arrow-up.png", 16, 16, true));
        bDescendreDomaine.setGraphic(imageManager.constructImageViewFrom("arrow-down.png", 16, 16, true));
        bDescendreCompte.setGraphic(imageManager.constructImageViewFrom("arrow-down.png", 16, 16, true));
        bAjoutDomaine.setGraphic(imageManager.constructImageViewFrom("plus.png", 16, 16, true));
        bAjoutCompte.setGraphic(imageManager.constructImageViewFrom("plus.png", 16, 16, true));
        bModificationDomaine.setGraphic(imageManager.constructImageViewFrom("file.png", 16, 16, true));
        bModificationCompte.setGraphic(imageManager.constructImageViewFrom("file.png", 16, 16, true));
        bSuppressionDomaine.setGraphic(imageManager.constructImageViewFrom("error.png", 16, 16, true));
        bSuppressionCompte.setGraphic(imageManager.constructImageViewFrom("error.png", 16, 16, true));

        // Cas particulier: ecraserFiltre
        bEcraserFiltre.setText(null);
        bEcraserFiltre.setGraphic(imageManager.constructImageViewFrom("error.png", 16, 16, true));
    }
    private void initIdle() {
        FXMLLoader l = new FXMLLoader(getClass().getResource("/fxml/DetailsIdle.fxml"));
        try {
            detailsIdle = l.load();
            setAnchor(detailsIdle, .0, .0, .0, .0);
        } catch (IOException ignored) {}
    }

    public void initPhase2() { // Phase 2: application chargée, on peut la reconfigurer avec les paramètres de l'utilisateur
        charger(new File("dataRead" + PasswordManager.SAVE_EXTENSION));
        lvDomaines.getSelectionModel().selectFirst();
    }

    @FXML
    private void monterDomaine() {
        if (donneesActives == null || domaineSelectionne == null) return;

        int li = donneesActives.getDomaines().lastIndexOf(domaineSelectionne);
        if (li == 0) return; // le domaine est déjà en première position

        Collections.swap(donneesActives.getDomaines(), li - 1, li);
        lvDomaines.getSelectionModel().select(li - 1);
    }

    @FXML
    private void monterCompte() {
        if (donneesActives == null || domaineSelectionne == null || compteSelectionne == null) return;

        int li = domaineSelectionne.getComptes().lastIndexOf(compteSelectionne);
        if (li == 0) return; // le compte est déjà en première position

        Collections.swap(domaineSelectionne.getComptes(), li - 1, li);
        tvComptes.getSelectionModel().select(li - 1);
    }

    @FXML
    private void descendreDomaine() {
        if (donneesActives == null || domaineSelectionne == null) return;

        int li = donneesActives.getDomaines().lastIndexOf(domaineSelectionne);
        if (li == donneesActives.getDomaines().size() - 1) return; // le domaine est déjà en dernière position

        Collections.swap(donneesActives.getDomaines(), li + 1, li);
        lvDomaines.getSelectionModel().select(li + 1);
    }

    @FXML
    private void descendreCompte() {
        if (donneesActives == null || domaineSelectionne == null || compteSelectionne == null) return;

        int li = domaineSelectionne.getComptes().lastIndexOf(compteSelectionne);
        if (li == domaineSelectionne.getComptes().size() - 1) return; // le compte est déjà en dernière position

        Collections.swap(domaineSelectionne.getComptes(), li + 1, li);
        tvComptes.getSelectionModel().select(li + 1);
    }

    @FXML
    private void modifierFichierInfo() {
        if (donneesActives == null) return;

        fichierInfo.initData(donneesActives);
        root.getChildren().add(fichierInfoVue);
    }

    @FXML
    public void nouvelleSauvegarde() {
        donneesActives = new Donnees();
        fichierOuvert = null;

        tfFiltre.setText("");
        bMonterDomaine.setDisable(true);
        bDescendreDomaine.setDisable(true);
        bModificationDomaine.setDisable(true);
        bSuppressionDomaine.setDisable(true);

        miSauvegarder.setDisable(true);
        miAjouter.setDisable(true);
        miModifier.setDisable(true);
        miSupprimer.setDisable(true);

        miSauvegarder.setDisable(true);

        initUi();
        setTitre();

        detailsRoot.getChildren().add(detailsIdle);
    }

    @FXML
    private void modificationDomaine() {
        if (domaineSelectionne == null) return;

        infosDomaine.initDomaine(domaineSelectionne);
        root.getChildren().add(infosDomaineVue);
    }
    @FXML
    private void modificationCompte() {
        if (compteSelectionne == null) return;

        infosCompte.initCompte(compteSelectionne);
        root.getChildren().add(infosCompteVue);
    }

    @FXML
    private void ajoutDomaine() {
        infosDomaine.nouveauDomaine();
        root.getChildren().add(infosDomaineVue);
    }
    @FXML
    private void ajoutCompte() {
        infosCompte.nouveauCompte();
        root.getChildren().add(infosCompteVue);
    }

    @FXML
    private void suppressionDomaine() {
        if (domaineSelectionne == null) return;

        donneesActives.getDomaines().remove(domaineSelectionne);
    }
    @FXML
    private void suppressionCompte() {
        if (compteSelectionne == null) return;

        domaineSelectionne.getComptes().remove(compteSelectionne);
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
            fichierOuvert = selectedFile;
            miSauvegarder.setDisable(false);
            setTitre();
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
        if (fichierOuvert == null) sauvegarderDialog();

        sauvegarder(fichierOuvert);
    }

    @FXML
    private void ecraserFiltre() {
        tfFiltre.setText("");
    }

    private void sauvegarder(File file) {
        Utils.writeSaveData(donneesActives, file.getAbsolutePath());
    }
    private void charger(File file) {
        donneesActives = Utils.readSavedData(file.getAbsolutePath());
        if (donneesActives == null) { // erreur
            passwordManager.stage.setTitle(PasswordManager.TITLE);
            System.err.println("Erreur de lecture!");
            return;
        }

        fichierOuvert = file;
        miSauvegarder.setDisable(false);
        miAutoriser.setDisable(!(donneesActives.getEncrytionLevel() > 0));
        setTitre();
        Platform.runLater(lvDomaines::requestFocus);

        initUi();
    }

    private void initUi() {
        // Filter (http://code.makery.ch/blog/javafx-8-tableview-sorting-filtering/)
        FilteredList<Domaine> filteredList = new FilteredList<>(donneesActives.getDomaines(), p -> true);
        tfFiltre.textProperty().addListener((observable, oldValue, newValue) -> filteredList.setPredicate(domaine -> {
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

        lvDomaines.setItems(filteredList);
    }

    void finEdition() {
        root.getChildren().removeAll(infosCompteVue, infosDomaineVue, fichierInfoVue);
    }

    private void updateDomaine(Domaine d) {
        if (d == null || d.equals(domaineSelectionne)) return;

        domaineSelectionne = d;
        compteSelectionne = null;

        detailsRoot.getChildren().remove(detailsIdle);

        tvComptes.setItems(d.getComptes());
        lDetailsTitre.textProperty().bind(d.nomProperty());
        lDetailsDomaine.textProperty().bind(d.domaineProperty());
        lDetailsComptesTaille.setText("" + d.getComptes().size());
        lDetailsNotes.textProperty().bind(d.notesProperty());

        ivDetailsIcone.setImage(imageManager.getImage(d.getIconeLocation()));

        bMonterDomaine.setDisable(!(donneesActives.getDomaines().indexOf(domaineSelectionne) > 0));
        bDescendreDomaine.setDisable(!(donneesActives.getDomaines().indexOf(domaineSelectionne) < donneesActives.getDomaines().size() - 1));
        bMonterCompte.setDisable(true);
        bDescendreCompte.setDisable(true);

        bModificationDomaine.setDisable(false);
        bSuppressionDomaine.setDisable(false);
        bAjoutCompte.setDisable(false);

        bSuppressionCompte.setDisable(true);
        bModificationCompte.setDisable(true);
    }
    private void updateCompte(Compte c) {
        if (c == null || c.equals(compteSelectionne)) return;

        compteSelectionne = c;

        bMonterCompte.setDisable(!(domaineSelectionne.getComptes().indexOf(compteSelectionne) > 0));
        bDescendreCompte.setDisable(!(domaineSelectionne.getComptes().indexOf(compteSelectionne) < domaineSelectionne.getComptes().size() - 1));
        bModificationCompte.setDisable(false);
        bSuppressionCompte.setDisable(false);
    }

    public void setMain(PasswordManager m) {
        passwordManager = m;
    }
    private void setTitre() {
        if (fichierOuvert != null)
            passwordManager.stage.setTitle(PasswordManager.TITLE + " - " + fichierOuvert.getAbsolutePath());
        else
            passwordManager.stage.setTitle(PasswordManager.TITLE);
    }

    @FXML
    private void exit() {
        Platform.exit();
    }

    private void setAnchor(Node n, double t, double r, double b, double l) {
        AnchorPane.setTopAnchor(n, t);
        AnchorPane.setRightAnchor(n, r);
        AnchorPane.setBottomAnchor(n, b);
        AnchorPane.setLeftAnchor(n, l);
    }
}

