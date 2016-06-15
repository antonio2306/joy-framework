package cn.joy.framework.kits;

import java.util.Random;
import java.util.UUID;

/**
 * 编码工具类
 */
public class CodeKit {
	private static Random random = new Random();
	private static String[] chars = new String[] { "a", "b", "c", "d", "e", "f", "g", "h",
            "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t",
            "u", "v", "w", "x", "y", "z", "0", "1", "2", "3", "4", "5",
            "6", "7", "8", "9", "A", "B", "C", "D", "E", "F", "G", "H",
            "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T",
            "U", "V", "W", "X", "Y", "Z"
    };
	
	/**
	 * 获取编码
	 * 格式：UUID替换所有中划线并转为大写
	 */
	public static String getCode(){
		return UUID.randomUUID().toString().replace("-", "").toUpperCase();
	}
	
	/**
	 * 获取编码，指定前缀
	 * 格式：前缀-UUID替换所有中划线并转为大写
	 * @param prefix 前缀，建议为大写字母
	 */
	public static String getCode(String prefix){
		return prefix+"-"+getCode();
	}
	
	/**
	 * 获取编码，指定前缀和截取位数
	 * 格式：前缀-UUID替换所有中划线并转为大写然后从头截取指定位数
	 * @param prefix 前缀，建议为大写字母
	 * @param digit 截取位数，不大于31
	 */
	public static String getCode(String prefix, int digit){
		return prefix+"-"+getCode().substring(Math.min(digit, 31));
	}
	
	/**
	 * 获取指定位数的随机数字编码（每一位取值0-9）
	 * @param digit 位数
	 */
	public static String getNumberCode(int digit) {
		String result="";
		for(int i=0;i<digit;i++) {
			 result+= String.valueOf((int)(10*(random.nextDouble())));
	    }
		return result;
	}

	/**
	 * 获取包含时间戳和随机数字的编码
	 * 当前时间毫秒数，拼接指定位数的随机数字（每一位取值0-9）
	 * @param digit 位数
	 */
	public static String generateNumberCodeWithTimestamp(int digit) {
		return System.currentTimeMillis()+getNumberCode(digit);
	}
	
	/**
	 * 获取指定位数的随机数字和大小写字母编码（A-Za-z0-9）
	 * @param digit 位数
	 * @return
	 */
	public static String generateCharAndNumberCode(int digit){
		String result="";
		int length = chars.length;
		for (int i = 0; i < digit; i++) {
			result+= chars[random.nextInt(length)];
		}
		return result;
	}
	
	/**
	 * 生成6位短链接编号
	 * @param url
	 * @return
	 */
	public static String generateShortUrl(String url) {
        // 可以自定义生成 MD5 加密字符传前的混合 KEY
        String key = "JOY_SHORT_URL_KEY";
        
        // 对传入网址进行 MD5 加密
        String hex = EncryptKit.md5(key + url);

        String[] resUrl = new String[4];
        for (int i = 0; i < 4; i++) {
            // 把加密字符按照 8 位一组 16 进制与 0x3FFFFFFF 进行位与运算
            String sTempSubString = hex.substring(i * 8, i * 8 + 8);
            // 这里需要使用 long 型来转换，因为 Inteper .parseInt() 只能处理 31 位 , 首位为符号位 , 如果不用long ，则会越界
            long lHexLong = 0x3FFFFFFF & Long.parseLong(sTempSubString, 16);
            String outChars = "";
            for (int j = 0; j < 6; j++) {
                // 把得到的值与 0x0000003D 进行位与运算，取得字符数组 chars 索引
                long index = 0x0000003D & lHexLong;
                // 把取得的字符相加
                outChars += chars[(int) index];
                // 每次循环按位右移 5 位
                lHexLong = lHexLong >> 5;
            }
            // 把字符串存入对应索引的输出数组
            resUrl[i] = outChars;
        }
        int j = random.nextInt(4);//产成4以内随机数
        return resUrl[j];
    }
}
