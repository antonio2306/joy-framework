package cn.joy.framework.annotation;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.ElementType.FIELD;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
@Target({ TYPE, FIELD })
public @interface Info {
	public String name() default "";
	
	public String label() default "";
	
	public String type() default "";

	public String desc() default "";

	public String code() default "";
	
}
