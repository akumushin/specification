package common.filter;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.persistence.criteria.JoinType;

@Target(TYPE)
@Retention(RUNTIME)
@Documented
public @interface FilterJoinColumn {
	String name() default "";
	JoinType type() default JoinType.INNER;
	/**
	 * Table column Equal(==) or Not equal(!=) this column
	 * default Equal
	 * Equal(==) or Not equal(!=) or In or Not In ONLY
	 * @return
	 */
	CompareType compare() default CompareType.Equal;
	boolean negative() default false;
}
