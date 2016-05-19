package cn.joy.framework.kits;

import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.Date;
import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.converters.DateConverter;
/**
 * 类实例操作
 * @author liyy
 * @date 2014-05-20
 */
public class BeanKit {
	/**
	 * 基于getter和setter的浅拷贝
	 * @see org.apache.commons.beanutils.BeanUtils#cloneBean(Object)
	 */
	public static Object cloneBean(Object bean) {
		try {
			return BeanUtils.cloneBean(bean);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 获取对象属性值，转化为字符串返回
	 * @see org.apache.commons.beanutils.BeanUtils#getSimpleProperty(Object, String)
	 */
	public static String getSimpleProperty(Object bean, String name) {
		try {
			return BeanUtils.getSimpleProperty(bean, name);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 设置对象属性值
	 * @see org.apache.commons.beanutils.BeanUtils#setProperty(Object, String, Object)
	 */
	public static void setProperty(Object bean, String name, Object value) {
		try {
			BeanUtils.setProperty(bean, name, value);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 创建指定类型的类实例
	 * @param clazz 用于指定类型的Class对象
	 * @return
	 */
	public static <T> T getNewInstance(Class<T> clazz){
		try {
			if(clazz!=null)
				return clazz.newInstance();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return null;
	}

	/**
	 * 创建指定类型全名的类实例
	 * @param clazz 用于指定类型全名的Class对象
	 * @return
	 */
	public static Object getNewInstance(String clazz) {
		return getNewInstance(ClassKit.getClass(clazz));
	}

	/**
	 * 读取相对于classpath的指定路径资源
	 * @param clazz 用于获取备用类加载器
	 * @param path 资源相对于classpath的路径
	 * @return 资源输入流
	 */
	public static InputStream getResourceAsStream(Class clazz, String path) {
		InputStream result = null;

		while (path.startsWith("/")) {
			path = path.substring(1);
		}

		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

		if (classLoader == null) {
			classLoader = clazz.getClassLoader();
			result = classLoader.getResourceAsStream(path);
		} else {
			result = classLoader.getResourceAsStream(path);

			if (result == null) {
				classLoader = clazz.getClassLoader();
				if (classLoader != null)
					result = classLoader.getResourceAsStream(path);
			}
		}
		return result;
	}

	/**
	 * 从request参数构造指定类型的类实例，按参数名赋值到对应名称的属性
	 * 日期类型解析格式支持："yyyyMMdd", "yyyyMMddHHmmss", "yyyy-MM-dd", "yyyy-MM-dd HH:mm:ss"
	 * @param request
	 * @param bean
	 * @return
	 */
	public static <T> T injectBeanFromRequest(HttpServletRequest request, Class<T> bean) {  
        T t = null;  
        try {  
            t = bean.newInstance();  
            Enumeration parameterNames = request.getParameterNames();  
            DateConverter convert = new DateConverter();//写一个日期转换器  
            String[] patterns = { "yyyyMMdd", "yyyyMMddHHmmss", "yyyy-MM-dd", "yyyy-MM-dd HH:mm:ss" };//限定日期的格式字符串数组  
            convert.setPatterns(patterns);  
            ConvertUtils.register(convert, Date.class);  
            while (parameterNames.hasMoreElements()) {  
                String name = (String) parameterNames.nextElement();  
                String value = request.getParameter(name);  
  
                BeanUtils.setProperty(t, name, value);//使用BeanUtils来设置对象属性的值  
  
            }  
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
        return t;  
  
    }  
}
