package cn.joy.framework.kits;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.apache.commons.lang3.StringUtils;

import bsh.StringUtil;
/**
 * Class操作工具类
 * @author liyy
 * @date 2014-07-05
 */
public class ClassKit {
	public static ClassLoader getClassLoader() {
		return Thread.currentThread().getContextClassLoader();
	}
	
	public static boolean isJavaClass(Class<?> clz) {
		return clz != null && clz.getClassLoader() == null;
	}
	
	public static Class getClass(String clazz){
		ClassLoader loader = getClassLoader();
		if (loader != null) {
			try {
				return Class.forName(clazz, true, loader);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}else{
			try {
				return Class.forName(clazz);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
	}

	/**
	 * 取得所有实现某接口或继承某类的类
	 * */
	public static <T> List<Class<? extends T>> listClassBySuper(String packageName, Class c) {
		return listClassBySuper(packageName, c, true, "");
	}
	
	public static <T> List<Class<? extends T>> listClassBySuper(String packageName, Class c, String pattern) {
		return listClassBySuper(packageName, c, true, pattern);
	}
	
	public static <T> List<Class<? extends T>> listClassBySuper(String packageName, Class c, boolean recursive) {
		return listClassBySuper(packageName, c, recursive, "");
	}
	
	public static <T> List<Class<? extends T>> listClassBySuper(String packageName, Class c, boolean recursive, String pattern) {
		List<Class<? extends T>> returnClassList = new ArrayList<>();

		// 获取当前包下以及子包下所以的类
		List<Class> allClass = listClass(packageName, recursive, pattern);
		if (allClass != null) {
			for (Class classes : allClass) {
				// 判断是否是同一个接口
				if (c.isAssignableFrom(classes)) {
					// 本身不加入进去
					if (!c.equals(classes)) {
						returnClassList.add(classes);
					}
				}
			}
		}

		return returnClassList;
	}
	
	public static List<Class> listClassByAnnotation(String packageName, Class c) {
		return listClassByAnnotation(packageName, c, true);
	}
	
	public static List<Class> listClassByAnnotation(String packageName, Class c, String pattern) {
		return listClassByAnnotation(packageName, c, true, pattern);
	}
	
	public static List<Class> listClassByAnnotation(String packageName, Class c, boolean recursive) {
		return listClassByAnnotation(packageName, c, recursive, "");
	}
	
	public static List<Class> listClassByAnnotation(String packageName, Class c, boolean recursive, String pattern) {
		List<Class> returnClassList = new ArrayList<Class>();
		
		List<Class> allClass = listClass(packageName, recursive, pattern);
		if (allClass != null) {
			for (Class clazz : allClass) {
				if (clazz.getAnnotation(c)!=null) {
					returnClassList.add(clazz);
				}
			}
		}
		
		return returnClassList;
	}
	
	public static Class getClassBySuper(String packageName, Class c) {
		return getClassBySuper(packageName, c, true);
	}
	
	public static Class getClassBySuper(String packageName, Class c, String pattern) {
		return getClassBySuper(packageName, c, true, pattern);
	}
	
	public static Class getClassBySuper(String packageName, Class c, boolean recursive) {
		return getClassBySuper(packageName, c, recursive, "");
	}

	public static Class getClassBySuper(String packageName, Class c, boolean recursive,  String pattern) {
		// 获取当前包下以及子包下所以的类
		List<Class> allClass = listClass(packageName, recursive, pattern);
		if (allClass != null) {
			for (Class clazz : allClass) {
				// 判断是否是同一个接口
				if (c.isAssignableFrom(clazz)) {
					// 本身不加入进去
					if (!c.equals(clazz)) {
						return clazz;
					}
				}
			}
		}
		return null;
	}
	
	/**
	 * 从包package中获取所有的Class
	 */
	public static List<Class> listClass(String packageName) {
		return listClass(packageName, true, "");
	}
	
	public static List<Class> listClass(String packageName, String pattern) {
		return listClass(packageName, true, pattern);
	}
	
	public static List<Class> listClass(String packageName, boolean recursive) {
		return listClass(packageName, recursive, "");
	}

	public static List<Class> listClass(String packageName, boolean recursive, String pattern) {
		// 第一个class类的集合
		List<Class> classes = new ArrayList<Class>();
		// 获取包的名字 并进行替换
		String packageDirName = packageName.replace('.', '/');
		try {
			// 定义一个枚举的集合 并进行循环来处理这个目录下的things
			Set<String> loadedUrl = new HashSet<String>();	//某些特定环境下，可能加载多次
			Enumeration<URL> dirs = getClassLoader().getResources(packageDirName);
			// 循环迭代下去
			while (dirs.hasMoreElements()) {
				// 获取下一个元素
				URL url = dirs.nextElement();
				String loadedPath = StringKit.trim(url.getPath().toString(), "/");
				if(loadedUrl.contains(loadedPath))
					continue;
				loadedUrl.add(loadedPath);
				
				// 得到协议的名称
				String protocol = url.getProtocol();
				// 如果是以文件的形式保存在服务器上
				if ("file".equals(protocol)) {
					// 获取包的物理路径
					String filePath = URLDecoder.decode(url.getFile(), "UTF-8"); // url.getPath().replaceAll("%20",
																					// " ");
					// 以文件的方式扫描整个包下的文件 并添加到集合中
					findAndAddClassesInPackageByFile(packageName, filePath, recursive, classes, pattern);
				} else if ("jar".equals(protocol)) {
					// 如果是jar包文件
					try {
						// 获取jar
						JarFile jar = ((JarURLConnection) url.openConnection()).getJarFile();
						Enumeration<JarEntry> entries = jar.entries();
						while (entries.hasMoreElements()) {
							// 获取jar里的一个实体 可以是目录 和一些jar包里的其他文件 如META-INF等文件
							JarEntry entry = entries.nextElement();
							String name = entry.getName();
							// 如果是以/开头的
							if (name.charAt(0) == '/') {
								// 获取后面的字符串
								name = name.substring(1);
							}
							// 如果前半部分和定义的包名相同
							if (name.startsWith(packageDirName)) {
								int idx = name.lastIndexOf('/');
								// 如果以"/"结尾 是一个包
								if (idx != -1) {
									// 获取包名 把"/"替换成"."
									packageName = name.substring(0, idx).replace('/', '.');
								}
								// 如果可以迭代下去 并且是一个包
								if ((idx != -1) || recursive) {
									// 如果是一个.class文件 而且不是目录
									if (name.endsWith(".class") && !entry.isDirectory()) {
										if(StringKit.isNotEmpty(pattern) && !name.matches(pattern))
											continue;
										// 去掉后面的".class" 获取真正的类名
										String className = name.substring(packageName.length() + 1, name.length() - 6);
										try {
											// 添加到classes
											classes.add(Class.forName(packageName + '.' + className));
										} catch (ClassNotFoundException e) {
											throw new RuntimeException(e);
										}
									}
								}
							}
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return classes;
	}

	/**
	 * 以文件的形式来获取包下的所有Class
	 */
	private static void findAndAddClassesInPackageByFile(String packageName, String packagePath,
			final boolean recursive, List<Class> classes, final String pattern) {
		// 获取此包的目录 建立一个File
		File dir = new File(packagePath);
		// 如果不存在或者 也不是目录就直接返回
		if (!dir.exists() || !dir.isDirectory()) {
			return;
		}
		// 如果存在 就获取包下的所有文件 包括目录
		File[] dirfiles = dir.listFiles(new FileFilter() {
			// 自定义过滤规则 如果可以循环(包含子目录) 或则是以.class结尾的文件(编译好的java类文件)
			public boolean accept(File file) {
				return (recursive && file.isDirectory()) || (file.isFile() && (StringKit.isEmpty(pattern) || file.getName().matches(pattern)) && file.getName().endsWith(".class"));
			}
		});
		// 循环所有文件
		for (File file : dirfiles) {
			// 如果是目录 则继续扫描
			if (file.isDirectory()) {
				findAndAddClassesInPackageByFile(packageName + "." + file.getName(), file.getAbsolutePath(), recursive,
						classes, pattern);
			} else {
				// 如果是java类文件 去掉后面的.class 只留下类名
				String className = file.getName().substring(0, file.getName().length() - 6);
				try {
					// 添加到集合中去
					classes.add(Class.forName(packageName + '.' + className, false, getClassLoader()));
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
			}
		}
	}
}