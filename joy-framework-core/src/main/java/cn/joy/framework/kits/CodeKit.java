package cn.joy.framework.kits;

import java.util.UUID;

public class CodeKit {
	public static String getCode(){
		return UUID.randomUUID().toString().replace("-", "").toUpperCase();
	}
	
	public static String getCode(String prefix){
		return prefix+"-"+getCode();
	}
	
	public static String getCode(String prefix, int digit){
		return prefix+"-"+getCode().substring(Math.min(digit, 31));
	}
	
	public static String getNumberCode(int digit) {
		String result="";
		for(int i=0;i<digit;i++) {
			 result+= String.valueOf((int)(10*(Math.random())));
	    }
		return result;
	}

	public static String generateNumberCodeWithTimestamp(int digit) {
		return System.currentTimeMillis()+getNumberCode(digit);
	}
}
