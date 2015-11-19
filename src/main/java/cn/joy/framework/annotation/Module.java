package cn.joy.framework.annotation;

import static java.lang.annotation.ElementType.TYPE;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
@Target({ TYPE })
public @interface Module {
	public String name() default "";

	public String desc() default "";

	public String init() default "initModule";

	public String[] depends() default {};
}
