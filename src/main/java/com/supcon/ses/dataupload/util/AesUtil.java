package com.supcon.ses.dataupload.util;

import com.supcon.ses.dataupload.exceptions.AesErrorException;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * AES加密工具类
 *
 * @author liangKai
 * @version 1.0
 * @date 2020/6/1510:42
 */
public class AesUtil {

    /**
     * AES密码器
     */
    private static Cipher cipher;

    /**
     * 算法方式
     */
    private static final String KEY_ALGORITHM = "AES";

    /**
     * 算法/模式/填充
     */
    private static final String CIPHER_ALGORITHM_CBC = "AES/CBC/PKCS5Padding";

    /**
     * 私钥大小128/192/256(bits)位 即：16/24/32bytes，暂时使用128，如果扩大需要更换java/jre里面的jar包
     */
    private static final Integer PRIVATE_KEY_SIZE_BIT = 128;

    private static final Integer PRIVATE_KEY_SIZE_BYTE = 16;

    private AesUtil(){

    }

    /**
     * 加密
     *
     * @param secretKey 秘钥
     * @param plainText 明文字符串
     * @return
     */
    public static String encrypt(String secretKey, String plainText) {

        byte[] bytes = secretKey.getBytes(StandardCharsets.UTF_8);

        if (bytes.length != PRIVATE_KEY_SIZE_BYTE) {
            throw new AesErrorException("AESUtil:Invalid AES secretKey length (must be 16 bytes)");
        }

        // 密文字符串
        String cipherText = "";
        try {
            // 加密模式初始化参数
            initParam(bytes, Cipher.ENCRYPT_MODE);
            // 获取加密内容的字节数组
            byte[] bytePlainText = plainText.getBytes(StandardCharsets.UTF_8);
            // 执行加密
            byte[] byteCipherText = cipher.doFinal(bytePlainText);
            cipherText = Base64.getEncoder().encodeToString(byteCipherText);
        } catch (Exception e) {
            throw new AesErrorException("AESUtil:encrypt fail!", e);
        }
        return cipherText;
    }

    /**
     * 解密
     *
     * @param secretKey  秘钥
     * @param cipherText 密文字符串
     * @return
     */
    public static String decrypt(String secretKey, String cipherText) {

        byte[] bytes = secretKey.getBytes(StandardCharsets.UTF_8);

        if (bytes.length != PRIVATE_KEY_SIZE_BYTE) {
            throw new AesErrorException("AESUtil:Invalid AES secretKey length (must be 16 bytes)");
        }

        // 明文字符串
        String plainText = "";
        try {
            initParam(bytes, Cipher.DECRYPT_MODE);
            // 将加密并编码后的内容解码成字节数组
            byte[] byteCipherText = Base64.getDecoder().decode(cipherText);
            // 解密
            byte[] bytePlainText = cipher.doFinal(byteCipherText);
            plainText = new String(bytePlainText, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new AesErrorException("AESUtil:decrypt fail!", e);
        }
        return plainText;
    }

    /**
     * 初始化参数
     *
     * @param keyBytes 密钥：加密的规则 16位
     * @param mode     加密模式：加密or解密
     */
    public static void initParam(byte[] keyBytes, int mode) {
        try {
            // 防止Linux下生成随机key
            SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
            secureRandom.setSeed(keyBytes);
            // 获取key生成器
            KeyGenerator keygen = KeyGenerator.getInstance(KEY_ALGORITHM);
            keygen.init(PRIVATE_KEY_SIZE_BIT, secureRandom);

            // 获得原始对称密钥的字节数组
            byte[] raw = keyBytes;

            // 根据字节数组生成AES内部密钥
            SecretKeySpec key = new SecretKeySpec(raw, KEY_ALGORITHM);
            // 根据指定算法"AES/CBC/PKCS5Padding"实例化密码器
            cipher = Cipher.getInstance(CIPHER_ALGORITHM_CBC);
            IvParameterSpec iv = new IvParameterSpec(keyBytes);

            cipher.init(mode, key, iv);
        } catch (Exception e) {
            throw new AesErrorException("AESUtil:initParam fail!", e);
        }
    }

}
