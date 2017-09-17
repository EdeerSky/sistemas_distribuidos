/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.

    https://github.com/1MansiS/java_crypto/blob/master/cipher/SecuredRSAUsage.java
 */
package sisdist1;

import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Base64;
;
import javax.crypto.Cipher;
import java.lang.Exception;
import java.security.Key;
import java.security.KeyPair;



public class SecuredRSAUsage {

    int RSA_KEY_LENGTH = 16;
    String ALGORITHM_NAME = "RSA";
    String PADDING_SCHEME = "OAEPWITHSHA-512ANDMGF1PADDING";
    String MODE_OF_OPERATION = "ECB"; // This essentially means none behind the scene
    KeyPair rsaKeyPair;
    PublicKey publicKey;
    PrivateKey privateKey;

    public void SecuredRSAUsage() {
        //String shortMessage = "oi tudo bom" ;

        try {

            // Generate Key Pairs
            KeyPairGenerator rsaKeyGen = KeyPairGenerator.getInstance(ALGORITHM_NAME);
            rsaKeyGen.initialize(RSA_KEY_LENGTH);
            rsaKeyPair = rsaKeyGen.generateKeyPair();
            publicKey = rsaKeyPair.getPublic();
            privateKey = rsaKeyPair.getPrivate();

            //String encryptedText = rsaEncrypt(shortMessage, publicKey);
            //String decryptedText = rsaDecrypt(Base64.getDecoder().decode(encryptedText), privateKey) ;
            //System.out.println("Encrypted text = " + encryptedText) ;
            //System.out.println("Decrypted text = " + decryptedText) ;
        } catch (Exception e) {
            System.out.println("Exception while encryption/decryption");
            e.printStackTrace();
        }

    }

    public PublicKey getPublicKey() {
        return publicKey;
    }

    public PrivateKey getPrivateKey() {
        return privateKey;
    }

    public String rsaEncrypt(String message, Key publicKey) throws Exception {

        Cipher c = Cipher.getInstance(ALGORITHM_NAME + "/" + MODE_OF_OPERATION + "/" + PADDING_SCHEME);

        c.init(Cipher.ENCRYPT_MODE, publicKey);

        byte[] cipherTextArray = c.doFinal(message.getBytes());

        return Base64.getEncoder().encodeToString(cipherTextArray);

    }

    public String rsaDecrypt(byte[] encryptedMessage, Key privateKey) throws Exception {
        Cipher c = Cipher.getInstance(ALGORITHM_NAME + "/" + MODE_OF_OPERATION + "/" + PADDING_SCHEME);
        c.init(Cipher.DECRYPT_MODE, privateKey);
        byte[] plainText = c.doFinal(encryptedMessage);

        return new String(plainText);

    }
}
