package passwordManager;

import passwordManager.model.Donnees;

import java.io.File;
import java.util.Observable;
import java.util.Observer;

/**
 * Nico on 10/06/2017.
 */
public class Backup implements Observer {
    private Donnees donnees;

    private String chemin;
    private final static int MAX = 5;
    private int interval;

    private int nb;

    private final static String PREFIX = "backup_";

    public Backup(String chemin, int interval) {
        this.chemin = chemin;
        this.interval = interval;

        analyseDossier();
    }

    private void doBackup() {
        try {
            Crypto c = null;
            if (donnees.getMotDePasse().length() > 5)
                c = new Crypto(donnees.getMotDePasse());
            File f = new File(chemin + File.separator + PREFIX + nb + PasswordManager.SAVE_EXTENSION);
            Utils.writeSaveData(donnees, f.getAbsolutePath(), c);
            nb++;
        } catch (Exception e) {
            e.printStackTrace();
        }

        checkMax();
    }

    private void checkMax() {
        File c = new File(chemin);
        File[] files = c.listFiles();

        if (files != null && files.length > MAX) {
            if (!files[0].delete())
                System.err.println("Can not delete a backup file");
        }
    }
    private void analyseDossier() {
        File c = new File(chemin);
        if (!c.exists()) if(!c.mkdir()) return;

        int m = 0;
        File[] files = c.listFiles();
        if (files != null) {
            for (File f : files) {
                String e = f
                        .getAbsolutePath()
                        .replace(c.getAbsolutePath() + File.separator + PREFIX, "")
                        .replace(PasswordManager.SAVE_EXTENSION, "");
                int num = Integer.parseInt(e);
                if (num > m)
                    m = num;
            }
        }

        nb = m;
    }

    public void setDonnees(Donnees donnees) {
        this.donnees = donnees;
        donnees.getHistorique().addObserver(this);
    }
    public void setChemin(String chemin) {
        this.chemin = chemin;
    }
    public void setInterval(int interval) {
        this.interval = interval;
    }

    @Override
    public void update(Observable o, Object arg) {
        if (((int) arg) % interval == 0)
            doBackup();
    }

    @Override
    public String toString() {
        return "Backup{" +
                "donnees=" + donnees +
                ", chemin='" + chemin + '\'' +
                ", interval=" + interval +
                ", nb=" + nb +
                '}';
    }
}
