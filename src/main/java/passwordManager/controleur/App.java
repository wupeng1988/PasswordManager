package passwordManager.controleur;

import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.binding.Bindings;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.util.Callback;
import passwordManager.*;
import passwordManager.cellStuff.TableViewCell;
import passwordManager.cellStuff.ListViewCell;
import passwordManager.cellStuff.TableViewRow;
import passwordManager.model.ActionHistorique;
import passwordManager.model.Compte;
import passwordManager.model.Domaine;
import passwordManager.model.Donnees;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.Random;
import java.util.ResourceBundle;

/**
 * Nico on 05/06/2017.
 */
public class App implements Initializable {
    private PasswordManager passwordManager;
    private ImageManager imageManager = new ImageManager();
    private Backup backup;
    private PSWFile fichierOuvert = null;
    private DriveHelper driveHelper = new DriveHelper();
    private passwordManager.alea.AleaSchemes aleaSchemes = new passwordManager.alea.AleaSchemes();

    private Donnees donneesActives = null;
    private Domaine domaineSelectionne = null;
    private Compte compteSelectionne = null;

    private Random random = new Random();

    public boolean inOptions = false;

    private InfosCompte infosCompteControleur;
    private InfosDomaine infosDomaineControleur;
    private FichierInfo fichierInfoControleur;
    private Autorisation autorisationControleur;
    private DetailsIdle detailsIdleControleur;
    private Confirmation confirmationControleur;
    private Parametres parametresControleur;
    private Explications explicationsControleur;
    private About aboutControleur;
    private AleaSchemes aleaSchemesControleur;
    private Parent infosCompteVue;
    private Parent infosDomaineVue;
    private Parent detailsIdleVue;
    private Parent fichierInfoVue;
    private Parent autorisationVue;
    private Parent confirmationVue;
    private Parent parametresVue;
    private Parent explicationsVue;
    private Parent aboutVue;
    private Parent aleaSchemesVue;

    @FXML private MenuItem miNouveau;
    @FXML private MenuItem miFermer;
    @FXML private MenuItem miOuvrir;
    @FXML private MenuItem miOuvrirDrive;
    @FXML private MenuItem miSauvegarder;
    @FXML private MenuItem miSauvegarderVers;
    @FXML private MenuItem miSauvegarderVersDrive;
    @FXML private MenuItem miAutoriser;
    @FXML private MenuItem miInformations;
    @FXML private MenuItem miStatistiques;
    @FXML private MenuItem miQuitter;

    @FXML private MenuItem miAnnuler;
    @FXML private MenuItem miRefaire;
    @FXML private MenuItem miAjouter;
    @FXML private MenuItem miModifier;
    @FXML private MenuItem miSupprimer;
    @FXML private MenuItem miDupliquer;
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

    @FXML private Button bMonterDomaineMax;
    @FXML private Button bMonterCompteMax;

    @FXML private Button bDescendreDomaineMax;
    @FXML private Button bDescendreCompteMax;

    @FXML private Label lEtat;

    @FXML private AnchorPane root;
    @FXML private VBox vbRoot;
    @FXML private AnchorPane detailsRoot;
    @FXML private BorderPane bpEtat;
    @FXML private BorderPane detailsTitrePane;

    @FXML private TextField tfFiltre;

    @FXML private ScrollPane spDomaines;
    @FXML private ListView<Domaine> lvDomaines;

    @FXML private TableView<Compte> tvComptes;
    @FXML private TableColumn<Compte, String> tvComptesNumero;
    @FXML private TableColumn<Compte, String> tvComptesUtilisateur;
    @FXML private TableColumn<Compte, String> tvComptesMotDePasse;
    @FXML private TableColumn<Compte, LocalDate> tvComptesDateCreation;

    @FXML private Label lDetailsTitre;
    @FXML private Label lDetailsDomaine;
    @FXML private Label lDetailsCategorie;
    @FXML private Label lDetailsComptesTaille;
    @FXML private Label lDetailsNotes;

    @FXML private ImageView ivDetailsIcone;

    // Phase 1: initUi
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        lDetailsTitre.maxWidthProperty().bind(detailsTitrePane.widthProperty().subtract(72));
        lvDomaines.setPlaceholder(new Label("No domain"));
        tvComptes.setPlaceholder(new Label("No account"));

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
        FXMLLoader fxmlLoaderExplications = new FXMLLoader(getClass().getResource(PasswordManager.FXML_EXPLICATIONS));
        FXMLLoader fxmlLoaderAbout = new FXMLLoader(getClass().getResource(PasswordManager.FXML_ABOUT));
        FXMLLoader fxmlLoaderAleaSchemes = new FXMLLoader(getClass().getResource(PasswordManager.FXML_ALEASCHEMES));

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

            explicationsVue = fxmlLoaderExplications.load();
            explicationsControleur = fxmlLoaderExplications.getController();

            aboutVue = fxmlLoaderAbout.load();
            aboutControleur = fxmlLoaderAbout.getController();

            aleaSchemesVue = fxmlLoaderAleaSchemes.load();
            aleaSchemesControleur = fxmlLoaderAleaSchemes.getController();

            infosCompteControleur.bindParent(this);
            infosDomaineControleur.bindParent(this);
            fichierInfoControleur.bindParent(this);
            autorisationControleur.bindParent(this);
            confirmationControleur.bindParent(this);
            parametresControleur.bindParent(this);
            explicationsControleur.bindParent(this);
            aboutControleur.bindParent(this);
            aleaSchemesControleur.bindParent(this);

            setAnchor(infosCompteVue, infosDomaineVue, fichierInfoVue, autorisationVue, detailsIdleVue, confirmationVue, parametresVue, explicationsVue, aboutVue, aleaSchemesVue);
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
        //lvDomaines.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    }
    private void initDetailsTable() {
        tvComptesUtilisateur.setCellValueFactory(new PropertyValueFactory<>("utilisateur"));
        tvComptesMotDePasse.setCellValueFactory(new PropertyValueFactory<>("motDePasse"));
        tvComptesDateCreation.setCellValueFactory(new PropertyValueFactory<>("dateCreation"));

        App self = this;
        tvComptes.setRowFactory(param -> new TableViewRow(self));

        tvComptes.setEditable(false);
        Callback<TableColumn<Compte, String>, TableCell<Compte, String>> cellFactory = (TableColumn<Compte, String> p) -> new TableViewCell();

        tvComptesNumero.setCellFactory(column -> new TableCell<Compte, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);

                if (empty) {
                    setCursor(Cursor.DEFAULT);
                    setText(null);
                } else {
                    setCursor(Cursor.HAND);
                    setText(String.valueOf(tvComptes.getItems().indexOf((Compte)getTableRow().getItem()) + 1));
                }
            }
        });
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
        tvComptesDateCreation.setCellFactory(column -> new TableCell<Compte, LocalDate>() {
            @Override
            protected void updateItem(LocalDate item, boolean empty) {
                super.updateItem(item, empty);

                if (empty) {
                    setText(null);
                    setCursor(Cursor.DEFAULT);
                } else {
                    if (item == null) {
                        setText("????/??/??");
                        setCursor(Cursor.HAND);
                    } else {
                        setText(item.format(DateTimeFormatter.ofPattern(Compte.DATE_FORMAT)));
                        setCursor(Cursor.HAND);

                        // Style all dates in March with a different color.
                        /*if (item.getMonth() == Month.MARCH) {
                            setTextFill(Color.CHOCOLATE);
                            setStyle("-fx-background-color: yellow");
                        } else {
                            setTextFill(Color.BLACK);
                            setStyle("");
                        }*/
                    }
                }
            }
        });

        tvComptes.getSelectionModel().selectedItemProperty().addListener((o, ov, nv) -> updateCompte(nv));
    }
    private void initBoutons() {
        // Retirer les textes mis sur Scene Builder
        bMonterDomaine.setText(null);
        bMonterCompte.setText(null);
        bDescendreDomaine.setText(null);
        bDescendreCompte.setText(null);
        bMonterDomaineMax.setText(null);
        bMonterCompteMax.setText(null);
        bDescendreDomaineMax.setText(null);
        bDescendreCompteMax.setText(null);
        bAjoutDomaine.setText(null);
        bAjoutCompte.setText(null);
        bModificationDomaine.setText(null);
        bModificationCompte.setText(null);
        bSuppressionDomaine.setText(null);
        bSuppressionCompte.setText(null);

        // Tootips
        bMonterDomaine.setTooltip(new Tooltip("Fit the selected domain"));
        bMonterCompte.setTooltip(new Tooltip("Edit the selected account"));
        bDescendreDomaine.setTooltip(new Tooltip("Go down the selected domain"));
        bDescendreCompte.setTooltip(new Tooltip("Move down the selected account"));
        bMonterDomaineMax.setTooltip(new Tooltip("Climb the selected domain to the first place"));
        bMonterCompteMax.setTooltip(new Tooltip("Fit the selected account to the first place"));
        bDescendreDomaineMax.setTooltip(new Tooltip("Move down the selected domain to the last"));
        bDescendreCompteMax.setTooltip(new Tooltip("Move down the selected account to the last"));
        bAjoutDomaine.setTooltip(new Tooltip("Add a new domain"));
        bAjoutCompte.setTooltip(new Tooltip("Add a new account"));
        bModificationDomaine.setTooltip(new Tooltip("Edit the selected domain"));
        bModificationCompte.setTooltip(new Tooltip("Edit the selected account"));
        bSuppressionDomaine.setTooltip(new Tooltip("Delete the selected domain"));
        bSuppressionCompte.setTooltip(new Tooltip("Delete the selected account"));

        // Gestion des activations de boutons
        bMonterDomaine.setDisable(true);
        bMonterCompte.setDisable(true);
        bDescendreDomaine.setDisable(true);
        bDescendreCompte.setDisable(true);
        bMonterDomaineMax.setDisable(true);
        bMonterCompteMax.setDisable(true);
        bDescendreDomaineMax.setDisable(true);
        bDescendreCompteMax.setDisable(true);
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
        bMonterDomaineMax.setGraphic(imageManager.constructImageViewFrom(ImageManager.ICONE_DOUBLE_UP, 16, 16, true));
        bMonterCompteMax.setGraphic(imageManager.constructImageViewFrom(ImageManager.ICONE_DOUBLE_UP, 16, 16, true));
        bDescendreDomaineMax.setGraphic(imageManager.constructImageViewFrom(ImageManager.ICONE_DOUBLE_DOWN, 16, 16, true));
        bDescendreCompteMax.setGraphic(imageManager.constructImageViewFrom(ImageManager.ICONE_DOUBLE_DOWN, 16, 16, true));
        bAjoutDomaine.setGraphic(imageManager.constructImageViewFrom(ImageManager.ICONE_ADD, 16, 16, true));
        bAjoutCompte.setGraphic(imageManager.constructImageViewFrom(ImageManager.ICONE_ADD, 16, 16, true));
        bModificationDomaine.setGraphic(imageManager.constructImageViewFrom(ImageManager.ICONE_INFO, 16, 16, true));
        bModificationCompte.setGraphic(imageManager.constructImageViewFrom(ImageManager.ICONE_INFO, 16, 16, true));
        bSuppressionDomaine.setGraphic(imageManager.constructImageViewFrom(ImageManager.ICONE_REMOVE, 16, 16, true));
        bSuppressionCompte.setGraphic(imageManager.constructImageViewFrom(ImageManager.ICONE_REMOVE, 16, 16, true));

        // Cas particulier: ecraserFiltre
        bEcraserFiltre.setText(null);
        bEcraserFiltre.setTooltip(new Tooltip("Retirer le filtre"));
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
        Preferences preferences = passwordManager.getPreferences();
        backup = new Backup(
                preferences.getPropriete(Preferences.PROP_DOSSIER_BACKUP_AUTO),
                Integer.parseInt(preferences.getPropriete(Preferences.PROP_BACKUP_AUTO))
        );

        fichierOuvert = new PSWFile(preferences);
        if (!fichierOuvert.exists()) {
            nouvelleSauvegarde();
        } else {
            boolean reussi = false;
            if (fichierOuvert.isDepuisDrive()) {
                if (telecharger(fichierOuvert, null))
                    reussi = true;
            } else {
                if (charger(fichierOuvert, null))
                    reussi = true;
            }

            if (!reussi)
                nouvelleSauvegarde();

            initUi();
        }

        updateWithPreferences();
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

        miAnnuler.setDisable(true);
        miRefaire.setDisable(true);
        miAjouter.setDisable(true);
        miModifier.setDisable(true);
        miSupprimer.setDisable(true);
        miDupliquer.setDisable(true);
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

        bMonterDomaineMax.setDisable(true);
        bMonterCompteMax.setDisable(true);

        bDescendreDomaineMax.setDisable(true);
        bDescendreCompteMax.setDisable(true);

        if (detailsRoot.getChildren().contains(detailsIdleVue)) {
            miStatistiques.setDisable(true);
        }

        if (donneesActives.isAutorise()) {
            bAjoutDomaine.setDisable(false);
            bAjoutCompte.setDisable(false);

            if (donneesActives.getHistorique().retourArrierePossible())
                miAnnuler.setDisable(false);

            if (donneesActives.getHistorique().retourAvantPossible())
                miRefaire.setDisable(false);

            if (tvComptes.isFocused() || tvComptes.getSelectionModel().getSelectedIndex() > -1) {
                miAjouter.setDisable(false);
                if (compteSelectionne != null) {
                    miModifier.setDisable(false);
                    miSupprimer.setDisable(false);
                    miDupliquer.setDisable(false);
                }
            } else if (lvDomaines.isFocused() || lvDomaines.getSelectionModel().getSelectedIndex() > -1) {
                miAjouter.setDisable(false);
                if (domaineSelectionne != null) {
                    miModifier.setDisable(false);
                    miSupprimer.setDisable(false);
                    miDupliquer.setDisable(false);
                }
            }

            if (domaineSelectionne != null) {
                bModificationDomaine.setDisable(false);
                bSuppressionDomaine.setDisable(false);

                bMonterDomaine.setDisable(!(donneesActives.getDomaines().indexOf(domaineSelectionne) > 0));
                bMonterDomaineMax.setDisable(!(donneesActives.getDomaines().indexOf(domaineSelectionne) > 0));
                bDescendreDomaine.setDisable(!(donneesActives.getDomaines().indexOf(domaineSelectionne) < donneesActives.getDomaines().size() - 1));
                bDescendreDomaineMax.setDisable(!(donneesActives.getDomaines().indexOf(domaineSelectionne) < donneesActives.getDomaines().size() - 1));
            }

            if (compteSelectionne != null) {
                bModificationCompte.setDisable(false);
                bSuppressionCompte.setDisable(false);

                bMonterCompte.setDisable(!(domaineSelectionne.getComptes().indexOf(compteSelectionne) > 0));
                bMonterCompteMax.setDisable(!(domaineSelectionne.getComptes().indexOf(compteSelectionne) > 0));
                bDescendreCompte.setDisable(!(domaineSelectionne.getComptes().indexOf(compteSelectionne) < domaineSelectionne.getComptes().size() - 1));
                bDescendreCompteMax.setDisable(!(domaineSelectionne.getComptes().indexOf(compteSelectionne) < domaineSelectionne.getComptes().size() - 1));
            }

            if (fichierOuvert != null) {
                if (!donneesActives.getHistorique().isSaved()) {
                    miSauvegarder.setDisable(false);
                }
            }

            miSauvegarderVers.setDisable(false);

            if (donneesActives.getHistorique().isSaved()) {
                if (donneesActives.getEncrytionLevel() > 0) {
                    miFermer.setDisable(false);
                }
            }
        } else if (donneesActives.getEncrytionLevel() > 0) {
            miAutoriser.setDisable(false);
        }
    }
    private void updateUi() {
        if (lvDomaines.getItems().size() == 0) {
            montrerDetailsIdle();
        }

        updateControles();
    }

    @FXML public void ajouterAuFocus() {
        if (tvComptes.isFocused()) {
            ajoutCompte();
        } else if (lvDomaines.isFocused()) {
            ajoutDomaine();
        }
    }
    @FXML public void modifierAuFocus() {
        if (tvComptes.isFocused()) {
            modificationCompte();
        } else if (lvDomaines.isFocused()) {
            modificationDomaine();
        }
    }
    @FXML public void supprimerAuFocus() {
        if (tvComptes.isFocused()) {
            suppressionCompte();
        } else if (lvDomaines.isFocused()) {
            suppressionDomaine();
        }
    }
    @FXML public void dupliquerAuFocus() {
        if (tvComptes.isFocused()) {
            dupliquerCompte();
        } else if (lvDomaines.isFocused()) {
            dupliquerDomaine();
        }
    }

    @FXML public void monterAuFocus() {
        if (tvComptes.isFocused()) {
            monterCompte();
        } else if (lvDomaines.isFocused()) {
            monterDomaine();
        }
    }
    @FXML public void descendreAuFocus() {
        if (tvComptes.isFocused()) {
            descendreCompte();
        } else if (lvDomaines.isFocused()) {
            descendreDomaine();
        }
    }
    @FXML public void monterMaxAuFocus() {
        if (tvComptes.isFocused()) {
            monterCompteMax();
        } else if (lvDomaines.isFocused()) {
            monterDomaineMax();
        }
    }
    @FXML public void descendreMaxAuFocus() {
        if (tvComptes.isFocused()) {
            descendreCompteMax();
        } else if (lvDomaines.isFocused()) {
            descendreDomaineMax();
        }
    }

    private void selection(int level, int item) { // 0 = compte, 1 = domaine
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

    @FXML private void menuDetailsIdle() {
        montrerDetailsIdle();
        updateUi();
    }
    private void montrerDetailsIdle() {
        addIfNotPresent(detailsRoot, detailsIdleVue);
        detailsIdleControleur.initDonnees(donneesActives);
        deselection(2);
    }

    @FXML public void montrerExplications() {
        root.getChildren().add(explicationsVue);
    }
    @FXML private void montrerAbout() {
        root.getChildren().add(aboutVue);
    }
    @FXML private void montrerAleaSchemes() {
        aleaSchemesControleur.initSchemes(getAleaSchemes());
        montrerOption(aleaSchemesVue);
    }

    @FXML public void montrerParametres() {
        montrerOption(parametresVue);
        parametresControleur.initDonnees();
    }

    @FXML private void monterDomaineMax() {
        if (donneesActives == null || domaineSelectionne == null) return;

        int li = donneesActives.getDomaines().lastIndexOf(domaineSelectionne);
        if (li == 0) return; // le domaine est déjà en première position

        getDonneesActives().getHistorique().ajoutAction(ActionHistorique.deplacementDomaine(li, 0, getDonneesActives()));
        selection(1, 0);
    }
    @FXML private void monterCompteMax() {
        if (donneesActives == null || domaineSelectionne == null || compteSelectionne == null) return;

        int li = domaineSelectionne.getComptes().lastIndexOf(compteSelectionne);
        if (li == 0) return; // le compte est déjà en première position

        getDonneesActives().getHistorique().ajoutAction(ActionHistorique.deplacementCompte(li, 0, getDomaineSelectionne()));
        selection(0, 0);
    }
    @FXML private void descendreDomaineMax() {
        if (donneesActives == null || domaineSelectionne == null) return;

        int litem = donneesActives.getDomaines().size() - 1;
        int li = donneesActives.getDomaines().lastIndexOf(domaineSelectionne);
        if (li == litem) return; // le domaine est déjà en dernière position

        getDonneesActives().getHistorique().ajoutAction(ActionHistorique.deplacementDomaine(li, litem, getDonneesActives()));
        selection(1, litem);
    }
    @FXML private void descendreCompteMax() {
        if (donneesActives == null || domaineSelectionne == null || compteSelectionne == null) return;

        int litem = domaineSelectionne.getComptes().size() - 1;
        int li = domaineSelectionne.getComptes().lastIndexOf(compteSelectionne);
        if (li == litem) return; // le compte est déjà en dernière position

        getDonneesActives().getHistorique().ajoutAction(ActionHistorique.deplacementCompte(li, litem, getDomaineSelectionne()));
        selection(0, litem);
    }

    @FXML private void monterDomaine() {
        if (donneesActives == null || domaineSelectionne == null) return;

        int li = donneesActives.getDomaines().lastIndexOf(domaineSelectionne);
        if (li == 0) return; // le domaine est déjà en première position


        getDonneesActives().getHistorique().ajoutAction(ActionHistorique.deplacementDomaine(li, li - 1, getDonneesActives()));
        selection(1, li - 1);
    }
    @FXML private void monterCompte() {
        if (donneesActives == null || domaineSelectionne == null || compteSelectionne == null) return;

        int li = domaineSelectionne.getComptes().lastIndexOf(compteSelectionne);
        if (li == 0) return; // le compte est déjà en première position

        getDonneesActives().getHistorique().ajoutAction(ActionHistorique.deplacementCompte(li, li - 1, getDomaineSelectionne()));
        selection(0, li - 1);
    }
    @FXML private void descendreDomaine() {
        if (donneesActives == null || domaineSelectionne == null) return;

        int li = donneesActives.getDomaines().lastIndexOf(domaineSelectionne);
        if (li == donneesActives.getDomaines().size() - 1) return; // le domaine est déjà en dernière position

        getDonneesActives().getHistorique().ajoutAction(ActionHistorique.deplacementDomaine(li, li + 1, getDonneesActives()));
        selection(1, li + 1);
    }
    @FXML private void descendreCompte() {
        if (donneesActives == null || domaineSelectionne == null || compteSelectionne == null) return;

        int li = domaineSelectionne.getComptes().lastIndexOf(compteSelectionne);
        if (li == domaineSelectionne.getComptes().size() - 1) return; // le compte est déjà en dernière position

        getDonneesActives().getHistorique().ajoutAction(ActionHistorique.deplacementCompte(li, li + 1, getDomaineSelectionne()));
        selection(0, li + 1);
    }

    @FXML private void modifierFichierInfo() {
        if (donneesActives == null) return;

        fichierInfoControleur.initData(donneesActives);
        montrerOption(fichierInfoVue);
    }

    @FXML public void nouvelleSauvegarde() {
        if (!checkSaveEnregistree()) return;

        nouvellesDonnees(new Donnees());
        fichierOuvert = new PSWFile();

        tfFiltre.setText("");

        initUi();
        setTitre();
    }

    @FXML public void modificationDomaine() {
        if (domaineSelectionne == null) return;
        if (!donneesActives.isAutorise()) {
            autoriser();
            return;
        }

        montrerOption(infosDomaineVue);
        infosDomaineControleur.initDomaine(domaineSelectionne);
    }
    @FXML public void modificationCompte() {
        if (compteSelectionne == null) return;
        if (!donneesActives.isAutorise()) {
            autoriser();
            return;
        }

        montrerOption(infosCompteVue);
        infosCompteControleur.initCompte(compteSelectionne);
    }

    @FXML private void ajoutDomaine() {
        if (!donneesActives.isAutorise()) {
            autoriser();
            return;
        }

        montrerOption(infosDomaineVue);
        infosDomaineControleur.nouveauDomaine();
    }
    @FXML private void ajoutCompte() {
        if (!donneesActives.isAutorise()) {
            autoriser();
            return;
        }

        montrerOption(infosCompteVue);
        infosCompteControleur.nouveauCompte();
    }

    @FXML public void suppressionDomaine() {
        if (domaineSelectionne == null) return;
        if (!donneesActives.isAutorise()) {
            autoriser();
            return;
        }

        montrerOption(confirmationVue);
        confirmationControleur.initObject(domaineSelectionne);
    }
    @FXML public void suppressionCompte() {
        if (compteSelectionne == null) return;
        if (!donneesActives.isAutorise()) {
            autoriser();
            return;
        }

        montrerOption(confirmationVue);
        confirmationControleur.initObject(compteSelectionne, domaineSelectionne);
    }

    private void dupliquerCompte() {
        if (getDomaineSelectionne() == null || getCompteSelectionne() == null) return;

        int place = getDomaineSelectionne().getComptes().indexOf(getCompteSelectionne()) + 1;
        Compte nCompte = new Compte(getCompteSelectionne());
        nCompte.setUtilisateur(nCompte.getUtilisateur() + " 2");

        getDonneesActives().getHistorique().ajoutAction(ActionHistorique.ajoutCompte(place, getDomaineSelectionne(), nCompte));
        selection(0, place);
    }
    private void dupliquerDomaine() {
        if (getDomaineSelectionne() == null) return;

        int place = getDonneesActives().getDomaines().indexOf(getDomaineSelectionne()) + 1;
        Domaine nDom = new Domaine(getDomaineSelectionne());
        nDom.setNom(nDom.getNom() + " 2");

        getDonneesActives().getHistorique().ajoutAction(ActionHistorique.ajoutDomaine(place, getDonneesActives(), nDom));
        selection(1, place);
    }

    @FXML public boolean sauvegarderDialog() {
        if (!donneesActives.isAutorise()) return false;

        boolean result = false;

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choisir un emplacement de sauvegarde");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Sauvegarde", "*" + PasswordManager.SAVE_EXTENSION));
        fileChooser.setInitialDirectory(new File("."));

        File selectedFile = fileChooser.showSaveDialog(passwordManager.getStage());
        if (selectedFile != null) {
            fichierOuvert.setDepuisDrive(false);
            fichierOuvert.setFichier(selectedFile);
            fichierOuvert.saveToPreferences(getPasswordManager().getPreferences());
            result = ecrire(selectedFile.getAbsolutePath(), fichierOuvert);
            initUi();
        }

        return result;
    }
    @FXML private void sauvegarderDrive() {
        String v = "save.psw";
        if (!fichierOuvert.getNomDansDrive().equals(""))
            v = fichierOuvert.getNomDansDrive();

        TextInputDialog dialog = new TextInputDialog(v);
        dialog.setTitle("Choisir un nom");
        dialog.setHeaderText("Choisissez un nom pour le fichier");
        dialog.setContentText("Nom du fichier:");

        dialog.showAndWait().ifPresent(nom -> {
            fichierOuvert.setDepuisDrive(true);
            fichierOuvert.setNomDansDrive(nom);
            fichierOuvert.saveToPreferences(getPasswordManager().getPreferences());
            ecrire(nom, fichierOuvert);
            initUi();
        });
    }
    @FXML public void chargerDialog() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choisir un fichier de sauvegarde");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Sauvegarde", "*" + PasswordManager.SAVE_EXTENSION));
        fileChooser.setInitialDirectory(new File("."));

        File selectedFile = fileChooser.showOpenDialog(passwordManager.getStage());
        if (selectedFile != null) {
            if (!charger(new PSWFile(selectedFile.getAbsolutePath(), false), null)) return;

            initUi();
        }
    }
    @FXML private void chargerDriveDialog() {
        PSWFile selectedFile = driveHelper.chooseFile();

        if (selectedFile != null) {
            if (!telecharger(selectedFile, null)) return;

            initUi();
        }
    }

    @FXML public void fermer() {
        if (fichierOuvert != null && donneesActives.getEncrytionLevel() > 0 && donneesActives.isAutorise())
            if (ouvrir(fichierOuvert, null))
                initUi();
    }

    @FXML private void autoriser() {
        montrerOption(autorisationVue);
        autorisationControleur.initDonnees();
    }

    @FXML public boolean sauvegarderSc() { // shortcut
        if (fichierOuvert == null) return sauvegarderDialog();

        if (fichierOuvert.isDepuisDrive()) {
            String nom = !fichierOuvert.getNomDansDrive().equals("") ? fichierOuvert.getNomDansDrive() : "save.psw";
            return ecrire(nom, fichierOuvert);
        } else {
            return ecrire(fichierOuvert.getChemin(), fichierOuvert);
        }
    }

    @FXML private void ecraserFiltre() {
        tfFiltre.setText("");
    }

    private void montrerOption(Parent p) {
        if (!root.getChildren().contains(p)) root.getChildren().add(p);
        inOptions = true;
    }

    public boolean ouvrir(PSWFile f, Crypto c) {
        if (!checkSaveEnregistree()) return false;

        if (f.isDepuisDrive())
            return telecharger(f, c);
        else
            return charger(f, c);
    }
    public boolean ecrire(String n, PSWFile f) {
        if (!donneesActives.isAutorise() || f == null || n.equals("")) return false;

        if (f.isDepuisDrive())
            return televerser(n, f, getCrypto());
        else
            return sauvegarder(n, f, getCrypto());
    }

    private boolean sauvegarder(String chemin, PSWFile file, Crypto c) {
        try {
            Utils.writeSaveData(donneesActives, chemin, c);
            donneesActives.getHistorique().setSaved(true);
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }

        return true;
    }
    private boolean televerser(String nom, PSWFile file, Crypto c) {
        try {
            Utils.writeSaveData(donneesActives, nom, c);
            File f = new File(nom);
            file.setFichier(f);
            file.setNomDansDrive(nom);
            driveHelper.uploadFile(file);
            f.delete();
            file.setFichier(null);
            file.saveToPreferences(getPasswordManager().getPreferences());
            donneesActives.getHistorique().setSaved(true);
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }

        return true;
    }
    private boolean charger(PSWFile file, Crypto crypto) {
        nouvellesDonnees(Utils.readSavedData(file.getFichier(), crypto));
        if (donneesActives == null) { // erreur
            //nouvelleSauvegarde();
            System.err.println("Playback error!");
            return false;
        }

        fichierOuvert = file;
        fichierOuvert.saveToPreferences(getPasswordManager().getPreferences());
        miSauvegarder.setDisable(false);

        return true;
    }
    private boolean telecharger(PSWFile file, Crypto crypto) {
        File tmpFile = driveHelper.downloadFile(file);
        if (tmpFile != null)
            nouvellesDonnees(Utils.readSavedData(tmpFile, crypto));
        if (donneesActives == null) { // erreur
            //nouvelleSauvegarde();
            System.err.println("Playback error!");
            return false;
        } else if (!tmpFile.delete()) {
            System.err.println("Can not delete the temporary file!");
        }

        fichierOuvert = file;
        fichierOuvert.saveToPreferences(getPasswordManager().getPreferences());
        miSauvegarder.setDisable(false);

        return true;
    }

    void initUi() {
        bpEtat.getStyleClass().clear();
        bpEtat.getStyleClass().add((donneesActives.getEncrytionLevel() > 0 && !donneesActives.isAutorise() ? "bpEtatNA" : "bpEtatA"));
        if (fichierOuvert != null)
            lEtat.setText(fichierOuvert.getNomFichier() + " - edition " + (donneesActives.getEncrytionLevel() > 0 && !donneesActives.isAutorise() ? "forbidden" : "authorized"));
        else
            lEtat.setText("New File");
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
                return true;
            } else if (domaine.getDomaine().toLowerCase().contains(lowerCaseFilter)) {
                return true;
            }

            return false;
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
                parametresVue,
                explicationsVue,
                aboutVue,
                aleaSchemesVue
        );
        inOptions = false;
    }

    @FXML public void defaire() {
        donneesActives.getHistorique().retourArriere();
    }
    @FXML public void refaire() {
        donneesActives.getHistorique().retourAvant();
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
        lDetailsCategorie.textProperty().bind(d.categorieProperty());
        lDetailsComptesTaille.textProperty().bind(Bindings.size(d.getComptes()).asString());
        lDetailsNotes.textProperty().bind(Bindings.createStringBinding(() -> {
            if (d.getNotes().length() > 0) {
                lDetailsNotes.getStyleClass().clear();
                return d.getNotes();
            } else {
                lDetailsNotes.getStyleClass().add("lItalic");
                return "Not rated yet";
            }
        }, d.notesProperty()));

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
            passwordManager.getStage().setTitle(PasswordManager.TITLE + " - " + fichierOuvert.getNomFichier());
        else
            passwordManager.getStage().setTitle(PasswordManager.TITLE);
    }

    public ImageManager getImageManager() {
        return imageManager;
    }
    public Donnees getDonneesActives() {
        return donneesActives;
    }
    public Domaine getDomaineSelectionne() {
        return domaineSelectionne;
    }
    public Compte getCompteSelectionne() {
        return compteSelectionne;
    }
    public PasswordManager getPasswordManager() {
        return passwordManager;
    }
    PSWFile getFichierOuvert() {
        return fichierOuvert;
    }
    Random getRandom() {
        return random;
    }
    private Crypto getCrypto() {
        try {
            if (donneesActives.getEncrytionLevel() > 0 && donneesActives.getMotDePasse().length() > 5)
                return new Crypto(donneesActives.getMotDePasse());
        } catch (Exception ignored) {}

        return null;
    }
    public passwordManager.alea.AleaSchemes getAleaSchemes() {
        return aleaSchemes;
    }

    private void nouvellesDonnees(Donnees donnees) {
        donneesActives = donnees;
        backup.setDonnees(donneesActives);
    }

    public boolean checkSaveEnregistree() {
        if (getDonneesActives() == null || getDonneesActives().getHistorique().isSaved()) return true;

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Unsaved backup!");
        alert.setHeaderText("You will leave without saving your changes!");
        alert.setContentText("What do you want to do?");

        ButtonType sv = new ButtonType("Save to");
        ButtonType s = new ButtonType("Save");
        ButtonType q = new ButtonType("Leave");
        ButtonType a = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

        alert.getButtonTypes().setAll(sv, s, q, a);

        boolean quit = true;
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent()) {
            if (result.get() == sv) {
                quit = sauvegarderDialog();
            } else if (result.get() == s) {
                quit = sauvegarderSc();
            } else if (result.get() == q) {
                // rien
            } else {
                quit = false;
            }
        } else
            quit = false;

        return quit;
    }

    @FXML private void exit() {
        Platform.exit();
    }

    void updateWithPreferences() {
        Preferences preferences = passwordManager.getPreferences();

        donneesActives.getHistorique().setMaxSize(Integer.parseInt(preferences.getPropriete(Preferences.PROP_MAX_HISTORIQUE)));
        backup.setChemin(preferences.getPropriete(Preferences.PROP_DOSSIER_BACKUP_AUTO));
        backup.setInterval(Integer.parseInt(preferences.getPropriete(Preferences.PROP_BACKUP_AUTO)));
    }

    private void setAnchor(Node n) {
        AnchorPane.setTopAnchor(n, .0);
        AnchorPane.setRightAnchor(n, .0);
        AnchorPane.setBottomAnchor(n, .0);
        AnchorPane.setLeftAnchor(n, .0);
    }
    private void setAnchor(Node ...n) {
        for (Node ns : n)
            setAnchor(ns);
    }
}

