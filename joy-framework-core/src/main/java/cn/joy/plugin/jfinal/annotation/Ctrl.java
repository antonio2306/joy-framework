package cn.joy.plugin.jfinal.annotation;

import static java.lang.annotation.ElementType.TYPE;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
@Target({ TYPE })
public @interface Ctrl {
	public String url() default "";
	
	public String viewPath() default "";
}
