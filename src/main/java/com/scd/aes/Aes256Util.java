package com.scd.aes;

import com.scd.exception.DecryptException;
import com.scd.exception.EncryptException;
import com.scd.util.UnicodeUtil;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Security;

/**
 * @author chengdu
 * 256位 32字节 key
 * 秘钥key 长度支持 128/192/256 位  16、24、32字节
 */
public class Aes256Util {

    public static boolean initialized = false;

    public static final String UTF8ENCODE = "utf-8";

    public static final String ALGORITHM_PKCS7Padding = "AES/ECB/PKCS7Padding";

    private static final String ALGORITHM_PKCS5Padding = "AES/ECB/PKCS5Padding";

    public static byte[] aes256Encode(String str, String key){
        initialize();
        try{
            Cipher cipher = Cipher.getInstance(ALGORITHM_PKCS7Padding, "BC");
            SecretKeySpec keySpec = new SecretKeySpec(key.getBytes(UTF8ENCODE), "AES");
            cipher.init(Cipher.ENCRYPT_MODE, keySpec);
            return cipher.doFinal(str.getBytes(UTF8ENCODE));
        } catch (NoSuchAlgorithmException | BadPaddingException | InvalidKeyException | NoSuchPaddingException | NoSuchProviderException | IllegalBlockSizeException | UnsupportedEncodingException e) {
            throw new EncryptException("aes encrypt error ", e);
        }
    }

    public static String aes256Decode(byte[] bytes, String key){
        initialize();
        try{
            Cipher cipher = Cipher.getInstance(ALGORITHM_PKCS7Padding, "BC");
            SecretKeySpec keySpec = new SecretKeySpec(key.getBytes(UTF8ENCODE), "AES");
            cipher.init(Cipher.DECRYPT_MODE, keySpec);
            byte[] decoded = cipher.doFinal(bytes);
            return new String(decoded, UTF8ENCODE);
        } catch (NoSuchAlgorithmException | BadPaddingException | InvalidKeyException | NoSuchPaddingException | NoSuchProviderException | IllegalBlockSizeException | UnsupportedEncodingException e) {
            throw new DecryptException("aes decrypt error ", e);
        }
    }

    public static byte[] aes256EncrptyDefault(String str, String key) throws Exception{
        Cipher cipher = Cipher.getInstance(ALGORITHM_PKCS5Padding);
        SecretKeySpec keySpec = new SecretKeySpec(key.getBytes(UTF8ENCODE), "AES");
        cipher.init(Cipher.ENCRYPT_MODE, keySpec);
        return cipher.doFinal(str.getBytes(UTF8ENCODE));
    }

    public static String aes256dcrptyDefault(byte[] bytes, String key) throws Exception{
        Cipher cipher = Cipher.getInstance(ALGORITHM_PKCS5Padding);
        cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(key.getBytes(UTF8ENCODE), "AES"));
        bytes = cipher.doFinal(bytes);
        return new String(bytes, UTF8ENCODE);
    }


    public static void initialize(){
        if (initialized) {
            return;
        }
        Security.addProvider(new BouncyCastleProvider());
        initialized = true;
    }

    public static String generateKey(int size){
        StringBuffer sb = new StringBuffer();
        for(int i=0; i < size; i++){
            sb.append("1");
        }
        return sb.toString();
    }

    public static void main(String[] args) throws Exception{
        String key = generateKey(256/8);
        System.out.println(key.length());
        byte[] encrptyArr = aes256EncrptyDefault("成都", key);
        System.out.println(encrptyArr);
        String originStr = aes256dcrptyDefault(encrptyArr, key);
        System.out.println(originStr);
        byte[] pkcs7Attr = aes256Encode("成都",key);
        System.out.println(pkcs7Attr);
        String orginstr7 = aes256Decode(pkcs7Attr, key);
        System.out.println(orginstr7);
        // 6 * 5 + 2
        String testCnKey = "嗨成都中文";
        String unicodeKey = UnicodeUtil.gbEncoding(testCnKey) + "@#";
        byte[] pkcs7CnArr = aes256Encode("成都", unicodeKey);
        System.out.println(pkcs7CnArr);
        String decodeCn = aes256Decode(pkcs7CnArr, unicodeKey);
        System.out.println(decodeCn);
    }
}
