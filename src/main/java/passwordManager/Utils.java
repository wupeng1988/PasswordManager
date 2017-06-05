package passwordManager;

import passwordManager.model.Compte;
import passwordManager.model.Domaine;
import passwordManager.model.Donnees;

import javax.crypto.*;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;
import java.io.*;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Scanner;

/**
 * Nico on 05/06/2017.
 */
public class Utils {
    // Crypto from https://stackoverflow.com/questions/13673556/using-password-based-encryption-on-a-file-in-java

    // TODO: random salt
    private static final byte[] SALT = {
            (byte) 0x43, (byte) 0x76, (byte) 0x95, (byte) 0xc7,
            (byte) 0x5b, (byte) 0xd7, (byte) 0x45, (byte) 0x17
    };
    private static final String PASSWORD = "Password";

    public static void encryptFile(String inFileName, String outFileName, char[] pass) throws IOException, GeneralSecurityException {
        Cipher cipher = makeCipher(pass, true);
        try (CipherOutputStream cipherOutputStream = new CipherOutputStream(new FileOutputStream(outFileName), cipher);
             BufferedInputStream bis = new BufferedInputStream(new FileInputStream(inFileName))) {
            int i;
            while ((i = bis.read()) != -1) {
                cipherOutputStream.write(i);
            }
        }
    }

    public static void decryptFile(String inFileName, String outFileName, char[] pass) throws GeneralSecurityException, IOException {
        Cipher cipher = makeCipher(pass, false);
        try (CipherInputStream cipherInputStream = new CipherInputStream(new FileInputStream(inFileName), cipher);
             BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(outFileName))) {
            int i;
            while ((i = cipherInputStream.read()) != -1) {
                bos.write(i);
            }
        }
    }

    private static Cipher makeCipher(char[] pass, Boolean decryptMode) throws GeneralSecurityException {
        // Use a KeyFactory to derive the corresponding key from the passphrase:
        PBEKeySpec keySpec = new PBEKeySpec(pass);
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("PBEWithMD5AndDES");
        SecretKey key = keyFactory.generateSecret(keySpec);

        // Create parameters from the salt and an arbitrary number of iterations:
        PBEParameterSpec pbeParamSpec = new PBEParameterSpec(SALT, 43);

        // Set up the cipher:
        Cipher cipher = Cipher.getInstance("PBEWithMD5AndDES");

        // Set the cipher mode to decryption or encryption:
        if (decryptMode) {
            cipher.init(Cipher.ENCRYPT_MODE, key, pbeParamSpec);
        } else {
            cipher.init(Cipher.DECRYPT_MODE, key, pbeParamSpec);
        }

        return cipher;
    }

    public static void writeSaveData(Donnees donnees, String location) {
        File f = new File(location);
        File t = new File(location + ".tmp");

        StringBuilder sb = new StringBuilder();
        String toWrite;
        for (Domaine d : donnees.getDomaines()) {
            sb.append(d.getNom() + ";" + d.getDomaine() + ";" + d.getIconeLocation() + ";" + d.getComptes().size() + ";" + d.getNotes() + "\n");
            for (Compte c : d.getComptes()) {
                sb.append("\t" + c.getUtilisateur() + "//" + c.getMotDePasse() + "//" + c.getNotes() + "\n");
            }
        }

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(t))) {
            bw.write(sb.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            encryptFile(t.getAbsolutePath(), f.getAbsolutePath(), PASSWORD.toCharArray());
        }
        catch (Exception ignored) {}
        finally {
            t.delete();
        }
    }
    public static Donnees readSavedData(String location, boolean encrypted) {
        ArrayList<Domaine> domaines = new ArrayList<>();
        String line, parts[];
        int nbEntrees;
        Domaine dtmp;
        Compte ctmp;

        File f, t;
        if (encrypted) {
            t = new File(location);
            f = new File(location + ".tmp");
            try {
                decryptFile(t.getAbsolutePath(), f.getAbsolutePath(), PASSWORD.toCharArray());
            }
            catch (Exception ignored) {}
        } else {
            f = new File(location);
        }

        if (!f.exists() || !f.isFile())
            return new Donnees();

        try (Scanner s = new Scanner(f)) {
            while ((line = s.nextLine()) != null && line.charAt(0) == '#') {} // Avancer jusqu'aux données utiles

            while (line != null) {
                parts = line.split(";", 5); // DomaineNom;nbEntrees
                dtmp = new Domaine(parts[0]);
                dtmp.setDomaine(parts[1]);
                dtmp.setNotes(parts[4]);
                dtmp.setIconeLocation(parts[2]);
                domaines.add(dtmp);
                nbEntrees = Integer.parseInt(parts[3]);

                while (nbEntrees-- > 0 && (line = s.nextLine()) != null) {
                    parts = line.split("//", 3); // \tuser//psw
                    ctmp = new Compte(parts[0].trim(), parts[1]);
                    ctmp.setNotes(parts[2]);
                    dtmp.addCompte(ctmp);
                }

                if (line != null) line = s.nextLine();
            }
        }
        catch (NoSuchElementException ignored) {}
        catch (IOException io) {
            io.printStackTrace();
        }

        if (encrypted) {
            f.delete();
        }

        return new Donnees(domaines);
    }
}
