package common.filter;

import static java.lang.annotation.ElementType.TYPE;

import java.lang.annotation.Target;

@Target(TYPE)
public @interface FilterJoinColumn {
	String name() default "";
	String entity();
	/**
	 * Table column Equal(==) or Not equal(!=) this column
	 * default Equal
	 * Equal(==) or Not equal(!=) ONLY
	 * @return
	 */
	CompareType compare() default CompareType.Equal;
}
