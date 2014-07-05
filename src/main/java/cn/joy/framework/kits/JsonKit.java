package cn.joy.framework.kits;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonKit {
	private static final ObjectMapper mapper = new ObjectMapper();

	public static String object2Json(Object object) {
		StringWriter writer = new StringWriter();
		try {
			if (object != null)
				mapper.writeValue(writer, object);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return writer.toString();
	}

	public static Object json2Object(String json, Class klass) {
		Object object = null;
		try {
			if (json != null && json.length() > 0)
				object = mapper.readValue(json, klass);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return object;
	}

	public static Map json2Map(String json) {
		Map m = null;
		try {
			if (json != null && json.length() > 0)
				m = mapper.readValue(json, Map.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return m;
	}

	public static List<Map> json2ListMap(String json) {
		List m = null;
		try {
			if (json != null && json.length() > 0)
				m = mapper.readValue(json, List.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return m;
	}
	
	public static List json2ListBean(String json, Class beanClass) {
		List m = null;
		try {
			if (json != null && json.length() > 0){
				JavaType javaType = getCollectionType(ArrayList.class, beanClass);
				m = (List) mapper.readValue(json, javaType);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return m;
	}
	
	public static Map json2MapBean(String json, Class keyClass, Class beanClass) {
		Map m = null;
		try {
			if (json != null && json.length() > 0){
				JavaType javaType = getCollectionType(HashMap.class, keyClass, beanClass);
				m = (Map) mapper.readValue(json, javaType);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return m;
	}

	public static void main(String[] args) throws Exception {
		String json = "[{\"name\":\"aaa\",\"age\":\"15\"}, {\"name\":\"bbb\",\"age\":\"25\"}]";
		List<TestBean> lst = json2ListBean(json, TestBean.class);
		System.out.println(lst);
		System.out.println(lst.get(0).getName());
		
		
		json = "{\"t1\":{\"name\":\"aaa\",\"age\":\"15\"}, \"t2\":{\"name\":\"bbb\",\"age\":\"25\"}}";
		Map<String, TestBean> mst = json2MapBean(json, String.class, TestBean.class);
		System.out.println(mst);
		System.out.println(mst.get("t1").getName());
	}

	private static JavaType getCollectionType(Class<?> collectionClass, Class<?>... elementClasses) {
		return mapper.getTypeFactory().constructParametricType(collectionClass, elementClasses);
	}
}

class TestBean {
	private String name;
	private String age;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAge() {
		return age;
	}

	public void setAge(String age) {
		this.age = age;
	}

}