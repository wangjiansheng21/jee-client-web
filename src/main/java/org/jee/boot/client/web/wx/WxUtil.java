package org.jee.boot.client.web.wx;


import com.alibaba.fastjson.JSON;
import com.xiaoleilu.hutool.json.JSONObject;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLDecoder;
import java.security.AlgorithmParameters;
import java.security.Security;
import java.util.Arrays;
import java.util.Base64;

public class WxUtil {

    public static String getWxPhoneNumber(String encryptedData, String session_key, String iv) {
        // 被加密的数据
        byte[] dataByte = Base64.getDecoder().decode(encryptedData);
        // 加密秘钥
        byte[] keyByte = Base64.getDecoder().decode(session_key);
        // 偏移量
        byte[] ivByte = Base64.getDecoder().decode(iv);
        try {
            // 如果密钥不足16位，那么就补足.  这个if 中的内容很重要
            int base = 16;
            if (keyByte.length % base != 0) {
                int groups = keyByte.length / base + (keyByte.length % base != 0 ? 1 : 0);
                byte[] temp = new byte[groups * base];
                Arrays.fill(temp, (byte) 0);
                System.arraycopy(keyByte, 0, temp, 0, keyByte.length);
                keyByte = temp;
            }
            // 初始化
            Security.addProvider(new BouncyCastleProvider());
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            SecretKeySpec spec = new SecretKeySpec(keyByte, "AES");
            AlgorithmParameters parameters;
            parameters = AlgorithmParameters.getInstance("AES");
            parameters.init(new IvParameterSpec(ivByte));
            cipher.init(Cipher.DECRYPT_MODE, spec, parameters);// 初始化
            byte[] resultByte = cipher.doFinal(dataByte);
            if (null != resultByte && resultByte.length > 0) {

                String result = new String(resultByte, "UTF-8");
                return JSON.parseObject(result).get("phoneNumber").toString();
//                return JSONObject.parseObject(result);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void main(String[] args) {

        String encryptedDataEncode = "zuTsg2GMceplNZOFYCgN0uGyA79e5ioTdxaa6R0nRXpaiOuAtTlsD%2ByD%2BEWxGYJkBtCpd0Cl7qsXIGkvk777N%2F9hL5uzzP%2F6IhUTpsdyJe5OUajB%2F4aHXUtOLwgktv1%2F6dIDkywCLbR1xvk5MTYbe63y%2FqSofkoXXMRJPA4itfo4RinQhcrtYrtbdsfoG3vxfWbDuPhpQrj7gYjRVur7FQ%3D%3D&oq=zuTsg2GMceplNZOFYCgN0uGyA79e5ioTdxaa6R0nRXpaiOuAtTlsD%2ByD%2BEWxGYJkBtCpd0Cl7qsXIGkvk777N%2F9hL5uzzP%2F6IhUTpsdyJe5OUajB%2F4aHXUtOLwgktv1%2F6dIDkywCLbR1xvk5MTYbe63y%2FqSofkoXXMRJPA4itfo4RinQhcrtYrtbdsfoG3vxfWbDuPhpQrj7gYjRVur7FQ%3D%3D";
        String sessionKeyEncode = "vNBdhVUKWzvj3DVB11Fmdw==";
        String ivEncode = "B1PyjrgWEm7gXWF5jqWMyw==";
        String encryptedData = URLDecoder.decode(encryptedDataEncode);
        String sessionKey = URLDecoder.decode(sessionKeyEncode);
        String iv = URLDecoder.decode(ivEncode);
        Object obj = getWxPhoneNumber(encryptedData, sessionKey, iv);
        System.out.println(obj);
        //{"phoneNumber":"18800000000","watermark":{"appid":"wx2ba363fc4454f27c","timestamp":1586333901},"purePhoneNumber":"18800000000","countryCode":"86"}
    }
}
