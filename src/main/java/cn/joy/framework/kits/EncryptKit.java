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
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.SecretKeySpec;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;
import cn.joy.framework.core.JoyConstants;

public class EncryptKit {
	/**
	 * 16进制数值
	 */
	private static char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };

	/**
	 * 生成MD5加密校验码
	 */
	public static byte[] md5(byte[] data){  
        MessageDigest md5 = getEncrypt("MD5");  
        md5.update(data);  
        return md5.digest();  
    }  

	/**
	 * 生成MD5加密校验码
	 */
	public static String md5(String str) {
		return encryptString(getEncrypt("MD5"), str);
	}

	/**
	 * 生成MD5加密校验码
	 */
	public static String md5(File file) {
		return encryptFile(getEncrypt("MD5"), file);
	}

	/**
	 * 生成SHA1加密校验码
	 */
	public static String sha1(String str) {
		return encryptString(getEncrypt("SHA1"), str);
	}

	/**
	 * 生成SHA1加密校验码
	 */
	public static String sha1(File file) {
		return encryptFile(getEncrypt("SHA1"), file);
	}

	/**
	 * 获得指定的算法加密器
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
	
	/*public static String bytes2HexString(byte[] b) {  
	    String ret = "";  
	    for (int i = 0; i < b.length; i++) {  
	      String hex = Integer.toHexString(b[i] & 0xFF);  
	      if (hex.length() == 1) {  
	    	  System.out.println(hex);
	        hex = '0' + hex;  
	      }  
	      ret += hex.toUpperCase();  
	    }  
	    return ret;  
	}*/
	
	/*private static byte uniteBytes(byte src0, byte src1) {  
        byte _b0 = Byte.decode("0x" + new String(new byte[]{src0})).byteValue();  
        _b0 = (byte)(_b0 << 4);  
        byte _b1 = Byte.decode("0x" + new String(new byte[]{src1})).byteValue();  
        byte ret = (byte)(_b0 ^ _b1);  
        return ret;  
	}  */

	/**
	 * 使用加密器对目标字符串进行加密
	 */
	private static String encryptString(MessageDigest digest, String str) {
		try {
			return bytesToHex(digest.digest(str.getBytes(JoyConstants.CHARSET_UTF8)));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 使用加密器对目标文件进行加密
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

	public static String md5SmallFile(File file) {
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

	/**
	 * 强随机数
	 */
	public int getSecureRandomInt(int n) {
		// SecureRandom random = new SecureRandom();
		SecureRandom random;
		try {
			random = SecureRandom.getInstance("SHA1PRNG");
		} catch (NoSuchAlgorithmException e) {
			random = new SecureRandom();
		}
		// random.setSeed(123123123123L);
		// byte bytes[] = new byte[20];
		// random.nextBytes(bytes);
		return random.nextInt(n);
	}

	/**
	 * SHA加密
	 */
	public static byte[] sha(byte[] data) throws Exception {
		MessageDigest sha = getEncrypt("SHA");
		sha.update(data);
		return sha.digest();
	}

	/**
	 * 初始化HMAC密钥
	 */
	public static String initMacKey() throws Exception {
		KeyGenerator keyGenerator = KeyGenerator.getInstance("HmacMD5");
		SecretKey secretKey = keyGenerator.generateKey();
		return encryptBASE64(secretKey.getEncoded());
	}

	/**
	 * HMAC加密
	 */
	public static byte[] hmac(byte[] data, String str) throws Exception {
		SecretKey secretKey = new SecretKeySpec(decryptBASE64(str), "HmacMD5");
		Mac mac = Mac.getInstance(secretKey.getAlgorithm());
		mac.init(secretKey);
		return mac.doFinal(data);
	}

	/**
	 * BASE64加密
	 */
	public static String encryptBASE64(byte[] str) throws Exception {
		return (new BASE64Encoder()).encodeBuffer(str);
	}

	/**
	 * BASE64解密
	 */
	public static byte[] decryptBASE64(String str) throws Exception {
		return (new BASE64Decoder()).decodeBuffer(str);
	}
	
	public static byte[] encryptDES(byte[] data, String key) throws Exception {
		return DES(data, key.getBytes(), Cipher.ENCRYPT_MODE);
	}
	
	public static byte[] decryptDES(byte[] data, String key) throws Exception {
		return DES(data, key.getBytes(), Cipher.DECRYPT_MODE);
	}
	
	private static byte[] DES(byte[] data, byte[] key, int mode){  
	    byte[] result = null ;  
	    try {  
	        SecureRandom sr = new SecureRandom();    
	        SecretKeyFactory keyFactory;  
	        DESKeySpec dks = new DESKeySpec(key);  
	        keyFactory = SecretKeyFactory.getInstance("DES");  
	        SecretKey secretkey = keyFactory.generateSecret(dks);   
	        //创建Cipher对象  
	        //Cipher cipher = Cipher.getInstance("DES/ECB/NoPadding");    
	        Cipher cipher = Cipher.getInstance("DES");    
	        //初始化Cipher对象    
	        cipher.init(mode, secretkey, sr);    
	        //加解密  
	        result = cipher.doFinal(data);   
	    } catch (Exception e) {  
	        e.printStackTrace();  
	    }   
	      
	    return result;  
	}  
	
	public static void main(String[] args) throws Exception {
		String inputStr = "简单加密";
		System.out.println("原文:\n" + inputStr);

		byte[] inputData = inputStr.getBytes();
		String code = encryptBASE64(inputData);
		System.out.println("BASE64加密后:\n" + code);

		byte[] output = decryptBASE64(code);
		String outputStr = new String(output);
		System.out.println("BASE64解密后:\n" + outputStr);

		// 验证BASE64加密解密一致性
		System.out.println(inputStr.equals(outputStr));
		
		byte[] desBytes = encryptDES(inputData, "abc12345");
		System.out.println("DES加密后:\n" + new String(desBytes));
		
		output = decryptDES(desBytes, "abc12345");
		System.out.println("DES解密后:\n" + new String(output));

		// 验证MD5对于同一内容加密是否一致
		System.out.println(new BigInteger(md5(inputData)).equals(new BigInteger(md5(inputData))));

		// 验证SHA对于同一内容加密是否一致
		System.out.println(new BigInteger(sha(inputData)).equals(new BigInteger(sha(inputData))));

		String key = initMacKey();
		System.out.println("Mac密钥:\n" + key);

		// 验证HMAC对于同一内容，同一密钥加密是否一致
		System.out.println(new BigInteger(hmac(inputData, key)).equals(new BigInteger(hmac(inputData, key))));

		BigInteger md5 = new BigInteger(md5(inputData));
		System.out.println("MD5:\n" + md5.toString(16));

		BigInteger sha = new BigInteger(sha(inputData));
		System.out.println("SHA:\n" + sha.toString(32));

		BigInteger mac = new BigInteger(hmac(inputData, inputStr));
		System.out.println("HMAC:\n" + mac.toString(16));
	}
}
