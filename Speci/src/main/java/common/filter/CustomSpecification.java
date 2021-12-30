package common.filter;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.FetchParent;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.jpa.domain.Specification;

public class CustomSpecification<T> implements Specification<T>{
	private static final long serialVersionUID = 1L;
	private Object filter;
	private final List<FetchAttribute> fetchFields;
	public List<FetchAttribute> getFetchFields() {
		return fetchFields;
	}
	public Object getFilter() {
		return filter;
	}
	public void setFilter(Object filter) {
		this.filter = filter;
	}
	/**
	 * return sub fetch
	 * @param attributeName
	 * @param joinType
	 * @return
	 */
	public FetchAttribute addSubFetch(String attributeName, JoinType joinType) {
		FetchAttribute attribute= new FetchAttribute(attributeName, joinType);
		fetchFields.add(attribute);
		return attribute;
	}
	public CustomSpecification(Object filter) {
		this.setFilter(filter);
		this.fetchFields= new ArrayList<>();
	}
	@Override
	public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
		for(FetchAttribute name: fetchFields) {
			addFetch(root, name);
		}
		query.distinct(true);
		return CustomSpecification.toCustomPredicate(root, cb, filter);
	}
	private void addFetch(FetchParent<?,?> root, FetchAttribute fetchAttr) {
		FetchParent<?,?> fetch = root.fetch(fetchAttr.getAttribute(), fetchAttr.getJoinType());
		for(FetchAttribute sub: fetchAttr.getSubFetchAttribute()) {
			addFetch(fetch, sub);
		}
	}
	
	/**
	 * get predicate from filter
	 * @param root
	 * @param cb
	 * @param filter
	 * @return
	 */
	public static Predicate toCustomPredicate(From<?,?> root, CriteriaBuilder cb, Object filter) {
		Predicate predicate = cb.conjunction();// and predicate
		// for each in all fields
		for(Field field: filter.getClass().getDeclaredFields()) {
			//*****************************************************
			// get name and value field
			field.setAccessible(true);
			Object fieldValue=field.getName();
			String fieldName=field.getName();
			try {
				
				fieldValue = field.get(filter);
				if(fieldValue==null)
					throw new IllegalArgumentException("null");
				
			} catch (IllegalArgumentException | IllegalAccessException e) {
				continue;
			}
			// get filter annotation
			FilterArray filterArray = field.getAnnotation(FilterArray.class);
			FilterColumn filterColumn = field.getAnnotation(FilterColumn.class);
			// filterColumn==null same the field is not filter column
			if(filterColumn == null)
				continue;
			//update field name, if filterColumn.name() not blank
			if(filterColumn.name().length()>0)
				fieldName= filterColumn.name();
			
			// =======================Not is array or list================================
			if(filterArray ==null) {
				// default column
				if(!filterColumn.isJoinColumn()) {
					predicate.getExpressions().add(conditionWith(root, cb, fieldName, fieldValue, filterColumn.compare()));
				}else {	// join column
					Join<?,?> join = root.join(fieldName, filterColumn.joinType());
					switch(filterColumn.compare()){
					case Equal:
						predicate.getExpressions().add( toCustomPredicate(join, cb, fieldValue));
						break;
					case NotEqual:
						predicate.getExpressions().add(  cb.not(toCustomPredicate(join, cb, fieldValue)));
						break;
					default:
						throw new FilterException(fieldName + " error with FilterJoinColumn.compare()");
					}
				}
				continue;
			}
			// ======================== Array or list (down to end method)======================================
			// get items of filter field return items
			List<Object> items = new ArrayList<>();
			if(fieldValue.getClass().isArray()) {
				for(int i =0; i<Array.getLength(fieldValue);i++)
					items.add(Array.get(fieldValue,i));
			}else if(fieldValue instanceof Collection<?>) {
				((Collection<?>)fieldValue)
					.forEach((item)-> items.add(item));
			}else {
				throw new FilterException(fieldName +" is not array or collection");
			}
			// sub predicate can replace to IN or NOT IN condition
			if(		!filterColumn.isJoinColumn() 
					&& filterArray.arrayType()== FilterArrayType.Any 
					&& filterColumn.compare()==CompareType.Equal ) {
				predicate.getExpressions().add(root.get(fieldName).in(items));
				continue;
			}
			if(		!filterColumn.isJoinColumn() 
					&& filterArray.arrayType()== FilterArrayType.NotAny
					&& filterColumn.compare()==CompareType.Equal ) {
				predicate.getExpressions().add(cb.not(root.get(fieldName).in(items)));
				continue;
			}
			// sub predicate can't replace to IN or NOT IN condition
			List<Expression<Boolean> > expressions = new ArrayList<>();
			if(!filterColumn.isJoinColumn()) {
				//this field is not join column
				for(Object item: items)
					expressions.add(conditionWith(root, cb, fieldName, item, filterColumn.compare()));
			}else {
				//this field is join column
				Join<?,?> join = root.join(fieldName, filterColumn.joinType());
				for(Object item: items) {
					switch(filterColumn.compare()){
					case Equal:
						expressions.add( toCustomPredicate(join, cb, item));
						break;
					case NotEqual:
						expressions.add( cb.not(toCustomPredicate(join, cb, item)));
						break;
					default:
						throw new FilterException(fieldName + " error with FilterJoinColumn.compare()");
					}
				}
					
			}
			Predicate subPredicate;
			switch(filterArray.arrayType()) {
			case Any:
				subPredicate = cb.disjunction();
				subPredicate.getExpressions().addAll(expressions);
				predicate.getExpressions().add(subPredicate);
				break;
			case NotAny:
				subPredicate = cb.disjunction();
				subPredicate.getExpressions().addAll(expressions);
				predicate.getExpressions().add(cb.not(subPredicate));
				break;
			case All:
				subPredicate = cb.conjunction();
				subPredicate.getExpressions().addAll(expressions);
				predicate.getExpressions().add(subPredicate);
				break;
			case NotAll:
				subPredicate = cb.conjunction();
				subPredicate.getExpressions().addAll(expressions);
				predicate.getExpressions().add(cb.not(subPredicate));
				break;
			}
		}// end for field
		return predicate;
		
	}
	/**
	 * build to expression<Boolean> of field which is not join column
	 * @param root
	 * @param cb
	 * @param fieldName
	 * @param fieldValue
	 * @param type
	 * @return
	 */
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
		
		return conditionWithDefault(root, cb, fieldName, fieldValue, type);
	}
	/**
	 * build to expression<Boolean> of field is Integer, Double, Date, DateTime
	 * @param <T>
	 * @param root
	 * @param cb
	 * @param fieldName
	 * @param value
	 * @param type
	 * @return
	 */
	public static <T extends Comparable<? super T>>Expression<Boolean> conditionWithComparable(From<?,?> root, CriteriaBuilder cb, String fieldName, T value, CompareType type) {
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
	 * build to expression<Boolean> of field is Enum or User defined class
	 * @param root
	 * @param cb
	 * @param fieldName
	 * @param value
	 * @param type
	 * @return
	 */
	public static Expression<Boolean> conditionWithDefault(From<?,?> root, CriteriaBuilder cb, String fieldName, Object value, CompareType type) {
		switch(type) {
		case Equal:
			return cb.equal(root.get(fieldName), value);
		case NotEqual:
			return cb.notEqual(root.get(fieldName), value);
		default:
			throw new FilterException(fieldName + " filter column error");
		}
	}
	/**
	 * build to expression<Boolean> of field is String
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
}
