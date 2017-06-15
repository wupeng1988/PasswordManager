package passwordManager;

import java.io.File;

/**
 * Nico on 14/06/2017.
 */
public class PSWFile {
    private File fichier = null;
    private String nomDansDrive = "";
    private String idDansDrive = "";
    private boolean depuisDrive = false;

    public PSWFile() {}
    public PSWFile(Preferences preferences) {
        String lastFile = preferences.getPropriete(Preferences.PROP_DERNIER_FICHIER_CHEMIN);
        Boolean lastFileDrive = Boolean.parseBoolean(preferences.getPropriete(Preferences.PROP_DERNIER_FICHIER_DRIVE));

        if (!Boolean.parseBoolean(preferences.getPropriete(Preferences.PROP_CHARGER_DERNIER_FICHIER)) || lastFile == null || lastFile.equals(""))
            changerFichier(null, false);
        else {
            changerFichier(lastFile, lastFileDrive);
        }
    }
    public PSWFile(String nomFichier) {
        this(nomFichier, false);
    }
    public PSWFile(String nomFichier, boolean depuisDrive) {
        changerFichier(nomFichier, depuisDrive);
    }

    public void changerFichier(String nomFichier, boolean depuisDrive) {
        if (depuisDrive) {
            setIdDansDrive(nomFichier);
            setNomDansDrive("");
            setDepuisDrive(true);
        } else {
            setFichier(nomFichier != null && !nomFichier.equals("") ? new File(nomFichier) : null);
            setDepuisDrive(false);
        }
    }

    public void setNomDansDrive(String nomDansDrive) {
        if (nomDansDrive.equals(getNomDansDrive())) return;

        this.nomDansDrive = nomDansDrive;
        setIdDansDrive("");
    }
    public void setIdDansDrive(String idDansDrive) {
        this.idDansDrive = idDansDrive;
    }
    public void setFichier(File fichier) {
        this.fichier = fichier;
    }
    public void setDepuisDrive(boolean depuisDrive) {
        this.depuisDrive = depuisDrive;
    }

    public String getNomDansDrive() {
        return nomDansDrive;
    }
    public String getIdDansDrive() {
        return idDansDrive;
    }
    public String getNomFichier() {
        if (!exists()) return "Nouveau fichier";
        else if (isDepuisDrive()) return "gdrive://" + getNomDansDrive();

        String[] p = Utils.toLocalPath(getFichier().getAbsolutePath()).split(File.separator + File.separator);
        String ps = p[p.length - 1];
        String[] e = ps.split("\\.");
        return ps.replace("." + e[e.length - 1], "");
    }
    public File getFichier() {
        return fichier;
    }
    public boolean isDepuisDrive() {
        return depuisDrive;
    }
    public String getChemin() {
        return (isDepuisDrive() ? getIdDansDrive() : Utils.toLocalPath(fichier.getAbsolutePath()));
    }
    public boolean exists() {
        return (isDepuisDrive() || fichier != null);
    }

    public void saveToPreferences(Preferences preferences) {
        preferences.setPropriete(Preferences.PROP_DERNIER_FICHIER_CHEMIN, getChemin());
        preferences.setPropriete(Preferences.PROP_DERNIER_FICHIER_DRIVE, String.valueOf(isDepuisDrive()));
    }

    @Override
    public String toString() {
        return "PSWFile{" +
                "fichier=" + fichier +
                ", nomDansDrive='" + nomDansDrive + '\'' +
                ", idDansDrive='" + idDansDrive + '\'' +
                ", depuisDrive=" + depuisDrive +
                '}';
    }
}
