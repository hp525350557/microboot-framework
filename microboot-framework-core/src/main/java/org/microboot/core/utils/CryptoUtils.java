package org.microboot.core.utils;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.crypto.cipher.CryptoCipher;
import org.apache.commons.crypto.cipher.CryptoCipherFactory;
import org.apache.commons.crypto.cipher.CryptoCipherFactory.CipherProvider;
import org.apache.commons.crypto.utils.Utils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.microboot.core.entity.Token;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Map;
import java.util.Properties;

/**
 * @author 胡鹏
 */
public class CryptoUtils {

    private static final Logger logger = LogManager.getLogger(CryptoUtils.class);
    private static final String defaultSalt = "0123456789abcdef";
    private static final String key = "microboot.";
    private static final String keyAlgorithm = "AES";
    private static final String cipherAlgorithm = "AES/CBC/PKCS5Padding";
    private static final Properties properties = new Properties();

    static {
        properties.setProperty(CryptoCipherFactory.CLASSES_KEY, CipherProvider.JCE.getClassName());
    }

    /**
     * base64编码
     *
     * @param input
     * @return
     */
    public static String encodeToString(byte[] input) {
        if (input == null) {
            return null;
        }
        return Base64.encodeBase64URLSafeString(input);
    }

    /**
     * base64编码
     *
     * @param text
     * @return
     */
    public static String encodeToString(String text) {
        if (StringUtils.isBlank(text)) {
            return text;
        }
        return encodeToString(getUTF8Bytes(text));
    }

    /**
     * base64解码
     *
     * @param input
     * @return
     */
    public static String decodeToString(byte[] input) {
        if (input == null) {
            return null;
        }
        return new String(Base64.decodeBase64(input));
    }

    /**
     * base64解码
     *
     * @param text
     * @return
     */
    public static String decodeToString(String text) {
        if (StringUtils.isBlank(text)) {
            return text;
        }
        return decodeToString(getUTF8Bytes(text));
    }

    /**
     * 加密
     *
     * @param text
     * @param salt
     * @return
     */
    public static String encrypt(String text, String salt) {
        if (StringUtils.isBlank(text) || StringUtils.isBlank(salt)) {
            return null;
        }
        byte[] newKey = DigestUtils.md5(key + salt);
        SecretKeySpec secretKeySpec = new SecretKeySpec(newKey, keyAlgorithm);
        IvParameterSpec ivParameterSpec = new IvParameterSpec(newKey);
        try (CryptoCipher encipher = Utils.getCipherInstance(cipherAlgorithm, properties)) {
            byte[] input = getUTF8Bytes(text);
            byte[] output = new byte[input.length + 16];
            encipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivParameterSpec);
            int updateBytes = encipher.update(input, 0, input.length, output, 0);
            int finalBytes = encipher.doFinal(input, 0, 0, output, updateBytes);
            return encodeToString(Arrays.copyOf(output, updateBytes + finalBytes));
        } catch (Exception e) {
            LoggerUtils.error(logger, e);
        }
        return null;
    }

    /**
     * 加密
     *
     * @param text
     * @return
     */
    public static String encrypt(String text) {
        return encrypt(text, defaultSalt);
    }

    /**
     * 解密
     *
     * @param text
     * @param salt
     * @return
     */
    public static String decrypt(String text, String salt) {
        if (StringUtils.isBlank(text) || StringUtils.isBlank(salt)) {
            return null;
        }
        byte[] newKey = DigestUtils.md5(key + salt);
        SecretKeySpec secretKeySpec = new SecretKeySpec(newKey, keyAlgorithm);
        IvParameterSpec ivParameterSpec = new IvParameterSpec(newKey);
        try (CryptoCipher decipher = Utils.getCipherInstance(cipherAlgorithm, properties)) {
            byte[] input = Base64.decodeBase64(text.getBytes());
            byte[] output = new byte[input.length + 16];
            decipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivParameterSpec);
            decipher.doFinal(input, 0, input.length, output, 0);
            return StringUtils.trim(new String(output, StandardCharsets.UTF_8));
        } catch (Exception e) {
            LoggerUtils.error(logger, e);
        }
        return null;
    }

    public static String decrypt(String text) {
        return decrypt(text, defaultSalt);
    }

    /**
     * 生成token
     *
     * @param token
     * @param salt
     * @return
     */
    public static String generateToken(Token token, String salt) {
        String tokenString = ConvertUtils.object2Json(token);
        return encrypt(tokenString, salt);
    }

    /**
     * 生成token
     *
     * @param token
     * @return
     * @throws Exception
     */
    public static String generateToken(Token token) throws Exception {
        return generateToken(token, defaultSalt);
    }

    /**
     * 生成token
     *
     * @param token
     * @param salt
     * @return
     */
    public static String generateTokenForMap(Map<String, Object> token, String salt) {
        String tokenString = ConvertUtils.map2Json(token);
        return encrypt(tokenString, salt);
    }

    /**
     * 生成token
     *
     * @param token
     * @return
     * @throws Exception
     */
    public static String generateTokenForMap(Map<String, Object> token) throws Exception {
        return generateTokenForMap(token, defaultSalt);
    }

    /**
     * 解析token
     *
     * @param token
     * @param salt
     * @return
     */
    public static Token resolveToken(String token, String salt) {
        String text = decrypt(token, salt);
        return ConvertUtils.json2Object(text, Token.class);
    }

    /**
     * 解析token
     *
     * @param token
     * @return
     * @throws Exception
     */
    public static Token resolveToken(String token) throws Exception {
        return resolveToken(token, defaultSalt);
    }

    /**
     * 解析token
     *
     * @param token
     * @param salt
     * @return
     */
    public static Map<String, Object> resolveTokenForMap(String token, String salt) {
        String text = decrypt(token, salt);
        return ConvertUtils.json2Map(text);
    }

    /**
     * 解析token
     *
     * @param token
     * @return
     * @throws Exception
     */
    public static Map<String, Object> resolveTokenForMap(String token) throws Exception {
        return resolveTokenForMap(token, defaultSalt);
    }

    /**
     * md5Hex
     *
     * @return
     */
    public static String md5Hex() {
        String str = UUIDUtils.uuidFor32();
        return md5Hex(str, defaultSalt);
    }

    /**
     * md5Hex
     *
     * @param str
     * @return
     */
    public static String md5Hex(String str) {
        return md5Hex(str, defaultSalt);
    }

    /**
     * md5Hex
     *
     * @param str
     * @param salt
     * @return
     */
    public static String md5Hex(String str, String salt) {
        return DigestUtils.md5Hex(str + "|" + salt);
    }

    /**
     * md5Hex
     *
     * @param bytes
     * @return
     */
    public static String md5Hex(byte[] bytes) {
        return DigestUtils.md5Hex(bytes);
    }

    private static byte[] getUTF8Bytes(String text) {
        return text.getBytes(StandardCharsets.UTF_8);
    }
}
