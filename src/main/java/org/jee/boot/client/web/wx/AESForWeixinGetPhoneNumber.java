package org.jee.boot.client.web.wx;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.AlgorithmParameters;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.Security;
import java.security.spec.InvalidParameterSpecException;
import java.util.Base64;

@Slf4j
public class AESForWeixinGetPhoneNumber {
    //加密方式
    private static String keyAlgorithm = "AES";
    //避免重复new生成多个BouncyCastleProvider对象，因为GC回收不了，会造成内存溢出
    //只在第一次调用decrypt()方法时才new 对象
    private static boolean initialized = false;
    //用于Base64解密
    private Base64.Decoder decoder = Base64.getDecoder();

    //待解密的数据
    private String originalContent;
    //会话密钥sessionKey
    private String encryptKey;
    //加密算法的初始向量
    private String iv;

    public AESForWeixinGetPhoneNumber(String originalContent, String encryptKey, String iv) {
        this.originalContent = originalContent;
        this.encryptKey = encryptKey;
        this.iv = iv;
    }

    /**
     * AES解密
     * 填充模式AES/CBC/PKCS7Padding
     * 解密模式128
     *
     * @return 解密后的信息对象
     */
    public String decrypt() {
        log.info("加密的敏感数据:" + this.originalContent);
        log.info("初始向量:" + iv);
        log.info("会话密钥:" + encryptKey);
        initialize();
        try {
            //数据填充方式
            //Cipher cipher = Cipher.getInstance(“AES/CBC/PKCS7Padding”,”BC”);
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding");
            Key sKeySpec = new SecretKeySpec(decoder.decode(this.encryptKey), keyAlgorithm);
            // 初始化
            cipher.init(Cipher.DECRYPT_MODE, sKeySpec, generateIV(decoder.decode(this.iv)));
            byte[] data = cipher.doFinal(decoder.decode(this.originalContent));
            String datastr = new String(data, StandardCharsets.UTF_8);
            log.info("微信手机号解密:" + datastr);
            String phoneNum = JSONObject.parseObject(datastr).get("phoneNumber").toString();
            return phoneNum;
        } catch (Exception e) {
            log.error("微信解密异常:", e);
            return null;
        }
    }

    /**
     * BouncyCastle作为安全提供，防止我们加密解密时候因为jdk内置的不支持改模式运行报错。
     **/
    private static void initialize() {
        if (initialized) {
            return;
        }
        Security.addProvider(new BouncyCastleProvider());
        initialized = true;
    }

    // 生成iv
    private static AlgorithmParameters generateIV(byte[] iv) throws NoSuchAlgorithmException, InvalidParameterSpecException {
        AlgorithmParameters params = AlgorithmParameters.getInstance(keyAlgorithm);
        params.init(new IvParameterSpec(iv));
        return params;
    }

    public static Object decrypt(String encryptedData, String iv, String sessionKey) {
        String appId = "XXXXXXXXX";
        AESForWeixinGetPhoneNumber aes = new AESForWeixinGetPhoneNumber(encryptedData, sessionKey, iv);
        return aes.decrypt();
    }

    public static void main(String[] args) {
        decrypt("69D1hgWDzVzwlIy/zZt/vuWuQ3QGg9O15QnCu2ccVKB8cPBQwmGoVEUint9pYWFTnFEwceKn3hAvcKNwG8jnNQ5n4rhdMeiwP6tUw2/zg3ExQMdKt8lOJ6pWyV63vDMdqrAFJy9D0C67aQvvjHnVjFtR/M8ZLxlbmx+d9fsP2GodKZyjLjKtfGZT2xj4cbaMdqLf6ZatCKBMKlbQ6Vf0YA==", "sxtJxnRBZENhgBIXn70mbw==", "vNBdhVUKWzvj3DVB11Fmdw==");
    }
}
