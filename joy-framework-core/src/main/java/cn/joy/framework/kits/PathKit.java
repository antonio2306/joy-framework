package cn.joy.framework.kits;

import java.io.File;

/**
 * 路径操作工具类
 * @author liyy
 * @date 2014-07-06
 */
public class PathKit {
	private static String classPath;
	private static String webRootPath;

	/**
	 * 获取运行时的类路径
	 */
	public static String getClassPath() {
		if (classPath == null) {
			classPath = PathKit.class.getClassLoader().getResource("").getPath();
		}
		return classPath;
	}

	/**
	 * 将给定包名转换为基于类路径的文件目录路径
	 * 
	 * 比如：com.xxx.yyy转换为D:/.../classes/com/xxx/yyy
	 */
	public static String getPackagePath(String packageName) {
		return getClassPath() + (packageName != null ? packageName.replaceAll("\\.", "/") : "");
	}

	/**
	 * 获取web应用的根目录
	 */
	public static String getWebRootPath() {
		if (webRootPath == null)
			webRootPath = detectWebRootPath();;
		return webRootPath;
	}
	
	/**
	 * 设置web应用的根目录，用于自动检测不到时
	 */
	public static void setWebRootPath(String webRootPath) {
		if (webRootPath == null)
			return ;
		
		if (webRootPath.endsWith(File.separator))
			webRootPath = webRootPath.substring(0, webRootPath.length() - 1);
		PathKit.webRootPath = webRootPath;
	}
	
	private static String detectWebRootPath() {
		try {
			String path = PathKit.class.getResource("/").toURI().getPath();
			return new File(path).getParentFile().getParentFile().getCanonicalPath();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
