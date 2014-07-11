package cn.joy.framework.rule;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.log4j.Logger;

import cn.joy.framework.core.JoyManager;
/**
 * 调试状态的业务规则类加载器，对于指定调试的规则，每次执行都重新加载类定义并产生一个实例
 * @author liyy
 * @date 2014-05-22
 */
public class RuleDebugClassLoader extends ClassLoader {
	private static Logger logger = Logger.getLogger(RuleDebugClassLoader.class);
	
	private String debugClassPath;
	private ClassLoader parent;
	
	public RuleDebugClassLoader(ClassLoader parent) {
		this.debugClassPath = detectWebRootPath()+"/WEB-INF/classes";
		this.parent = parent;
	}
	
	private String detectWebRootPath() {
		try {
			String path = RuleDebugClassLoader.class.getResource("/").toURI().getPath();
			return new File(path).getParentFile().getParentFile().getCanonicalPath();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public Class loadRuleClass(String className) throws ClassNotFoundException{
		return this.loadClass(className);
	}
	
	@Override
	public synchronized Class loadClass(String className, boolean resolveit)
			throws ClassNotFoundException {
		Class result;

		// check the loaded class cache
		result = findLoadedClass(className);
		if (result != null) {
			return result;
		}
		// 如果是java核心文件由system查找，否则都由本classloader加载
		if (className.startsWith("java.")) {
			result = super.findSystemClass(className);
			return result;
		}
		
		if(className.startsWith(JoyManager.getServer().getModulePackage())){
			byte[] classData = getTypeFromBasePath(className);
			if (classData == null)
				throw new ClassNotFoundException();

			result = defineClass(className, classData, 0, classData.length);
			if (result == null) {
				throw new ClassFormatError();
			}else{
				if (resolveit)
					resolveClass(result);
				return result;
			}
		}
			
		if (parent != null)
			result = parent.loadClass(className);

		return result;
	}
	
	private byte[] getTypeFromBasePath(String typeName) {
		FileInputStream fis;
		String fileName = debugClassPath + File.separatorChar
				+ typeName.replace('.', File.separatorChar) + ".class";
		if(logger.isDebugEnabled())
			logger.debug("fileName:" + fileName);
		try {
			fis = new FileInputStream(fileName);
		} catch (FileNotFoundException e) {
			return null;
		}

		BufferedInputStream bis = new BufferedInputStream(fis);
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try {
			int c = bis.read();
			while (c != -1) {
				out.write(c);
				c = bis.read();
			}
		} catch (IOException e) {
			return null;
		}
		return out.toByteArray();

	}

}
