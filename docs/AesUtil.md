上报平台提供的`AesUtil.java`文件

```java
package com.craftsoft.util;

import com.craftsoft.exception.RRException;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.*;

/**
 * AES加密工具类
 * @author liangKai
 * @date 2020/6/1510:42
 * @version 1.0
 */
public class AesUtil {

	/**
	 * AES密码器
	 */
	private static Cipher cipher;

	/**
	 * 字符串编码
	 */
	private static final String KEY_CHARSET = "UTF-8";

	/**
	 * 算法方式
	 */
	private static final String KEY_ALGORITHM = "AES";

	/**
	 * 算法/模式/填充
	 */
	private static final String CIPHER_ALGORITHM_CBC = "AES/CBC/PKCS5Padding";

	/**
	 *  私钥大小128/192/256(bits)位 即：16/24/32bytes，暂时使用128，如果扩大需要更换java/jre里面的jar包
	 */
	private static final Integer PRIVATE_KEY_SIZE_BIT = 128;

	private static final Integer PRIVATE_KEY_SIZE_BYTE = 16;

	/**
	 *  加密
	 * @param secretKey 秘钥
	 * @param plainText 明文字符串
	 * @return
	 */
	public static String encrypt(String secretKey, String plainText) {

		byte[] bytes = secretKey.getBytes(StandardCharsets.UTF_8);

		if (bytes.length != PRIVATE_KEY_SIZE_BYTE) {
			throw new RRException("AESUtil:Invalid AES secretKey length (must be 16 bytes)");
		}

		// 密文字符串
		String cipherText = "";
		try {
			// 加密模式初始化参数
			initParam(bytes, Cipher.ENCRYPT_MODE);
			// 获取加密内容的字节数组
			byte[] bytePlainText = plainText.getBytes(KEY_CHARSET);
			// 执行加密
			byte[] byteCipherText = cipher.doFinal(bytePlainText);
			cipherText = Base64.getEncoder().encodeToString(byteCipherText);
		} catch (Exception e) {
			throw new RRException("AESUtil:encrypt fail!", e);
		}
		return cipherText;
	}

	/**
	 *  解密
	 * @param secretKey 秘钥
	 * @param cipherText 密文字符串
	 * @return
	 */
	public static String decrypt(String secretKey, String cipherText) {

		byte[] bytes = secretKey.getBytes(StandardCharsets.UTF_8);

		if (bytes.length != PRIVATE_KEY_SIZE_BYTE) {
			throw new RRException("AESUtil:Invalid AES secretKey length (must be 16 bytes)");
		}

		// 明文字符串
		String plainText = "";
		try {
			initParam(bytes, Cipher.DECRYPT_MODE);
			// 将加密并编码后的内容解码成字节数组
			byte[] byteCipherText = Base64.getDecoder().decode(cipherText);
			// 解密
			byte[] bytePlainText = cipher.doFinal(byteCipherText);
			plainText = new String(bytePlainText, KEY_CHARSET);
		} catch (Exception e) {
			throw new RRException("AESUtil:decrypt fail!", e);
		}
		return plainText;
	}

	/**
	 * 初始化参数
	 * @param keyBytes 密钥：加密的规则 16位
	 * @param mode 加密模式：加密or解密
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
			throw new RRException("AESUtil:initParam fail!", e);
		}
	}

	/**
	 * 秘钥
	 */
	public static final String SECRET_KEY = "f271379419e349ba";

	public static void main(String[] args) {

		Map<String, Object> map = new HashMap<>();

		map.put("companyCode", "RD3213000018");
		map.put("serviceId", "PERSONNEL_REAL");
		map.put("dataId", SafeUtil.getUuid());

		Map<String, Object> data = new HashMap<>();
		data.put("collectTime", "20230901162326");

		List<Map<String, Object>> list = new ArrayList<>();
		Map<String, Object> valueData = new HashMap<>();
		valueData.put("personName", "张恒");
		valueData.put("personId", "15150762331");
		valueData.put("stationId", "操作工");
		valueData.put("isOutside", 0);
		valueData.put("longitude", "118.3650215");
		valueData.put("latitude", "34.1065155");
		list.add(valueData);

		data.put("datas", list);

		map.put("data", AesUtil.encrypt(SECRET_KEY, SafeUtil.GSON.toJson(data)));

		System.out.println(SafeUtil.GSON.toJson(map));
	}
}
```