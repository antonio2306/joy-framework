package cn.joy.framework.kits;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;

import cn.joy.framework.kits.LogKit.Log;
/**
 * Json操作工具类
 * @author liyy
 * @date 2014-05-20
 */
public class JsonKit {
	private static Log logger = LogKit.get();
	private static final ObjectMapper mapper = new ObjectMapper();
	
	static{
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	}

	public static void main(String[] args) throws Exception {
		addSerializer(TestBean.class, new JsonSerializer<TestBean>(){
			@Override
			public void serialize(TestBean value, JsonGenerator jgen, SerializerProvider provider)
					throws IOException, JsonProcessingException {
				//jgen.writeStartObject();  
		        //jgen.writeString("age:"+value.getAge());  
				jgen.writeRawValue(value.toJson());
		        //jgen.writeEndObject(); 
				//jgen.writeRaw(value.toJson());
			}
			
			/*@Override
			public Class<TestBean> handledType() {
				return TestBean.class;
			}*/
		});
		
		TestBean tb = new TestBean();
		tb.setName("abc");
		tb.setAge(20);
		System.out.println(object2Json(tb));
		
		List<TestBean> lst = new ArrayList();
		lst.add(tb);
		
		tb = new TestBean();
		tb.setName("abc");
		tb.setAge(30);
		lst.add(tb);
		
		logger.debug(object2Json(lst));
		
		/*String json = "[{\"name\":\"aaa\",\"age\":\"15\"}, {\"name\":\"bbb\",\"age\":\"25\"}]";
		List<TestBean> lst = json2ListBean(json, TestBean.class);
		System.out.println(lst);
		System.out.println(lst.get(0).getName());
		
		json = "{\"t1\":{\"name\":\"aaa\",\"age\":\"15\"}, \"t2\":{\"name\":\"bbb\",\"age\":\"25\"}}";
		Map<String, TestBean> mst = json2MapBean(json, String.class, TestBean.class);
		System.out.println(mst);
		System.out.println(mst.get("t1").getName());*/
	}
	
	/**
	 * 添加自定义序列化实现
	 */
	public static void addSerializer(JsonSerializer<?> ser){
		SimpleModule module = new SimpleModule();
		module.addSerializer(ser);
		mapper.registerModule(module);
	}
	
	/**
	 * 为指定类型添加自定义序列化实现
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void addSerializer(Class type, JsonSerializer<?> ser){
		SimpleModule module = new SimpleModule();
		module.addSerializer(type, ser);
		mapper.registerModule(module);
	}
	
	/**
	 * 批量添加自定义序列化实现
	 */
	public static void addSerializers(JsonSerializer<?>... sers){
		if(sers==null && sers.length>0){
			SimpleModule module = new SimpleModule();
			for(JsonSerializer<?> ser:sers){
				module.addSerializer(ser);
			}
			mapper.registerModule(module);
		}
	}

	/**
	 * 对象转换为json字符串
	 */
	public static String object2Json(Object object) {
		StringWriter writer = new StringWriter();
		try {
			if (object != null)
				mapper.writeValue(writer, object);
		} catch (Exception e) {
			logger.error("", e);
		}
		return writer.toString();
	}
	
	/**
	 * 对象转换为json字节数组
	 */
	public static byte[] object2JsonBytes(Object object) {
		try {
			if (object != null)
				return mapper.writeValueAsBytes(object);
		} catch (Exception e) {
			logger.error("", e);
		}
		return null;
	}
	
	/**
	 * 对象转换为格式化的json字符串
	 */
	public static String object2FormatJson(Object object) {
		StringWriter writer = new StringWriter();
		try {
			if (object != null)
				mapper.writerWithDefaultPrettyPrinter().writeValue(writer, object);
		} catch (Exception e) {
			logger.error("", e);
		}
		return writer.toString();
	}
	
	/**
	 * json字符串转换为指定类型的对象
	 */
	public static Object json2Object(String json, Class klass) {
		Object object = null;
		try {
			if (json != null && json.length() > 0)
				object = mapper.readValue(json, klass);
		} catch (Exception e) {
			logger.error("", e);
		}
		return object;
	}
	
	/**
	 * json字节数组转换为指定类型的对象
	 */
	public static Object jsonBytes2Object(byte[] jsonBytes, Class klass) {
		Object object = null;
		try {
			if (jsonBytes != null && jsonBytes.length > 0)
				object = mapper.readValue(jsonBytes, klass);
		} catch (Exception e) {
			logger.error("", e);
		}
		return object;
	}

	/**
	 * json字符串转换为Map对象
	 */
	public static Map json2Map(String json) {
		Map m = null;
		try {
			if (json != null && json.length() > 0)
				m = mapper.readValue(json, Map.class);
		} catch (Exception e) {
			logger.error("", e);
		}
		return m;
	}
	
	/**
	 * json字节数组转换为Map对象
	 */
	public static Map jsonBytes2Map(byte[] jsonBytes) {
		Map m = null;
		try {
			if (jsonBytes != null && jsonBytes.length > 0)
				m = mapper.readValue(jsonBytes, Map.class);
		} catch (Exception e) {
			logger.error("", e);
		}
		return m;
	}

	/**
	 * json字符串转换为元素为Map的List对象
	 */
	public static List<Map> json2ListMap(String json) {
		List m = null;
		try {
			if (json != null && json.length() > 0)
				m = mapper.readValue(json, List.class);
		} catch (Exception e) {
			logger.error("", e);
		}
		return m;
	}
	
	/**
	 * json字节数组转换为元素为Map的List对象
	 */
	public static List<Map> jsonBytes2ListMap(byte[] jsonBytes) {
		List m = null;
		try {
			if (jsonBytes != null && jsonBytes.length > 0)
				m = mapper.readValue(jsonBytes, List.class);
		} catch (Exception e) {
			logger.error("", e);
		}
		return m;
	}
	
	/**
	 * json字符串转换为元素为指定类型的List对象
	 */
	public static List json2ListBean(String json, Class beanClass) {
		List m = null;
		try {
			if (json != null && json.length() > 0){
				JavaType javaType = getCollectionType(ArrayList.class, beanClass);
				m = (List) mapper.readValue(json, javaType);
			}
		} catch (Exception e) {
			logger.error("", e);
		}
		return m;
	}
	
	/**
	 * json字节数组转换为元素为指定类型的List对象
	 */
	public static List jsonBytes2ListBean(byte[] jsonBytes, Class beanClass) {
		List m = null;
		try {
			if (jsonBytes != null && jsonBytes.length > 0){
				JavaType javaType = getCollectionType(ArrayList.class, beanClass);
				m = (List) mapper.readValue(jsonBytes, javaType);
			}
		} catch (Exception e) {
			logger.error("", e);
		}
		return m;
	}
	
	/**
	 * json字符串转换为指定key和value类型的Map
	 */
	public static Map json2MapBean(String json, Class keyClass, Class beanClass) {
		Map m = null;
		try {
			if (json != null && json.length() > 0){
				JavaType javaType = getCollectionType(HashMap.class, keyClass, beanClass);
				m = (Map) mapper.readValue(json, javaType);
			}
		} catch (Exception e) {
			logger.error("", e);
		}
		return m;
	}
	
	/**
	 * json字节数组转换为指定key和value类型的Map
	 */
	public static Map jsonBytes2MapBean(byte[] jsonBytes, Class keyClass, Class beanClass) {
		Map m = null;
		try {
			if (jsonBytes != null && jsonBytes.length > 0){
				JavaType javaType = getCollectionType(HashMap.class, keyClass, beanClass);
				m = (Map) mapper.readValue(jsonBytes, javaType);
			}
		} catch (Exception e) {
			logger.error("", e);
		}
		return m;
	}

	private static JavaType getCollectionType(Class<?> collectionClass, Class<?>... elementClasses) {
		return mapper.getTypeFactory().constructParametricType(collectionClass, elementClasses);
	}
}

class TestBean {
	private String name;
	private int age;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public String toJson(){
		return "{age:"+age+"}";
	}
}