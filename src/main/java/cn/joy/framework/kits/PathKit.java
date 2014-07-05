package cn.joy.framework.kits;

public class PathKit {
	private static String classPath;
	
	public static String getClassPath() {
		if (classPath == null) {
			classPath = PathKit.class.getClassLoader().getResource("").getPath();
		}
		return classPath;
	}
	
}


