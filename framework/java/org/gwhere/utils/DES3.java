package org.gwhere.utils;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import java.security.SecureRandom;

/**
 * Created by jiangtao on 16/3/30.
 */
public class DES3 {

    public static byte[] generateKey() {
        try {
            SecureRandom secureRandom = new SecureRandom();
            KeyGenerator _generator = KeyGenerator.getInstance("DES");
            _generator.init(56, secureRandom);
            SecretKey key = _generator.generateKey();
            return key.getEncoded();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void main(String[] args) {
        byte[] key = generateKey();
        System.out.println(new String(key));
        String s = "user&111";
        String ss = encryptDES(s, key);
        System.out.println(ss);
        String sss = decryptDES(ss, key);
        System.out.println(sss);
    }

    public static String encryptDES(String encryptString, byte[] key) {
        try {
            DESKeySpec dks = new DESKeySpec(key);
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
            SecretKey secretKey = keyFactory.generateSecret(dks);
            Cipher cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            byte[] b = cipher.doFinal(encryptString.getBytes());
            BASE64Encoder encoder = new BASE64Encoder();
            return encoder.encode(b);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String decryptDES(String decryptString, byte[] key) {
        try {
            BASE64Decoder decoder = new BASE64Decoder();
            byte[] bytes = decoder.decodeBuffer(decryptString);
            DESKeySpec dks = new DESKeySpec(key);
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
            SecretKey secretKey = keyFactory.generateSecret(dks);
            Cipher cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            byte[] retByte = cipher.doFinal(bytes);
            return new String(retByte);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
