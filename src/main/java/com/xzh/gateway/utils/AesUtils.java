package com.xzh.gateway.utils;

import org.apache.commons.codec.binary.Base64;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;

/**
 * AES加密工具
 *
 * @author: 向振华
 * @date: 2020/09/08 09:31
 */
public class AesUtils {

    /**
     * 密钥（16bytes）
     */
    public static final String SECRET_KEY = "3X6i3A5n1G4z2H37";

    /**
     * 密钥字节数
     */
    private static final Integer SECRET_KEY_SIZE_BYTE = 16;

    /**
     * 密钥大小128/192/256(bits)位 即：16/24/32bytes，暂时使用128，如果扩大需要更换java/jre里面的jar包
     */
    private static final Integer SECRET_KEY_SIZE_BIT = 128;

    /**
     * 偏移量（16bytes）
     */
    private static final String IV = SECRET_KEY;

    /**
     * 字符串编码
     */
    private static final String CHARSET = "UTF-8";

    /**
     * 算法方式
     */
    private static final String ALGORITHM = "AES";

    /**
     * 算法/模式/填充
     */
    private static final String ALGORITHM_MODE_FILLING = "AES/CBC/PKCS5Padding";

    /**
     * 加密
     *
     * @param plainText
     * @return
     */
    public static String encrypt(String plainText) {
        return encrypt(SECRET_KEY, plainText);
    }

    /**
     * 加密
     *
     * @param secretKey
     * @param plainText
     * @return
     */
    public static String encrypt(String secretKey, String plainText) {
        if (secretKey.length() != SECRET_KEY_SIZE_BYTE) {
            throw new RuntimeException("密钥必须是16字节");
        }
        // 密文字符串
        String cipherText;
        try {
            // 加密模式初始化参数
            Cipher cipher = initParam(secretKey, Cipher.ENCRYPT_MODE);
            // 获取加密内容的字节数组
            byte[] bytePlainText = plainText.getBytes(CHARSET);
            // 执行加密
            byte[] byteCipherText = cipher.doFinal(bytePlainText);
            cipherText = Base64.encodeBase64String(byteCipherText);
        } catch (Exception e) {
            throw new RuntimeException("AES加密失败", e);
        }
        return cipherText;
    }

    /**
     * 解密
     *
     * @param cipherText
     * @return
     */
    public static String decrypt(String cipherText) {
        return decrypt(SECRET_KEY, cipherText);
    }

    /**
     * 解密
     *
     * @param secretKey
     * @param cipherText
     * @return
     */
    public static String decrypt(String secretKey, String cipherText) {
        if (secretKey.length() != SECRET_KEY_SIZE_BYTE) {
            throw new RuntimeException("密钥必须是16字节");
        }
        // 明文字符串
        String plainText;
        try {
            Cipher cipher = initParam(secretKey, Cipher.DECRYPT_MODE);
            // 将加密并编码后的内容解码成字节数组
            byte[] byteCipherText = Base64.decodeBase64(cipherText);
            // 解密
            byte[] bytePlainText = cipher.doFinal(byteCipherText);
            plainText = new String(bytePlainText, CHARSET);
        } catch (Exception e) {
            throw new RuntimeException("AES解密失败", e);
        }
        return plainText;
    }

    /**
     * 初始化参数
     *
     * @param secretKey
     * @param mode
     * @return
     */
    public static Cipher initParam(String secretKey, int mode) {
        try {
            // 防止Linux下生成随机key
            SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
            secureRandom.setSeed(secretKey.getBytes());
            // 获取key生成器
            KeyGenerator keygen = KeyGenerator.getInstance(ALGORITHM);
            keygen.init(SECRET_KEY_SIZE_BIT, secureRandom);
            // 获得原始对称密钥的字节数组
            byte[] raw = secretKey.getBytes();
            // 根据字节数组生成AES内部密钥
            SecretKeySpec key = new SecretKeySpec(raw, ALGORITHM);
            // 根据指定算法"AES/CBC/PKCS5Padding"实例化密码器
            Cipher cipher = Cipher.getInstance(ALGORITHM_MODE_FILLING);
            IvParameterSpec iv = new IvParameterSpec(IV.getBytes());
            // 初始化AES密码器
            cipher.init(mode, key, iv);
            return cipher;
        } catch (Exception e) {
            throw new RuntimeException("AES初始化参数失败", e);
        }
    }
}