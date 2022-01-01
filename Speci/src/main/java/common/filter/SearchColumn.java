package common.filter;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.persistence.criteria.JoinType;

/**
 * 
 * @author vu van thuong
 *
 */
@Target({ FIELD, METHOD })
@Retention(RUNTIME)
@Documented
public @interface SearchColumn {
	String name() default "";
	/**
	 * Table column Equal(==) or Smaller(<) or  Greater(>).... this column
	 * default Equal
	 * @return
	 */
	CompareType compare() default CompareType.Equal;
	SearchArrayType arrayType() default SearchArrayType.All;
}
