package cn.joy.framework.kits;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import cn.joy.framework.core.JoyConstants;

public class EncryptKit {
	/**
	 * 16进制数值
	 */
	private static char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };

	/**
	 * 日志
	 */

	/**
	 * 生成MD5加密校验码
	 * 
	 * @param string
	 *            待加密字符串
	 * @return MD5加密校验码
	 * @since 1.0
	 */
	public static String md5(String string) {
		return encryptString(getEncrypt("MD5"), string);
	}

	/**
	 * 生成MD5加密校验码
	 * 
	 * @param file
	 *            待加密文件
	 * @return MD5加密校验码
	 * @since 1.0
	 */
	public static String md5(File file) {
		return encryptFile(getEncrypt("MD5"), file);
	}

	/**
	 * 生成SHA1加密校验码
	 * 
	 * @param string
	 *            待加密字符串
	 * @return SHA1加密校验码
	 * @since 1.0
	 */
	public static String sha1(String string) {
		return encryptString(getEncrypt("SHA1"), string);
	}

	/**
	 * 生成SHA1加密校验码
	 * 
	 * @param file
	 *            待加密文件
	 * @return SHA1加密校验码
	 * @since 1.0
	 */
	public static String sha1(File file) {
		return encryptFile(getEncrypt("SHA1"), file);
	}

	/**
	 * 获得指定的算法加密器
	 * 
	 * @param algorithm
	 *            算法
	 * @throws CatGroupException
	 *             如果没有参数algorithm指定的加密算法则抛出此异常
	 * @return 加密器
	 * @since 1.0
	 */
	private static MessageDigest getEncrypt(String algorithm) {
		try {
			return MessageDigest.getInstance(algorithm);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 计算结果转为16进制表示
	 * 
	 * @param bytes
	 *            待转换Byte数组
	 * @return 转换结果
	 * @since 1.0
	 */
	private static String bytesToHex(byte[] bytes) {
		int length = bytes.length;
		StringBuilder sb = new StringBuilder(2 * length);
		for (int i = 0; i < length; i++) {
			sb.append(hexDigits[(bytes[i] & 0xf0) >> 4]);
			sb.append(hexDigits[bytes[i] & 0xf]);
		}
		return sb.toString();
	}

	/**
	 * 使用加密器对目标字符串进行加密
	 * 
	 * @param digest
	 *            加密器
	 * @param string
	 *            目标字符串
	 * @return 计算结果
	 * @since 1.0
	 */
	private static String encryptString(MessageDigest digest, String string) {
		try {
			return bytesToHex(digest.digest(string.getBytes(JoyConstants.CHARSET_UTF8)));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 使用加密器对目标文件进行加密
	 * 
	 * @param digest
	 *            加密器
	 * @param file
	 *            目标文件
	 * @throws XXXException
	 *             当文件未找到或读取错误时抛出此异常
	 * @return 计算结果
	 * @since 1.0
	 */
	private static String encryptFile(MessageDigest digest, File file) {
		if (digest == null)
			return null;
		InputStream fis = null;
		try {
			fis = new FileInputStream(file);
			byte[] buffer = new byte[8192];
			int numRead = 0;
			while ((numRead = fis.read(buffer)) > 0) {
				digest.update(buffer, 0, numRead);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				fis.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		return bytesToHex(digest.digest());
	}

	public static String md5SmallFile(File file){
		String value = null;
		FileInputStream in = null;
		try {
			in = new FileInputStream(file);
			MappedByteBuffer byteBuffer = in.getChannel().map(FileChannel.MapMode.READ_ONLY, 0, file.length());
			MessageDigest md5 = getEncrypt("MD5");
			md5.update(byteBuffer);
			BigInteger bi = new BigInteger(1, md5.digest());
			value = bi.toString(16);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return value;
	}
	
}
