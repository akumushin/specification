package common.filter;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.jpa.domain.Specification;

public class CustomSpecification<T> implements Specification<T>{
	private static final long serialVersionUID = 1L;
	private Object filter;
	public CustomSpecification(Object filter) {
		this.setFilter(filter);
	}
	@Override
	public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
		return CustomSpecification.toCustomPredicate(root, cb, filter);
	}
	public static Predicate toCustomPredicate(From<?,?> root, CriteriaBuilder cb, Object filter) {
		Predicate predicate = cb.conjunction();
		// field
		for(Field field: filter.getClass().getDeclaredFields()) {
			Object fieldValue=null;
			String fieldName=null;
			try {
				field.setAccessible(true);
				fieldValue = field.get(filter);
				if(fieldValue==null)
					throw new IllegalArgumentException("null");
				
			} catch (IllegalArgumentException | IllegalAccessException e) {
				continue;
			}
			//******************************************************
			// if filterColumn
			FilterColumn filterColumn = field.getAnnotation(FilterColumn.class);
			if(filterColumn != null) {
				fieldName= filterColumn.name().length()==0 ? field.getName() : filterColumn.name();
				predicate.getExpressions().add(conditionWith(root, cb, fieldName, fieldValue, filterColumn.compare()));
			}
			//******************************************************
			// if filterJoinColumn
			FilterJoinColumn filterJoinColumn = field.getAnnotation(FilterJoinColumn.class);
			if(filterJoinColumn!= null) {
				fieldName= filterJoinColumn.name().length()==0 ? field.getName() : filterJoinColumn.name();
				Join<?,?> join = root.join(fieldName, filterJoinColumn.type());
				predicate.getExpressions().add(conditionWithJoinColumn(join, cb, fieldName, fieldValue, filterJoinColumn.compare()));
				
			}
			
			
		}
		return predicate;
		
	}
	public static Expression<Boolean> conditionWithJoinColumn(From<?,?> join, CriteriaBuilder cb, String fieldName, Object fieldValue, CompareType type){
		if(fieldValue.getClass().isArray()|| fieldValue instanceof Collection<?>) {
			List<Object> items = new ArrayList<>();
			if(fieldValue.getClass().isArray()) {
				for(int i=0;i<Array.getLength(fieldValue);i++)
					items.add(Array.get(items, i));
			}else {
				for(Object item : (Collection<?> ) fieldValue)
					items.add(item);
			}
			Predicate predicate = cb.disjunction();
			items
				.stream()
				.map(	(item)	->	toCustomPredicate(join, cb, item)	)
				.forEach(	p	->	predicate.getExpressions().add(p)	);
			switch(type) {
			case In:
				return predicate;
			case NotIn:
				return cb.not(predicate);
			default:
				throw new FilterException(fieldName + " error with FilterJoinColumn.compare()");
			}
		}	else switch(type){
		case Equal:
			return toCustomPredicate(join, cb, fieldValue);
		case NotEqual:
			return cb.not(toCustomPredicate(join, cb, fieldValue));
		default:
			throw new FilterException(fieldName + " error with FilterJoinColumn.compare()");
		}
	}
	
	public static Expression<Boolean> conditionWith(From<?,?> root, CriteriaBuilder cb, String fieldName, Object fieldValue, CompareType type){
		if(fieldValue instanceof Boolean)
			return conditionWithComparable(root, cb, fieldName, (Boolean)fieldValue, type);
		if(fieldValue instanceof String)
			return conditionWithString(root, cb, fieldName, (String)fieldValue, type);
		if(fieldValue instanceof Integer)
			return conditionWithComparable(root, cb, fieldName, (Integer)fieldValue, type);
		if(fieldValue instanceof Double)
			return conditionWithComparable(root, cb, fieldName, (Double)fieldValue, type);
		if(fieldValue instanceof LocalDateTime)
			return conditionWithComparable(root, cb, fieldName, (LocalDateTime)fieldValue, type);
		if(fieldValue instanceof LocalDate)
			return conditionWithComparable(root, cb, fieldName, (LocalDate)fieldValue, type);
		if(fieldValue instanceof Timestamp)
			return conditionWithComparable(root, cb, fieldName, (Timestamp)fieldValue, type);
		if(fieldValue instanceof java.sql.Date)
			return conditionWithComparable(root, cb, fieldName, (java.sql.Date)fieldValue, type);
		if(fieldValue instanceof java.util.Date)
			return conditionWithComparable(root, cb, fieldName, (java.util.Date)fieldValue, type);
		
		if(fieldValue.getClass().isArray())
			return conditionWithArray(root, cb, fieldName, fieldValue, type);
		if(fieldValue instanceof Collection) {
			return conditionWithCollection(root, cb, fieldName, (Collection<?> ) fieldValue, type);
		}
		throw new FilterException(fieldName + " filter column error");
	}
	
	/**
	 * 
	 * @param root
	 * @param cb
	 * @param fieldName
	 * @param value
	 * @param type
	 * @return
	 */
	public static Expression<Boolean> conditionWithArray(From<?,?> root, CriteriaBuilder cb, String fieldName, Object value, CompareType type) {
		Object[] objs = new Object[Array.getLength(value)];
		for(int i=0;i<objs.length;i++) {
			objs[i] = Array.get(value, i);
		}
		switch(type) {
		case In:
			return root.get(fieldName).in(objs);
		case NotIn:
			return cb.not(root.get(fieldName).in(objs));
		default:
			throw new FilterException(fieldName + " filter column error");
		}
	}
	
	/**
	 * 
	 * @param root
	 * @param cb
	 * @param fieldName
	 * @param value
	 * @param type
	 * @return
	 */
	public static Expression<Boolean> conditionWithCollection(From<?,?> root, CriteriaBuilder cb, String fieldName, Collection<?> value, CompareType type) {
		switch(type) {
		case In:
			return root.get(fieldName).in(value);
		case NotIn:
			return cb.not(root.get(fieldName).in(value));
		default:
			throw new FilterException(fieldName + " filter column error");
		}
	}
	/**
	 * value has type Y extend
	 * @param <T>
	 * @param root
	 * @param cb
	 * @param fieldName
	 * @param value
	 * @param type
	 * @return
	 */
	public static <T extends Comparable<? super T>>Expression<Boolean> conditionWithComparable(From<?,?> root, CriteriaBuilder cb, String fieldName, T value, CompareType type) {
		Expression<Boolean> expression;
		switch(type) {
		case Equal:
			return cb.equal(root.get(fieldName), value);
		case NotEqual:
			return cb.notEqual(root.get(fieldName), value);
		case EqualGreaterThan:
			return cb.greaterThanOrEqualTo(root.get(fieldName), value);
		case EqualLessThan:
			return cb.lessThanOrEqualTo(root.get(fieldName), value);
		case GreaterThan:
			return cb.greaterThan(root.get(fieldName), value);
		case LessThan:
			return cb.lessThan(root.get(fieldName), value);
		
		default:
			throw new FilterException(fieldName + " filter column error");
		}
	}
	
	/**
	 * value type string
	 * @param root
	 * @param cb
	 * @param fieldName
	 * @param value
	 * @param type
	 * @return
	 */
	public static Expression<Boolean> conditionWithString(From<?,?> root, CriteriaBuilder cb, String fieldName, String value, CompareType type) {
		switch(type) {
		case Equal:
		case EqualGreaterThan:
		case EqualLessThan:
		case GreaterThan:
		case LessThan:
		case NotEqual:
			return conditionWithComparable(root, cb, fieldName, value, type);
		case Like:
			return cb.like(root.get(fieldName), value);
		case HasContains:
			return cb.like(root.get(fieldName), '%'+value+'%');
		default:
			throw new FilterException(fieldName + " filter column error");
		}
	}
	
	public Object getFilter() {
		return filter;
	}
	public void setFilter(Object filter) {
		this.filter = filter;
	}
}
