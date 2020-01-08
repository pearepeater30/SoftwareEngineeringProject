package Tools;

import org.jasypt.util.text.BasicTextEncryptor;

public class EncryptionModule {

  /**Storing the key for the encryptor*/
  private static final String password = "secret-pass";

  /**
   * @param plaintext
   * @return
   * Encrypts plaintext taken as a parameter
   */
  public static String Encrypt(String plaintext){
    /**Creating BasicTextEncryptor object*/
    BasicTextEncryptor passwordEncryptor = new BasicTextEncryptor();
    /**Setting the key for the encryptor as a CharArray*/
    passwordEncryptor.setPasswordCharArray(password.toCharArray());
    /**Returning the encrypted Text*/
    return passwordEncryptor.encrypt(plaintext);
  }

  /**
   * @param plaintext
   * @return
   * Decrypts ciphertext taken as a parameter
   */
  public static String Decrypt(String plaintext){
    /**Creating BasicTextEncryptor Object*/
    BasicTextEncryptor passwordEncryptor = new BasicTextEncryptor();
    /**Setting the key for the encryptor as a CharArray*/
    passwordEncryptor.setPasswordCharArray(password.toCharArray());
    /**Returning the decrypted Text*/
    return passwordEncryptor.decrypt(plaintext);
  }
}
