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

import cn.joy.framework.kits.LogKit.Log;
/**
 * 类操作工具类
 * @author liyy
 * @date 2014-07-05
 */
public class ClassKit {
	private static Log log = LogKit.getLog(ClassKit.class);
	
	/**
	 * 获取当前线程上下文的类加载器
	 * @see java.lang.Thread#getContextClassLoader()
	 * @return
	 */
	public static ClassLoader getClassLoader() {
		return Thread.currentThread().getContextClassLoader();
	}
	
	/**
	 * 加载指定全名的类
	 * @param clazz 类全名
	 * @return Class对象
	 */
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
	 * 获取指定包下面继承或实现了指定类型的类列表，递归子包
	 * @param packageName 指定包名
	 * @param clazz	指定父类型或接口类型
	 * @return
	 */
	public static <T> List<Class<? extends T>> listClassBySuper(String packageName, Class clazz) {
		return listClassBySuper(packageName, clazz, true, "", false);
	}
	
	/**
	 * 获取指定包下面继承或实现了指定类型，且类文件名匹配指定格式的类列表，递归子包
	 * @param packageName 指定包名
	 * @param clazz	指定父类型或接口类型
	 * @param pattern 指定类文件名要匹配的格式，一般为正则表达式
	 * @return
	 */
	public static <T> List<Class<? extends T>> listClassBySuper(String packageName, Class clazz, String pattern) {
		return listClassBySuper(packageName, clazz, true, pattern, false);
	}
	
	/**
	 * 获取指定包下面继承或实现了指定类型的类列表
	 * @param packageName 指定包名
	 * @param clazz	指定父类型或接口类型
	 * @param recursive 是否递归子包
	 * @return
	 */
	public static <T> List<Class<? extends T>> listClassBySuper(String packageName, Class clazz, boolean recursive) {
		return listClassBySuper(packageName, clazz, recursive, "", false);
	}
	
	/**
	 * 获取指定包下面继承或实现了指定类型的类列表
	 * @param packageName 指定包名
	 * @param clazz	指定父类型或接口类型
	 * @param recursive 是否递归子包
	 * @param pattern 指定类文件名要匹配的格式，一般为正则表达式
	 * @param ignoreNotFound 是否忽略ClassNotFound异常
	 * @return
	 */
	public static <T> List<Class<? extends T>> listClassBySuper(String packageName, Class clazz, boolean recursive, String pattern, boolean ignoreNotFound) {
		List<Class<? extends T>> returnClassList = new ArrayList<>();

		// 获取当前包下以及子包下所以的类
		List<Class> allClass = listClass(packageName, recursive, pattern, ignoreNotFound);
		if (allClass != null) {
			for (Class clz : allClass) {
				// 判断是否是同一个接口
				if (clazz.isAssignableFrom(clz)) {
					// 本身不加入进去
					if (!clazz.equals(clz)) {
						returnClassList.add(clz);
					}
				}
			}
		}

		return returnClassList;
	}
	
	/**
	 * 获取指定包下面包含指定注释类型的类列表，递归子包
	 * @param packageName 指定包名
	 * @param clazz	指定注释类型
	 * @return
	 */
	public static List<Class> listClassByAnnotation(String packageName, Class clazz) {
		return listClassByAnnotation(packageName, clazz, true);
	}
	
	/**
	 * 获取指定包下面包含指定注释类型的类列表，且类文件名匹配指定格式的类列表，递归子包
	 * @param packageName 指定包名
	 * @param clazz	指定注释类型
	 * @param pattern 指定类文件名要匹配的格式，一般为正则表达式
	 * @return
	 */
	public static List<Class> listClassByAnnotation(String packageName, Class clazz, String pattern) {
		return listClassByAnnotation(packageName, clazz, true, pattern, false);
	}
	
	/**
	 * 获取指定包下面包含指定注释类型的类列表
	 * @param packageName 指定包名
	 * @param clazz	指定注释类型
	 * @param recursive	是否递归子包
	 * @return
	 */
	public static List<Class> listClassByAnnotation(String packageName, Class clazz, boolean recursive) {
		return listClassByAnnotation(packageName, clazz, recursive, "", false);
	}
	
	/**
	 * 获取指定包下面包含指定注释类型的类列表
	 * @param packageName 指定包名
	 * @param clazz	指定注释类型
	 * @param recursive 是否递归子包
	 * @param pattern 指定类文件名要匹配的格式，一般为正则表达式
	 * @param ignoreNotFound 是否忽略ClassNotFound异常
	 * @return
	 */
	public static List<Class> listClassByAnnotation(String packageName, Class clazz, boolean recursive, String pattern, boolean ignoreNotFound) {
		List<Class> returnClassList = new ArrayList<Class>();
		
		List<Class> allClass = listClass(packageName, recursive, pattern, ignoreNotFound);
		if (allClass != null) {
			for (Class clz : allClass) {
				if (clz.getAnnotation(clazz)!=null) {
					returnClassList.add(clz);
				}
			}
		}
		
		return returnClassList;
	}
	
	/**
	 * 获取指定包下面包含指定注释类型的第一个类，递归子包
	 * @param packageName 指定包名
	 * @param clazz	指定注释类型
	 * @return
	 */
	public static Class getClassBySuper(String packageName, Class clazz) {
		return getClassBySuper(packageName, clazz, true, "", false);
	}
	
	/**
	 * 获取指定包下面继承或实现了指定类型，且类文件名匹配指定格式的第一个类，递归子包
	 * @param packageName 指定包名
	 * @param clazz	指定父类型或接口类型
	 * @param pattern 指定类文件名要匹配的格式，一般为正则表达式
	 * @return
	 */
	public static Class getClassBySuper(String packageName, Class clazz, String pattern) {
		return getClassBySuper(packageName, clazz, true, pattern, false);
	}
	
	/**
	 * 获取指定包下面继承或实现了指定类型的第一个类
	 * @param packageName 指定包名
	 * @param clazz	指定父类型或接口类型
	 * @param recursive 是否递归子包
	 * @return
	 */
	public static Class getClassBySuper(String packageName, Class clazz, boolean recursive) {
		return getClassBySuper(packageName, clazz, recursive, "", false);
	}

	/**
	 * 获取指定包下面继承或实现了指定类型的第一个类
	 * @param packageName 指定包名
	 * @param clazz	指定父类型或接口类型
	 * @param recursive 是否递归子包
	 * @param pattern 指定类文件名要匹配的格式，一般为正则表达式
	 * @param ignoreNotFound 是否忽略ClassNotFound异常
	 * @return
	 */
	public static Class getClassBySuper(String packageName, Class clazz, boolean recursive, String pattern, boolean ignoreNotFound) {
		// 获取当前包下以及子包下所以的类
		List<Class> allClass = listClass(packageName, recursive, pattern, ignoreNotFound);
		if (allClass != null) {
			for (Class clz : allClass) {
				// 判断是否是同一个接口
				if (clazz.isAssignableFrom(clz)) {
					// 本身不加入进去
					if (!clazz.equals(clz)) {
						return clz;
					}
				}
			}
		}
		return null;
	}
	
	/**
	 * 获取指定包下面的类列表，递归子包
	 * @param packageName 指定包名
	 * @return
	 */
	public static List<Class> listClass(String packageName) {
		return listClass(packageName, true, "", false);
	}
	
	/**
	 * 获取指定包下面类文件名匹配指定格式的类列表，递归子包
	 * @param packageName 指定包名
	 * @param pattern 指定类文件名要匹配的格式，一般为正则表达式
	 * @return
	 */
	public static List<Class> listClass(String packageName, String pattern) {
		return listClass(packageName, true, pattern, false);
	}
	
	/**
	 * 获取指定包下面的类列表
	 * @param packageName 指定包名
	 * @param recursive 是否递归子包
	 * @return
	 */
	public static List<Class> listClass(String packageName, boolean recursive) {
		return listClass(packageName, recursive, "", false);
	}

	/**
	 * 获取指定包下面的类列表
	 * @param packageName 指定包名
	 * @param recursive 是否递归子包
	 * @param pattern 指定类文件名要匹配的格式，一般为正则表达式
	 * @param ignoreNotFound 是否忽略ClassNotFound异常
	 * @return
	 */
	public static List<Class> listClass(String packageName, boolean recursive, String pattern, boolean ignoreNotFound) {
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
										
										//if(StringKit.isNotEmpty(pattern))
										//	log.debug("pattern="+pattern+", class="+className);
										try {
											// 添加到classes
											classes.add(Class.forName(packageName + '.' + className));
										} catch (ClassNotFoundException e) {
											if(ignoreNotFound)
												log.warn("ClassNotFound："+packageName + '.' + className);
											else
												throw new RuntimeException(e);
										} catch (Throwable t) {
											if(ignoreNotFound)
												log.warn("ClassNotFoundError："+packageName + '.' + className);
											else
												throw new RuntimeException(t);
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