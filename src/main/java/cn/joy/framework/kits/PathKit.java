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

	public static String getClassPath() {
		if (classPath == null) {
			classPath = PathKit.class.getClassLoader().getResource("").getPath();
		}
		return classPath;
	}

	public static String getPackagePath(String packageName) {
		return getClassPath() + (packageName != null ? packageName.replaceAll("\\.", "/") : "");
	}

	public static String getWebRootPath() {
		if (webRootPath == null)
			webRootPath = detectWebRootPath();;
		return webRootPath;
	}
	
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
