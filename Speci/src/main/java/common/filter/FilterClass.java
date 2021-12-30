package common.filter;

import static java.lang.annotation.ElementType.TYPE;

import java.lang.annotation.Target;
/**
 * 
 * @author vu van thuong
 *
 */
@Target(TYPE)
public @interface FilterClass {
	Class<? extends Object> clazz();
}
