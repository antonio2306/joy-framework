package cn.joy.framework.plugin.jfinal.annotation;

import static java.lang.annotation.ElementType.TYPE;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
@Target({ TYPE })
public @interface Table {
	public String name() default "";
	
	public String db() default "";
	
	public String pk() default "id";

}
