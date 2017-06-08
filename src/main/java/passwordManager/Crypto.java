package passwordManager;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;
import java.security.spec.KeySpec;
import java.util.Base64;

/**
 * Nico on 06/06/2017.
 */
/*
0: aucune encryption
1: mot de passes des comptes encryptés
2: mot de passes + noms des comptes encryptés
3: encryption totale
 */
public class Crypto {
    private static final String UNICODE_FORMAT = "UTF8";
    private static final String DESEDE_ENCRYPTION_SCHEME = "DESede";

    private Cipher cipher;
    private SecretKey key;

    private String password;

    public Crypto(String password) throws Exception {
        this.password = completerPassword(password);
        String myEncryptionScheme = DESEDE_ENCRYPTION_SCHEME;
        byte[] arrayByte = this.password.getBytes(UNICODE_FORMAT);
        KeySpec ks = new DESedeKeySpec(arrayByte);
        SecretKeyFactory skf = SecretKeyFactory.getInstance(myEncryptionScheme);
        cipher = Cipher.getInstance(myEncryptionScheme);
        key = skf.generateSecret(ks);
    }

    String encrypt(String unencryptedString) {
        String encryptedString = null;
        try {
            cipher.init(Cipher.ENCRYPT_MODE, key);
            byte[] plainText = unencryptedString.getBytes(UNICODE_FORMAT);
            byte[] encryptedText = cipher.doFinal(plainText);
            encryptedString = new String(Base64.getEncoder().encode(encryptedText));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return encryptedString;
    }

    String decrypt(String encryptedString) {
        String decryptedString = null;
        try {
            cipher.init(Cipher.DECRYPT_MODE, key);
            byte[] encryptedText = Base64.getDecoder().decode(encryptedString);
            byte[] plainText = cipher.doFinal(encryptedText);
            decryptedString = new String(plainText);
        } catch (Exception ignored) {}
        return decryptedString;
    }

    private String completerPassword(String password) {
        if (password.length() >= 24 || password.length() == 0) return password;

        String r = new StringBuilder(password).reverse().toString();

        StringBuilder pswcBuilder = new StringBuilder();
        while (pswcBuilder.length() < 24) {
            pswcBuilder.append(r);
        }

        return pswcBuilder.toString();
    }
}
