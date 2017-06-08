package passwordManager.controleur;

import javafx.application.Platform;
import javafx.beans.InvalidationListener;
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
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.util.Callback;
import passwordManager.Crypto;
import passwordManager.cellStuff.TableViewCell;
import passwordManager.cellStuff.ListViewCell;
import passwordManager.ImageManager;
import passwordManager.PasswordManager;
import passwordManager.Utils;
import passwordManager.cellStuff.TableViewRow;
import passwordManager.model.Compte;
import passwordManager.model.Domaine;
import passwordManager.model.Donnees;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.Random;
import java.util.ResourceBundle;

/**
 * Nico on 05/06/2017.
 */
public class App implements Initializable {
    PasswordManager passwordManager;
    ImageManager imageManager = new ImageManager();
    File fichierOuvert = null;

    Donnees donneesActives = null;
    Domaine domaineSelectionne = null;
    Compte compteSelectionne = null;

    Random random = new Random();

    public boolean inOptions = false;

    private InfosCompte infosCompteControleur;
    private InfosDomaine infosDomaineControleur;
    private FichierInfo fichierInfoControleur;
    private Autorisation autorisationControleur;
    private DetailsIdle detailsIdleControleur;
    private Confirmation confirmationControleur;
    private Parametres parametresControleur;
    private Parent infosCompteVue;
    private Parent infosDomaineVue;
    private Parent detailsIdleVue;
    private Parent fichierInfoVue;
    private Parent autorisationVue;
    private Parent confirmationVue;
    private Parent parametresVue;

    @FXML private MenuItem miNouveau;
    @FXML private MenuItem miFermer;
    @FXML private MenuItem miOuvrir;
    @FXML private MenuItem miSauvegarder;
    @FXML private MenuItem miSauvegarderVers;
    @FXML private MenuItem miAutoriser;
    @FXML private MenuItem miInformations;
    @FXML private MenuItem miStatistiques;
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

    @FXML private Label lEtat;

    @FXML private AnchorPane root;
    @FXML private AnchorPane detailsRoot;
    @FXML private BorderPane bpEtat;
    @FXML private BorderPane detailsTitrePane;

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
        lDetailsTitre.maxWidthProperty().bind(detailsTitrePane.widthProperty().subtract(72));

        initPhase1();
    }

    private void initPhase1() { // Phase 1: pré-init de l'application
        initInfosVue(); // details
        initList();
        initDetailsTable();
        initBoutons();
        initEtat();
    }

    private void initInfosVue() {
        FXMLLoader fxmlLoaderInfosCompte = new FXMLLoader(getClass().getResource(PasswordManager.FXML_INFOSCOMPTE));
        FXMLLoader fxmlLoaderInfosDomaine = new FXMLLoader(getClass().getResource(PasswordManager.FXML_INFOSDOMAINE));
        FXMLLoader fxmlLoaderFichierInfo = new FXMLLoader(getClass().getResource(PasswordManager.FXML_FICHIERINFO));
        FXMLLoader fxmlLoaderAutorisation = new FXMLLoader(getClass().getResource(PasswordManager.FXML_AUTORISATION));
        FXMLLoader fxmlLoaderDetailsIdle = new FXMLLoader(getClass().getResource(PasswordManager.FXML_DETAILSIDLE));
        FXMLLoader fxmlLoaderConfimation = new FXMLLoader(getClass().getResource(PasswordManager.FXML_CONFIRMATION));
        FXMLLoader fxmlLoaderParametres = new FXMLLoader(getClass().getResource(PasswordManager.FXML_PARAMETRES));

        try {
            infosCompteVue = fxmlLoaderInfosCompte.load();
            infosCompteControleur = fxmlLoaderInfosCompte.getController();

            infosDomaineVue = fxmlLoaderInfosDomaine.load();
            infosDomaineControleur = fxmlLoaderInfosDomaine.getController();

            fichierInfoVue = fxmlLoaderFichierInfo.load();
            fichierInfoControleur = fxmlLoaderFichierInfo.getController();

            autorisationVue = fxmlLoaderAutorisation.load();
            autorisationControleur = fxmlLoaderAutorisation.getController();

            detailsIdleVue = fxmlLoaderDetailsIdle.load();
            detailsIdleControleur = fxmlLoaderDetailsIdle.getController();

            confirmationVue = fxmlLoaderConfimation.load();
            confirmationControleur = fxmlLoaderConfimation.getController();

            parametresVue = fxmlLoaderParametres.load();
            parametresControleur = fxmlLoaderParametres.getController();

            infosCompteControleur.bindParent(this);
            infosDomaineControleur.bindParent(this);
            fichierInfoControleur.bindParent(this);
            autorisationControleur.bindParent(this);
            confirmationControleur.bindParent(this);
            parametresControleur.bindParent(this);

            setAnchor(infosCompteVue, infosDomaineVue, fichierInfoVue, autorisationVue, detailsIdleVue, confirmationVue, parametresVue);
        } catch (IOException io) {
            io.printStackTrace();
        }
    }
    private void initList() {
        lvDomaines.prefWidthProperty().bind(spDomaines.widthProperty().subtract(2));
        lvDomaines.prefHeightProperty().bind(spDomaines.heightProperty().subtract(2));

        App self = this;
        lvDomaines.setCellFactory(param -> new ListViewCell(self));
        lvDomaines.getSelectionModel().selectedItemProperty().addListener((l, ov, nv) -> updateDomaine(nv));
    }
    private void initDetailsTable() {
        tvComptesUtilisateur.setCellValueFactory(new PropertyValueFactory<>("utilisateur"));
        tvComptesMotDePasse.setCellValueFactory(new PropertyValueFactory<>("motDePasse"));

        App self = this;
        tvComptes.setRowFactory(param -> new TableViewRow(self));

        tvComptes.setEditable(false);
        Callback<TableColumn<Compte, String>, TableCell<Compte, String>> cellFactory = (TableColumn<Compte, String> p) -> new TableViewCell();

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
        tvComptesNumero.setCellValueFactory(p -> new ReadOnlyObjectWrapper<>((tvComptes.getItems().indexOf(p.getValue()) + 1) + ""));

        tvComptes.getSelectionModel().selectedItemProperty().addListener((o, ov, nv) -> updateCompte(nv));
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
        bMonterDomaine.setGraphic(imageManager.constructImageViewFrom(ImageManager.ICONE_UP, 16, 16, true));
        bMonterCompte.setGraphic(imageManager.constructImageViewFrom(ImageManager.ICONE_UP, 16, 16, true));
        bDescendreDomaine.setGraphic(imageManager.constructImageViewFrom(ImageManager.ICONE_DOWN, 16, 16, true));
        bDescendreCompte.setGraphic(imageManager.constructImageViewFrom(ImageManager.ICONE_DOWN, 16, 16, true));
        bAjoutDomaine.setGraphic(imageManager.constructImageViewFrom(ImageManager.ICONE_ADD, 16, 16, true));
        bAjoutCompte.setGraphic(imageManager.constructImageViewFrom(ImageManager.ICONE_ADD, 16, 16, true));
        bModificationDomaine.setGraphic(imageManager.constructImageViewFrom(ImageManager.ICONE_INFO, 16, 16, true));
        bModificationCompte.setGraphic(imageManager.constructImageViewFrom(ImageManager.ICONE_INFO, 16, 16, true));
        bSuppressionDomaine.setGraphic(imageManager.constructImageViewFrom(ImageManager.ICONE_REMOVE, 16, 16, true));
        bSuppressionCompte.setGraphic(imageManager.constructImageViewFrom(ImageManager.ICONE_REMOVE, 16, 16, true));

        // Cas particulier: ecraserFiltre
        bEcraserFiltre.setText(null);
        bEcraserFiltre.setGraphic(imageManager.constructImageViewFrom(ImageManager.ICONE_REMOVE, 16, 16, true));
    }
    private void initEtat() {
        lEtat.setText("Bienvenue!");
        bpEtat.getStyleClass().add("bpEtatA");
        bpEtat.setOnMouseClicked(e -> {
            if (donneesActives.getEncrytionLevel() > 0 && !donneesActives.isAutorise()) {
                autoriser();
            }
        });
    }

    public void initPhase2() { // Phase 2: application chargée, on peut la reconfigurer avec les paramètres de l'utilisateur
        //charger(new File("dataRead" + PasswordManager.SAVE_EXTENSION), null);
        //lvDomaines.getSelectionModel().selectFirst();
        nouvelleSauvegarde();
    }

    private void addIfNotPresent(Pane pane, Node n) {
        if (pane.getChildren().contains(n)) return;

        pane.getChildren().add(n);
    }

    private void updateControles() {
        miNouveau.setDisable(false);
        miFermer.setDisable(true);
        miOuvrir.setDisable(false);
        miSauvegarder.setDisable(true);
        miSauvegarderVers.setDisable(true);
        miAutoriser.setDisable(true);
        miInformations.setDisable(false);
        miStatistiques.setDisable(false);
        miQuitter.setDisable(false);

        miAjouter.setDisable(true);
        miModifier.setDisable(true);
        miSupprimer.setDisable(true);
        miParametres.setDisable(false);

        miExplications.setDisable(false);
        miAPropos.setDisable(false);

        bAjoutDomaine.setDisable(true);
        bAjoutCompte.setDisable(true);

        bSuppressionDomaine.setDisable(true);
        bSuppressionCompte.setDisable(true);

        bModificationDomaine.setDisable(true);
        bModificationCompte.setDisable(true);

        bMonterDomaine.setDisable(true);
        bMonterCompte.setDisable(true);

        bDescendreDomaine.setDisable(true);
        bDescendreCompte.setDisable(true);

        if (tvComptes.isFocused() || tvComptes.getSelectionModel().getSelectedIndex() > -1) {
            miAjouter.setDisable(false);
            if (compteSelectionne != null) {
                miModifier.setDisable(false);
                miSupprimer.setDisable(false);
            }
        } else if (lvDomaines.isFocused() || lvDomaines.getSelectionModel().getSelectedIndex() > -1) {
            miAjouter.setDisable(false);
            if (domaineSelectionne != null) {
                miModifier.setDisable(false);
                miSupprimer.setDisable(false);
            }
        }

        if (detailsRoot.getChildren().contains(detailsIdleVue)) {
            miStatistiques.setDisable(true);
        }

        if (donneesActives.isAutorise()) {
            bAjoutDomaine.setDisable(false);
            bAjoutCompte.setDisable(false);
        } else if (donneesActives.getEncrytionLevel() > 0) {
            miAutoriser.setDisable(false);
        }

        if (domaineSelectionne != null) {
            bModificationDomaine.setDisable(false);
            bSuppressionDomaine.setDisable(false);

            bMonterDomaine.setDisable(!(donneesActives.getDomaines().indexOf(domaineSelectionne) > 0));
            bDescendreDomaine.setDisable(!(donneesActives.getDomaines().indexOf(domaineSelectionne) < donneesActives.getDomaines().size() - 1));
        }

        if (compteSelectionne != null) {
            bModificationCompte.setDisable(false);
            bSuppressionCompte.setDisable(false);

            bMonterCompte.setDisable(!(domaineSelectionne.getComptes().indexOf(compteSelectionne) > 0));
            bDescendreCompte.setDisable(!(domaineSelectionne.getComptes().indexOf(compteSelectionne) < domaineSelectionne.getComptes().size() - 1));
        }

        if (donneesActives.isAutorise()) {
            if (fichierOuvert != null) {
                miSauvegarder.setDisable(false);
            }

            miSauvegarderVers.setDisable(false);
        }
    }
    private void updateUi() {
        if (lvDomaines.getItems().size() == 0) {
            montrerDetailsIdle();
        }

        updateControles();
    }

    @FXML
    private void ajouterAuFocus() {
        if (tvComptes.isFocused()) {
            ajoutCompte();
        } else if (lvDomaines.isFocused()) {
            ajoutDomaine();
        }
    }
    @FXML
    private void modifierAuFocus() {
        if (tvComptes.isFocused()) {
            modificationCompte();
        } else if (lvDomaines.isFocused()) {
            modificationDomaine();
        }
    }
    @FXML
    private void supprimerAuFocus() {
        if (tvComptes.isFocused()) {
            suppressionCompte();
        } else if (lvDomaines.isFocused()) {
            suppressionDomaine();
        }
    }

    @FXML
    private void monterDomaine() {
        if (donneesActives == null || domaineSelectionne == null) return;

        int li = donneesActives.getDomaines().lastIndexOf(domaineSelectionne);
        if (li == 0) return; // le domaine est déjà en première position

        Collections.swap(donneesActives.getDomaines(), li - 1, li);
        selection(1, li - 1);
    }

    public void selection(int level, int item) {
        switch (level) {
            case 0:
                if (domaineSelectionne != null && item < tvComptes.getItems().size())
                    tvComptes.getSelectionModel().select(item);
                break;
            case 1:
                if (donneesActives != null && item < lvDomaines.getItems().size())
                    lvDomaines.getSelectionModel().select(item);
                break;
        }
    }
    private void deselection(int level) { // level = 1 : déselection de domaine, 2 : déselection de domaine + compte
        if (level > 0 && tvComptes.getSelectionModel().getSelectedItems() != null) {
            tvComptes.getSelectionModel().clearSelection();
            compteSelectionne = null;
        }

        if (level > 1 && lvDomaines.getSelectionModel().getSelectedItem() != null) {
            lvDomaines.getSelectionModel().clearSelection();
            domaineSelectionne = null;
        }
    }

    void selectionDomaine(Domaine d) {
        if (lvDomaines.getItems().contains(d)) {
            lvDomaines.getSelectionModel().select(d);
            domaineSelectionne = d;
        }
    }

    void selectionCompte(Compte c) {
        if (tvComptes.getItems().contains(c)) {
            tvComptes.getSelectionModel().select(c);
            compteSelectionne = c;
        }
    }

    @FXML
    private void menuDetailsIdle() {
        montrerDetailsIdle();
        updateUi();
    }
    private void montrerDetailsIdle() {
        addIfNotPresent(detailsRoot, detailsIdleVue);
        detailsIdleControleur.initDonnees(donneesActives);
        deselection(2);
    }

    @FXML
    private void montrerParametres() {
        montrerOption(parametresVue);
    }

    @FXML
    private void monterCompte() {
        if (donneesActives == null || domaineSelectionne == null || compteSelectionne == null) return;

        int li = domaineSelectionne.getComptes().lastIndexOf(compteSelectionne);
        if (li == 0) return; // le compte est déjà en première position

        Collections.swap(domaineSelectionne.getComptes(), li - 1, li);
        selection(0, li - 1);
    }

    @FXML
    private void descendreDomaine() {
        if (donneesActives == null || domaineSelectionne == null) return;

        int li = donneesActives.getDomaines().lastIndexOf(domaineSelectionne);
        if (li == donneesActives.getDomaines().size() - 1) return; // le domaine est déjà en dernière position

        Collections.swap(donneesActives.getDomaines(), li + 1, li);
        selection(1, li + 1);
    }

    @FXML
    private void descendreCompte() {
        if (donneesActives == null || domaineSelectionne == null || compteSelectionne == null) return;

        int li = domaineSelectionne.getComptes().lastIndexOf(compteSelectionne);
        if (li == domaineSelectionne.getComptes().size() - 1) return; // le compte est déjà en dernière position

        Collections.swap(domaineSelectionne.getComptes(), li + 1, li);
        selection(0, li + 1);
    }

    @FXML
    private void modifierFichierInfo() {
        if (donneesActives == null) return;

        fichierInfoControleur.initData(donneesActives);
        montrerOption(fichierInfoVue);
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
    }

    @FXML
    public void modificationDomaine() {
        if (domaineSelectionne == null) return;
        if (!donneesActives.isAutorise()) {
            autoriser();
            return;
        }

        montrerOption(infosDomaineVue);
        infosDomaineControleur.initDomaine(domaineSelectionne);
    }
    @FXML
    public void modificationCompte() {
        if (compteSelectionne == null) return;
        if (!donneesActives.isAutorise()) {
            autoriser();
            return;
        }

        montrerOption(infosCompteVue);
        infosCompteControleur.initCompte(compteSelectionne);
    }

    @FXML
    private void ajoutDomaine() {
        montrerOption(infosDomaineVue);
        infosDomaineControleur.nouveauDomaine();
    }
    @FXML
    private void ajoutCompte() {
        montrerOption(infosCompteVue);
        infosCompteControleur.nouveauCompte();
    }

    @FXML
    public void suppressionDomaine() {
        if (domaineSelectionne == null) return;

        montrerOption(confirmationVue);
        confirmationControleur.initObject(domaineSelectionne);
    }
    @FXML
    public void suppressionCompte() {
        if (compteSelectionne == null) return;

        montrerOption(confirmationVue);
        confirmationControleur.initObject(compteSelectionne);
    }

    void suppression(Object o) {
        if (o instanceof Domaine) {
            donneesActives.getDomaines().remove(domaineSelectionne);
        } else if (o instanceof Compte) {
            domaineSelectionne.getComptes().remove(compteSelectionne);
        }
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
            initUi();
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
            charger(selectedFile, null);
            initUi();
        }
    }

    @FXML
    private void autoriser() {
        montrerOption(autorisationVue);
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

    private void montrerOption(Parent p) {
        if (!root.getChildren().contains(p)) root.getChildren().add(p);
        inOptions = true;
    }

    private void sauvegarder(File file) {
        if (!donneesActives.isAutorise()) return;

        try {
            System.out.println("Saving");
            if (donneesActives.getEncrytionLevel() > 0 && donneesActives.getMotDePasse().length() > 5)
                Utils.writeSaveData(donneesActives, file.getAbsolutePath(), new Crypto(donneesActives.getMotDePasse()));
            else
                Utils.writeSaveData(donneesActives, file.getAbsolutePath(), null);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    void charger(File file, Crypto crypto) {
        donneesActives = Utils.readSavedData(file.getAbsolutePath(), crypto);
        if (donneesActives == null) { // erreur
            passwordManager.stage.setTitle(PasswordManager.TITLE);
            System.err.println("Erreur de lecture!");
            return;
        }

        fichierOuvert = file;
        miSauvegarder.setDisable(false);
    }

    void initUi() {
        bpEtat.getStyleClass().clear();
        bpEtat.getStyleClass().add((donneesActives.getEncrytionLevel() > 0 && !donneesActives.isAutorise() ? "bpEtatNA" : "bpEtatA"));
        if (fichierOuvert != null)
            lEtat.setText(fichierOuvert.getAbsolutePath() + " - édition " + (donneesActives.getEncrytionLevel() > 0 && !donneesActives.isAutorise() ? "interdite" : "autorisée"));
        else
            lEtat.setText("Nouveau fichier");
        setTitre();
        Platform.runLater(lvDomaines::requestFocus);
        montrerDetailsIdle();

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
        filteredList.addListener((InvalidationListener) observable -> updateUi());

        updateUi();
    }

    void finEdition() {
        root.getChildren().removeAll(
                infosCompteVue,
                infosDomaineVue,
                fichierInfoVue,
                autorisationVue,
                confirmationVue,
                parametresVue
        );
        inOptions = false;
    }

    private void updateDomaine(Domaine d) {
        if (d == null || d.equals(domaineSelectionne)) return;

        domaineSelectionne = d;
        compteSelectionne = null;

        detailsRoot.getChildren().remove(detailsIdleVue);

        tvComptes.setItems(d.getComptes());
        d.getComptes().addListener((InvalidationListener) observable -> updateUi());

        lDetailsTitre.textProperty().bind(d.nomProperty());
        lDetailsDomaine.textProperty().bind(d.domaineProperty());
        lDetailsComptesTaille.setText("" + d.getComptes().size());
        lDetailsNotes.textProperty().bind(d.notesProperty());

        ivDetailsIcone.setImage(imageManager.getImage(d.getIconeLocation()));

        updateUi();
    }
    private void updateCompte(Compte c) {
        if (c == null || c.equals(compteSelectionne)) return;

        compteSelectionne = c;

        updateUi();
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

    public ImageManager getImageManager() {
        return imageManager;
    }
    public Donnees getDonneesActives() {
        return donneesActives;
    }

    @FXML
    private void exit() {
        Platform.exit();
    }

    private void setAnchor(Node n) {
        setAnchor(n, .0, .0, .0, .0);
    }
    private void setAnchor(Node ...n) {
        for (Node ns : n)
            setAnchor(ns);
    }
    private void setAnchor(Node n, double t, double r, double b, double l) {
        AnchorPane.setTopAnchor(n, t);
        AnchorPane.setRightAnchor(n, r);
        AnchorPane.setBottomAnchor(n, b);
        AnchorPane.setLeftAnchor(n, l);
    }
}

