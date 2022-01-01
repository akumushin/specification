package common.filter;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
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
/**
 * 
 * @author Vu van thuong
 *
 * @param <T>
 */
public class CustomSpecification<T> implements Specification<T>{
	private static final long serialVersionUID = 1L;
	private Object filter;
	private final Set<FetchAttribute> fetchFields;
	public Set<FetchAttribute> getFetchFields() {
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
		this.fetchFields= new HashSet<>();
	}
	/**
	 * 
	 */
	@Override
	public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
		for(FetchAttribute name: fetchFields) {
			addFetch(root, name);
		}
		query.distinct(true);
		return CustomSpecification.toCustomPredicate(root, cb, filter);
	}
	/**
	 * 
	 * @param root
	 * @param fetchAttr
	 * @return
	 */
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
			SearchColumn searchColumn = field.getAnnotation(SearchColumn.class);
			SearchJoinColumn searchJoinColumn = field.getAnnotation(SearchJoinColumn.class);
			// filterColumn==null same the field is not filter column
			if(searchColumn != null) {//----------------is column-------------------
				//update field name, if filterColumn.name() not blank
				if(searchColumn.name().length()>0)
					fieldName= searchColumn.name();
				predicate.getExpressions().add(
						conditionWithColumn(root, cb, fieldName, fieldValue, 
								searchColumn.compare(),
								searchColumn.arrayType())
						);
			}else if (searchJoinColumn!=null) { //------------is join column------------
				//update field name, if filterJoinColumn.name() not blank
				if(searchJoinColumn.name().length()>0)
					fieldName= searchJoinColumn.name();
				predicate.getExpressions().add(
						conditionWithJoinColumn(root, cb, fieldName, fieldValue, 
								searchJoinColumn.compare(), 
								searchJoinColumn.arrayType(),
								searchJoinColumn.joinType())
						);
			}
		}
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

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static Expression<Boolean> conditionWithColumn(From<?,?> root, CriteriaBuilder cb, String fieldName, Object fieldValue, CompareType compare, SearchArrayType arrayType){
		List<Object> items = new ArrayList<>();
		if(fieldValue.getClass().isArray()) {
			for(int i =0; i<Array.getLength(fieldValue);i++)
				items.add(Array.get(fieldValue,i));
		}else if(fieldValue instanceof Collection<?>) {
			((Collection<?>)fieldValue)
				.forEach((item)-> items.add(item));
		}else
			items.add(fieldValue);
		// if in
		if( arrayType== SearchArrayType.In 
			&& compare==CompareType.Equal ) {
			return root.get(fieldName).in(items);
		}
		// if not in
		if(	arrayType== SearchArrayType.NotIn
			&& compare==CompareType.Equal ) {
			return root.get(fieldName).in(items).not();
		}
		// get function return Expression<Boolean> >
		final Function<Object, Expression<Boolean>> function;
		switch(compare) {
		case EqualGreaterThan:
			function = (item) -> cb.greaterThanOrEqualTo(root.get(fieldName), (Comparable)item);
			break;
		case EqualLessThan:
			function = (item) -> cb.lessThanOrEqualTo(root.get(fieldName), (Comparable)item);
			break;
		case GreaterThan:
			function = (item) -> cb.greaterThan(root.get(fieldName), (Comparable)item);
			break;
		case LessThan:
			function = (item) -> cb.lessThan(root.get(fieldName), (Comparable)item);
			break;
		case NotEqual:
			function = (item) -> cb.notEqual(root.get(fieldName), item);
			break;
		case Like:
			function = (item) -> cb.like(root.get(fieldName), item.toString());
			break;
		case HasContains:
			function = (item) -> cb.like(root.get(fieldName), '%'+item.toString()+'%');
			break;
		default:// equal
			function = (item) -> cb.equal(root.get(fieldName), item);
			break;
		}
		List<Expression<Boolean> > expressions = new ArrayList();
		for(Object item: items)
			expressions.add(function.apply(item));
		//items.stream().map(function).collect(Collectors.toList());
		return joinList(cb, expressions, arrayType);
		
		
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static Expression<Boolean> conditionWithJoinColumn(From<?,?> root, CriteriaBuilder cb, String fieldName, Object fieldValue, CompareType compare, SearchArrayType arrayType, JoinType joinType){
		List<Object> items = new ArrayList<>();
		if(fieldValue.getClass().isArray()) {
			for(int i =0; i<Array.getLength(fieldValue);i++)
				items.add(Array.get(fieldValue,i));
		}else if(fieldValue instanceof Collection<?>) {
			((Collection<?>)fieldValue)
				.forEach((item)-> items.add(item));
		}else
			items.add(fieldValue);
		// join
		Join<?,?> join = root.join(fieldName, joinType);
		//System.out.println("~~~~~~~~~~~~~~~~~~"+root);
		// get function return  Expression<Boolean>> 
		final Function<Object, Expression<Boolean>> function;
		switch(compare) {
		case Equal:
			function = (item) -> toCustomPredicate(join, cb, item);
			break;
		case NotEqual:
			function = (item) -> toCustomPredicate(join, cb, item).not();
			break;
		default:
			throw new SearchException("error filter join column :" + fieldName);
		}
		List<Expression<Boolean> > expressions = new ArrayList();
		for(Object item: items)
			expressions.add(function.apply(item));
		//items.stream().map(function).collect(Collectors.toList());
		return joinList(cb, expressions, arrayType);
	}
	
	public static Predicate joinList(CriteriaBuilder cb, List<Expression<Boolean> > expressions, SearchArrayType type ){
		Predicate predicate;
		switch(type) {
		case NotIn:
		case NotAny:
			predicate= cb.disjunction();
			predicate.getExpressions().addAll(expressions);
			return predicate.not();
		case All:
			predicate= cb.conjunction();
			predicate.getExpressions().addAll(expressions);
			return predicate;
		case NotAll:
			predicate= cb.conjunction();
			predicate.getExpressions().addAll(expressions);
			return predicate.not();
		
		default:// In or Any
			predicate= cb.disjunction();
			predicate.getExpressions().addAll(expressions);
			return predicate;
		}
	}
}
