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
 * Bean操作工具类
 * @author liyy
 * @date 2014-05-20
 */
public class BeanKit {
	public static Object cloneBean(Object bean) {
		try {
			return BeanUtils.cloneBean(bean);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
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
	
	public static void setSimpleProperty(Object bean, String name, Object value) {
		try {
			BeanUtils.setProperty(bean, name, value);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
	}

	public static Class getClass(String clazz) throws ClassNotFoundException {
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		if (loader != null) {
			try {
				return Class.forName(clazz, true, loader);
			} catch (ClassNotFoundException E) {
			}
		}
		return Class.forName(clazz);
	}
	
	public static <T> T getNewInstance(Class<T> clazz){
		try {
			if(clazz!=null)
				return clazz.newInstance();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static Object getNewInstance(String clazz)
			throws ClassNotFoundException, IllegalAccessException,
			InstantiationException {
		return getClass(clazz).newInstance();
	}

	public static InputStream getResourceAsStream(Class claz, String name) {
		InputStream result = null;

		while (name.startsWith("/")) {
			name = name.substring(1);
		}

		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

		if (classLoader == null) {
			classLoader = claz.getClassLoader();
			result = classLoader.getResourceAsStream(name);
		} else {
			result = classLoader.getResourceAsStream(name);

			if (result == null) {
				classLoader = claz.getClassLoader();
				if (classLoader != null)
					result = classLoader.getResourceAsStream(name);
			}
		}
		return result;
	}

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
