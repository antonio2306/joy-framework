package cn.joy.framework.kits;
/**
 * 路径操作工具类
 * @author liyy
 * @date 2014-07-06
 */
public class PathKit {
	private static String classPath;

	public static String getClassPath() {
		if (classPath == null) {
			classPath = PathKit.class.getClassLoader().getResource("").getPath();
		}
		return classPath;
	}

	public static String getPackagePath(String packageName) {
		return getClassPath() + (packageName != null ? packageName.replaceAll("\\.", "/") : "");
	}

}
