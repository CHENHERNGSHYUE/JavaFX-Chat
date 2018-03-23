package wxs.org.encry;
import java.io.*;
import java.util.*;
import java.security.*;
import javax.crypto.*;
import javax.crypto.spec.*;
import org.apache.commons.codec.binary.Base64;


public class DesEncrypter {

    private Cipher encryptCipher = null;
    private Cipher decryptCipher = null;


    public DesEncrypter(SecretKey key) throws Exception {
        encryptCipher = Cipher.getInstance("DES");
        decryptCipher = Cipher.getInstance("DES");
        encryptCipher.init(Cipher.ENCRYPT_MODE, key);
        decryptCipher.init(Cipher.DECRYPT_MODE, key);
    }    

    //Encrypt a string using DES encryption, and return the encrypted
    public String encryptBase64 (String unencryptedString) throws Exception {
        // Encode the string into bytes using utf-8
        byte[] unencryptedByteArray = unencryptedString.getBytes("UTF8");

        // Encrypt
        byte[] encryptedBytes = encryptCipher.doFinal(unencryptedByteArray);

        // Encode bytes to base64 to get a string
        byte [] encodedBytes = Base64.encodeBase64(encryptedBytes);

        return new String(encodedBytes); 
    }

    //Decrypt a base64 encoded, DES encrypted string and return
    public String decryptBase64 (String encryptedString) throws Exception {
        // Encode bytes to base64 to get a string
        byte [] decodedBytes = Base64.decodeBase64(encryptedString.getBytes());

        // Decrypt
        byte[] unencryptedByteArray = decryptCipher.doFinal(decodedBytes);

        // Decode using utf-8
        return new String(unencryptedByteArray, "UTF8");
    }    

    public static String encrybyDEC(String input) throws Exception {
    	String password = "abcd1234";
        DESKeySpec key = new DESKeySpec(password.getBytes());
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");

        //Instantiate the encrypter/decrypter
        DesEncrypter crypt = new DesEncrypter(keyFactory.generateSecret(key));
        String res = crypt.encryptBase64(input);
        return res;
    }
    
    public static String dencrybyDEC(String input) throws Exception {
    	String password = "abcd1234";
        DESKeySpec key = new DESKeySpec(password.getBytes());
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");

        //Instantiate the encrypter/decrypter
        DesEncrypter crypt = new DesEncrypter(keyFactory.generateSecret(key));
        String res = crypt.decryptBase64(input);
        return res;
    }
    public static void main(String args[]) {
        try {
            //Generate the secret key
            String password = "abcd1234";
            DESKeySpec key = new DESKeySpec(password.getBytes());
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");

            //Instantiate the encrypter/decrypter
            DesEncrypter crypt = new DesEncrypter(keyFactory.generateSecret(key));
            String unencryptedString = "admin2017";
            String encryptedString = crypt.encryptBase64(unencryptedString);
            // Encrypted String:8dKft9vkZ4I=
            System.out.println("Encrypted String:"+encryptedString);

            //Decrypt the string
            unencryptedString = crypt.decryptBase64(encryptedString);
            // UnEncrypted String:Message
            System.out.println("UnEncrypted String:"+unencryptedString);

        } catch (Exception e) {
            System.err.println("Error:"+e.toString());
        }
    }
}