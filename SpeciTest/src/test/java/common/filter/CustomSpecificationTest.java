package common.filter;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Fetch;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Root;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

import common.EnumTest;
import common.entity.EntityJoinTest;
import common.entity.EntityTest;

@RunWith(SpringRunner.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class CustomSpecificationTest {
	@Autowired EntityManager entityManager;
	CriteriaBuilder cb;
	CriteriaQuery<EntityJoinTest> query1;
	Root<EntityJoinTest> root1;
	CriteriaQuery<EntityTest> query2;
	Root<EntityTest> root2;
	/**
	 * data initial
	 * @throws Exception
	 */
	@BeforeEach
	void setUp() throws Exception {
		for(int k=0;k<2;k++) {
			EntityJoinTest entityJoinTest = new EntityJoinTest();
			entityJoinTest.setId(k);
			entityJoinTest.setName("name"+k +"-test");
			entityManager.persist(entityJoinTest);
		
			for(int i=1;i<=5;i++) {
				EntityTest entityTest = new EntityTest();
				entityTest.setId(i+5*k);
				entityTest.setString1("string"+(i+5*k));
				entityTest.setEntityJoinTest(entityJoinTest);
				entityManager.persist(entityTest);
			}
		}
		
		cb= entityManager.getCriteriaBuilder();
		query1= cb.createQuery(EntityJoinTest.class);
		root1 = query1.from(EntityJoinTest.class);
		query2= cb.createQuery(EntityTest.class);
		root2 = query2.from(EntityTest.class);
		query2.select(root2);
		query1.select(root1);
	}
	/**
	 * delete all
	 * @throws Exception
	 */
	@AfterEach
	void tearDown() throws Exception {
		entityManager.createQuery("delete from EntityTest").executeUpdate();
		entityManager.createQuery("delete from EntityJoinTest").executeUpdate();
	}
	/**
	 * test add fetch
	 */
	@Test
	void testAddSubFetch() {
		EntityTestFilter filter = new EntityTestFilter();
		CustomSpecification<EntityTest> specification = new CustomSpecification<EntityTest>(filter);
		assertEquals(specification.getFetchFields().size(), 0);
		FetchAttribute fetchAttribute= specification.addSubFetch("joinColumn", JoinType.LEFT);
		assertEquals(specification.getFetchFields().size(), 1);
		List<FetchAttribute> list = new ArrayList<>(specification.getFetchFields());
		assertEquals(list.get(0).getAttribute(), "joinColumn");
		assertEquals(list.get(0).getJoinType(), JoinType.LEFT);
		fetchAttribute.addSubFetch("column1", JoinType.LEFT);
		assertEquals(fetchAttribute.getSubFetchAttribute().size(), 1);
		fetchAttribute.addSubFetch("column2", JoinType.LEFT);
		assertEquals(fetchAttribute.getSubFetchAttribute().size(), 2);
	}
	/**
	 * test method toPredicate
	 */
	@Test
	void testToPredicate() {
		// test predicate
		query2= cb.createQuery(EntityTest.class);
		root2= query2.from(EntityTest.class);
		EntityTestFilter filter = new EntityTestFilter();
		filter.setString1("string2");
		CustomSpecification<EntityTest> specification = new CustomSpecification<>(filter);
		query2.where(specification.toPredicate(root2, query2, cb));
		List<EntityTest> result = entityManager.createQuery(query2).getResultList();
		assertEquals(result.size(), 1);
		result.forEach(item->{
			assertTrue(item.getString1().contains("string2"));
		});
		
		// add fetch test
		query2= cb.createQuery(EntityTest.class);
		root2= query2.from(EntityTest.class);
		filter = new EntityTestFilter();
		specification = new CustomSpecification<>(filter);
		specification.addSubFetch("entityJoinTest", JoinType.LEFT);
		query2.where(specification.toPredicate(root2, query2, cb));
		
		assertEquals(root2.getFetches().size(), 1);
		List<Fetch<?, ?>> list = new ArrayList<>(root2.getFetches());
		assertEquals(list.get(0).getJoinType(), JoinType.LEFT);
		assertEquals(list.get(0).getAttribute().getName(), "entityJoinTest");
		
		//add fetch + fetch of fetch test
		query2= cb.createQuery(EntityTest.class);
		root2= query2.from(EntityTest.class);
		filter = new EntityTestFilter();
		specification = new CustomSpecification<>(filter);
		//add + add
		specification.addSubFetch("entityJoinTest", JoinType.LEFT).addSubFetch("entityJoin2Tests", JoinType.LEFT);
		query2.where(specification.toPredicate(root2, query2, cb));
		assertEquals(root2.getFetches().size(), 1);
		list = new ArrayList<>(root2.getFetches());
		assertEquals(list.get(0).getJoinType(), JoinType.LEFT);
		assertEquals(list.get(0).getAttribute().getName(), "entityJoinTest");
		List<Fetch<?, ?>> subFetchList = new ArrayList<>(list.get(0).getFetches());
		assertEquals(subFetchList.size(), 1);
		assertEquals(subFetchList.get(0).getJoinType(), JoinType.LEFT);
		assertEquals(subFetchList.get(0).getAttribute().getName(), "entityJoin2Tests");
		
	}
	/**
	 * test function toCustomPredicate
	 * value is JoinColumn / Column
	 */
	@Test
	void testToCustomPredicate() {
		query2= cb.createQuery(EntityTest.class);
		root2= query2.from(EntityTest.class);
		EntityTestFilter filter = new EntityTestFilter();
		filter.setString1("string2");
		query2.where(CustomSpecification.toCustomPredicate(root2, cb, filter));
		//filter.setEntityJoinTest(new EntityJoinTestFilter());
		//filter.getEntityJoinTest().setName("name");
		List<EntityTest> result = entityManager.createQuery(query2).getResultList();
		assertEquals(result.size(), 1);
		result.forEach(item->{
			assertTrue(item.getString1().contains("string2"));
		});
		//with new name
		query2= cb.createQuery(EntityTest.class);
		root2= query2.from(EntityTest.class);
		filter = new EntityTestFilter();
		filter.setString2("string2");
		query2.where(CustomSpecification.toCustomPredicate(root2, cb, filter));
		//filter.setEntityJoinTest(new EntityJoinTestFilter());
		//filter.getEntityJoinTest().setName("name");
		result = entityManager.createQuery(query2).getResultList();
		assertEquals(result.size(), 1);
		result.forEach(item->{
			assertTrue(item.getString1().contains("string2"));
		});
		
		query2= cb.createQuery(EntityTest.class);
		root2= query2.from(EntityTest.class);
		filter = new EntityTestFilter();
		filter.setString1("string");
		query2.where(CustomSpecification.toCustomPredicate(root2, cb, filter));
		//filter.setEntityJoinTest(new EntityJoinTestFilter());
		//filter.getEntityJoinTest().setName("name");
		result = entityManager.createQuery(query2).getResultList();
		assertEquals(result.size(), 10);
		result.forEach(item->{
			assertTrue(item.getString1().contains("string"));
		});
		
		// join column
		query2= cb.createQuery(EntityTest.class);
		root2= query2.from(EntityTest.class);
		filter = new EntityTestFilter();
		filter.setString1("string");
		filter.setEntityJoinTest(new EntityJoinTestFilter());
		filter.getEntityJoinTest().setName("name0");
		query2.where(CustomSpecification.toCustomPredicate(root2, cb, filter));
		result = entityManager.createQuery(query2).getResultList();
		assertEquals(result.size(), 5);
		result.forEach(item->{
			assertTrue(item.getString1().contains("string"));
			assertTrue(item.getEntityJoinTest().getName().contains("name0"));
		});
		//with new name
		query2= cb.createQuery(EntityTest.class);
		root2= query2.from(EntityTest.class);
		filter = new EntityTestFilter();
		filter.setString1("string");
		filter.setEntityJoinTest2(new EntityJoinTestFilter());
		filter.getEntityJoinTest2().setName("name0");
		query2.where(CustomSpecification.toCustomPredicate(root2, cb, filter));
		result = entityManager.createQuery(query2).getResultList();
		assertEquals(result.size(), 5);
		result.forEach(item->{
			assertTrue(item.getString1().contains("string"));
			assertTrue(item.getEntityJoinTest().getName().contains("name0"));
		});
		
		
		// nofilter column//
		query2= cb.createQuery(EntityTest.class);
		root2= query2.from(EntityTest.class);
		filter = new EntityTestFilter();
		filter.setNoFilter(100);
		query2.where(CustomSpecification.toCustomPredicate(root2, cb, filter));
		//filter.setEntityJoinTest(new EntityJoinTestFilter());
		//filter.getEntityJoinTest().setName("name");
		result = entityManager.createQuery(query2).getResultList();
		assertEquals(result.size(), 10);// all
	
	}
	/**
	 * test function conditionWithColumn
	 * with compare: equal, not equal, greater than, less than, equal or greater than, equal or less than, like, has contain
	 * with value is array , list , or not 
	 */
	@Test
	void testConditionWithColumn() {
		List<EntityJoinTest> result;
		String fieldName="id";
		//equal
		query1.where(CustomSpecification.conditionWithColumn(root1, cb, fieldName, 0 ,CompareType.Equal, SearchArrayType.All));
		result = entityManager.createQuery(query1).getResultList();
		assertEquals(result.size(), 1);
		result.forEach(item->{
			assertEquals(item.getId(), 0);
		});
		// not equal
		query1.where(CustomSpecification.conditionWithColumn(root1, cb, fieldName, 0 ,CompareType.NotEqual, SearchArrayType.All));
		result = entityManager.createQuery(query1).getResultList();
		assertEquals(result.size(), 1);
		result.forEach(item->{
			assertNotEquals(item.getId(), 0);
		});
		
		// greater than
		query1.where(CustomSpecification.conditionWithColumn(root1, cb, fieldName, 0 ,CompareType.GreaterThan, SearchArrayType.All));
		result = entityManager.createQuery(query1).getResultList();
		assertEquals(result.size(), 1);
		result.forEach(item->{
			assertTrue(item.getId()>0);
		});
		
		query1.where(CustomSpecification.conditionWithColumn(root1, cb, fieldName, 1 ,CompareType.GreaterThan, SearchArrayType.All));
		result = entityManager.createQuery(query1).getResultList();
		assertEquals(result.size(), 0);
		
		// less than
		query1.where(CustomSpecification.conditionWithColumn(root1, cb, fieldName, 0 ,CompareType.LessThan, SearchArrayType.All));
		result = entityManager.createQuery(query1).getResultList();
		assertEquals(result.size(), 0);
		
		query1.where(CustomSpecification.conditionWithColumn(root1, cb, fieldName, 1 ,CompareType.LessThan, SearchArrayType.All));
		result = entityManager.createQuery(query1).getResultList();
		assertEquals(result.size(), 1);
		result.forEach(item->{
			assertTrue(item.getId()<1);
		});
		// greater than or equal
		query1.where(CustomSpecification.conditionWithColumn(root1, cb, fieldName, 0 ,CompareType.EqualGreaterThan, SearchArrayType.All));
		result = entityManager.createQuery(query1).getResultList();
		assertEquals(result.size(), 2);
		result.forEach(item->{
			assertTrue(item.getId()>=0);
		});
		
		query1.where(CustomSpecification.conditionWithColumn(root1, cb, fieldName, 1 ,CompareType.EqualGreaterThan, SearchArrayType.All));
		result = entityManager.createQuery(query1).getResultList();
		assertEquals(result.size(), 1);
		result.forEach(item->{
			assertTrue(item.getId()>=1);
		});
		// less than or equal
		query1.where(CustomSpecification.conditionWithColumn(root1, cb, fieldName, 0 ,CompareType.EqualLessThan, SearchArrayType.All));
		result = entityManager.createQuery(query1).getResultList();
		assertEquals(result.size(), 1);
		result.forEach(item->{
			assertTrue(item.getId()<=0);
		});
		
		query1.where(CustomSpecification.conditionWithColumn(root1, cb, fieldName, 1 ,CompareType.EqualLessThan, SearchArrayType.All));
		result = entityManager.createQuery(query1).getResultList();
		assertEquals(result.size(), 2);
		result.forEach(item->{
			assertTrue(item.getId()<=1);
		});
		
		//like
		fieldName="name";
		query1.where(CustomSpecification.conditionWithColumn(root1, cb, fieldName, "name%" ,CompareType.Like, SearchArrayType.All));
		result = entityManager.createQuery(query1).getResultList();
		assertEquals(result.size(), 2);
		result.forEach(item->{
			assertTrue(item.getName().contains("name"));
		});
		fieldName="name";
		query1.where(CustomSpecification.conditionWithColumn(root1, cb, fieldName, "name0%" ,CompareType.Like, SearchArrayType.All));
		result = entityManager.createQuery(query1).getResultList();
		assertEquals(result.size(), 1);
		result.forEach(item->{
			assertTrue(item.getName().contains("name0"));
		});
		//has contain
		fieldName="name";
		query1.where(CustomSpecification.conditionWithColumn(root1, cb, fieldName, "name" ,CompareType.HasContains, SearchArrayType.All));
		result = entityManager.createQuery(query1).getResultList();
		assertEquals(result.size(), 2);
		result.forEach(item->{
			assertTrue(item.getName().contains("name"));
		});
		fieldName="name";
		query1.where(CustomSpecification.conditionWithColumn(root1, cb, fieldName, "name0" ,CompareType.HasContains, SearchArrayType.All));
		result = entityManager.createQuery(query1).getResultList();
		assertEquals(result.size(), 1);
		result.forEach(item->{
			assertTrue(item.getName().contains("name0"));
		});
		//array in
		//equal
		fieldName ="id";
		query1.where(CustomSpecification.conditionWithColumn(root1, cb, fieldName, new int[] {1,2,3} ,CompareType.Equal, SearchArrayType.In));
		result = entityManager.createQuery(query1).getResultList();
		assertEquals(result.size(), 1);
		result.forEach(item->{
			assertTrue(item.getId()==1 ||item.getId()==2 ||item.getId()==3);
		});
		//array not in
		query1.where(CustomSpecification.conditionWithColumn(root1, cb, fieldName, new int[] {1,2,3} ,CompareType.Equal, SearchArrayType.NotIn));
		result = entityManager.createQuery(query1).getResultList();
		assertEquals(result.size(), 1);
		result.forEach(item->{
			assertTrue(item.getId()!=1 && item.getId()!=2 &&item.getId()!=3);
		});
		
		//list in
		//equal
		fieldName ="id";
		query1.where(CustomSpecification.conditionWithColumn(root1, cb, fieldName, java.util.Arrays.asList(1,2,3) ,CompareType.Equal, SearchArrayType.In));
		result = entityManager.createQuery(query1).getResultList();
		assertEquals(result.size(), 1);
		result.forEach(item->{
			assertTrue(item.getId()==1 ||item.getId()==2 ||item.getId()==3);
		});
		//list in greater than
		fieldName ="id";
		query1.where(CustomSpecification.conditionWithColumn(root1, cb, fieldName, java.util.Arrays.asList(0,1) ,CompareType.GreaterThan, SearchArrayType.In));
		result = entityManager.createQuery(query1).getResultList();
		assertEquals(result.size(), 1);
		result.forEach(item->{
			assertTrue(item.getId()>0 ||item.getId()>1);
		});
		
		//list not in
		query1.where(CustomSpecification.conditionWithColumn(root1, cb, fieldName, java.util.Arrays.asList(1,2,3) ,CompareType.Equal, SearchArrayType.NotIn));
		result = entityManager.createQuery(query1).getResultList();
		assertEquals(result.size(), 1);
		result.forEach(item->{
			assertTrue(item.getId()!=1 && item.getId()!=2 &&item.getId()!=3);
		});
		//list not in greater than
		fieldName ="id";
		query1.where(CustomSpecification.conditionWithColumn(root1, cb, fieldName, java.util.Arrays.asList(0,1) ,CompareType.GreaterThan, SearchArrayType.NotIn));
		result = entityManager.createQuery(query1).getResultList();
		assertEquals(result.size(), 1);
		result.forEach(item->{
			assertTrue(!(item.getId()>0 ||item.getId()>1));
		});		
		//list any
		//greater than 0,1,2
		fieldName ="id";
		query1.where(CustomSpecification.conditionWithColumn(root1, cb, fieldName, java.util.Arrays.asList(0,1,2) ,CompareType.GreaterThan, SearchArrayType.Any));
		result = entityManager.createQuery(query1).getResultList();
		assertEquals(result.size(), 1);
		result.forEach(item->{
			assertTrue(item.getId()>0 ||item.getId()>1 ||item.getId()>2);
		});
		//list not any
		//greater than 0,1,2
		query1.where(CustomSpecification.conditionWithColumn(root1, cb, fieldName, java.util.Arrays.asList(0,1,2) ,CompareType.GreaterThan, SearchArrayType.NotAny));
		result = entityManager.createQuery(query1).getResultList();
		assertEquals(result.size(), 1);
		result.forEach(item->{
			assertTrue(!(item.getId()>0 ||item.getId()>1 ||item.getId()>2));
		});
		//list all
		//contain
		fieldName="name";
		query1.where(CustomSpecification.conditionWithColumn(root1, cb, fieldName, java.util.Arrays.asList("name","0") ,CompareType.HasContains, SearchArrayType.All));
		result = entityManager.createQuery(query1).getResultList();
		assertEquals(result.size(), 1);
		result.forEach(item->{
			assertTrue(item.getName().contains("name") && item.getName().contains("0"));
		});
		
		//list all
		//contain
		fieldName="name";
		query1.where(CustomSpecification.conditionWithColumn(root1, cb, fieldName, java.util.Arrays.asList("name","0") ,CompareType.HasContains, SearchArrayType.NotAll));
		result = entityManager.createQuery(query1).getResultList();
		assertEquals(result.size(), 1);
		result.forEach(item->{
			assertTrue(!(item.getName().contains("name") && item.getName().contains("0")));
		});
	}
	/**
	 * Test function conditionWithJoinColumn
	 * with compare: equal, not equal, exception when other
	 * with value is array , list , or not 
	 */
	@Test
	void testConditionWithJoinColumn() {
		List<EntityTest> result;
		String fieldName="entityJoinTest";
		EntityJoinTestFilter entityJoinTest = new EntityJoinTestFilter();
		// equal
		entityJoinTest.setId(0);
		query2.where(CustomSpecification.conditionWithJoinColumn(root2, cb, fieldName, entityJoinTest ,CompareType.Equal, SearchArrayType.All, JoinType.LEFT));
		result = entityManager.createQuery(query2).getResultList();
		assertEquals(result.size(), 5);
		result.forEach(item->{
			assertTrue(item.getEntityJoinTest().getId()==0);
		});
		// not equal
		query2=cb.createQuery(EntityTest.class);
		root2=query2.from(EntityTest.class);
		entityJoinTest.setId(0);
		query2.where(CustomSpecification.conditionWithJoinColumn(root2, cb, fieldName, entityJoinTest ,CompareType.NotEqual, SearchArrayType.All, JoinType.LEFT));
		result = entityManager.createQuery(query2).getResultList();
		assertEquals(result.size(), 5);
		result.forEach(item->{
			assertTrue(item.getEntityJoinTest().getId()!=0);
		});
		
		// greater than
		query2=cb.createQuery(EntityTest.class);
		root2=query2.from(EntityTest.class);
		entityJoinTest.setId(0);
		try {
			query2.where(CustomSpecification.conditionWithJoinColumn(root2, cb, fieldName, entityJoinTest ,
					CompareType.GreaterThan, SearchArrayType.All, JoinType.LEFT));
			fail();
		}catch(SearchException e) {
			assertTrue(e.getMessage().contains("error filter join column"));
		}
		//array
		EntityJoinTestFilter[] entityJoinTestFilters = new EntityJoinTestFilter[2];
		entityJoinTestFilters[0] = new EntityJoinTestFilter();
		entityJoinTestFilters[0].setName("name");
		entityJoinTestFilters[1] = new EntityJoinTestFilter();
		entityJoinTestFilters[1].setName("0");
		query2=cb.createQuery(EntityTest.class);
		root2=query2.from(EntityTest.class);
		entityJoinTest.setId(0);
		query2.where(CustomSpecification.conditionWithJoinColumn(root2, cb, fieldName, entityJoinTestFilters ,CompareType.Equal, SearchArrayType.All, JoinType.LEFT));
		result = entityManager.createQuery(query2).getResultList();
		assertEquals(result.size(), 5);
		result.forEach(item->{
			assertTrue(item.getEntityJoinTest().getName().contains("name"));
			assertTrue(item.getEntityJoinTest().getName().contains("0"));
		});
		entityJoinTestFilters = new EntityJoinTestFilter[2];
		entityJoinTestFilters[0] = new EntityJoinTestFilter();
		entityJoinTestFilters[0].setName("0");
		entityJoinTestFilters[1] = new EntityJoinTestFilter();
		entityJoinTestFilters[1].setName("1");
		query2=cb.createQuery(EntityTest.class);
		root2=query2.from(EntityTest.class);
		entityJoinTest.setId(0);
		query2.where(CustomSpecification.conditionWithJoinColumn(root2, cb, fieldName, entityJoinTestFilters ,CompareType.Equal, SearchArrayType.Any, JoinType.LEFT));
		result = entityManager.createQuery(query2).getResultList();
		assertEquals(result.size(), 10);
		result.forEach(item->{
			assertTrue(item.getEntityJoinTest().getName().contains("1")||item.getEntityJoinTest().getName().contains("0"));
		});
		//list
		//all
		List<EntityJoinTestFilter> entityJoinTestFiltersList = java.util.Arrays.asList(new EntityJoinTestFilter(), new EntityJoinTestFilter());
		entityJoinTestFiltersList.get(0).setName("name");
		entityJoinTestFiltersList.get(1).setName("0");
		query2=cb.createQuery(EntityTest.class);
		root2=query2.from(EntityTest.class);
		entityJoinTest.setId(0);
		query2.where(CustomSpecification.conditionWithJoinColumn(root2, cb, fieldName, entityJoinTestFiltersList ,CompareType.Equal, SearchArrayType.All, JoinType.LEFT));
		result = entityManager.createQuery(query2).getResultList();
		assertEquals(result.size(), 5);
		result.forEach(item->{
			assertTrue(item.getEntityJoinTest().getName().contains("name"));
			assertTrue(item.getEntityJoinTest().getName().contains("0"));
		});
		//any
		entityJoinTestFiltersList = java.util.Arrays.asList(new EntityJoinTestFilter(), new EntityJoinTestFilter());
		entityJoinTestFiltersList.get(0).setName("1");
		entityJoinTestFiltersList.get(1).setName("0");
		query2=cb.createQuery(EntityTest.class);
		root2=query2.from(EntityTest.class);
		entityJoinTest.setId(0);
		query2.where(CustomSpecification.conditionWithJoinColumn(root2, cb, fieldName, entityJoinTestFiltersList ,CompareType.Equal, SearchArrayType.Any, JoinType.LEFT));
		result = entityManager.createQuery(query2).getResultList();
		assertEquals(result.size(), 10);
		result.forEach(item->{
			assertTrue(item.getEntityJoinTest().getName().contains("1")||item.getEntityJoinTest().getName().contains("0"));
		});
	}
	/**
	 * test function joinList
	 * with all, not all, any, not any, in, not in
	 */
	@Test
	void testJoinList() {
		List<Expression<Boolean>> list = new ArrayList<>();
		list.add(cb.greaterThanOrEqualTo(root2.get("id"), 5));
		list.add(cb.lessThanOrEqualTo(root2.get("id"), 7));
		List<EntityTest> result;
		// all
		query2.where(CustomSpecification.joinList(cb, list, SearchArrayType.All));
		result = entityManager.createQuery(query2).getResultList();
		assertEquals(result.size(), 3);
		assertArrayEquals(
				result.stream().map((item)->item.getId()).sorted().toArray(Integer[]::new), 
				new Integer[] {5,6,7});
		
		// not all
		query2.where(CustomSpecification.joinList(cb, list, SearchArrayType.NotAll));
		result = entityManager.createQuery(query2).getResultList();
		assertEquals(result.size(), 7);
		assertArrayEquals(
				result.stream().map((item)->item.getId()).sorted().toArray(Integer[]::new), 
				new Integer[] {1,2,3,4,8,9,10});
		
		list.clear();
		list.add(cb.greaterThanOrEqualTo(root2.get("id"), 7));
		list.add(cb.lessThanOrEqualTo(root2.get("id"), 5));
		// any
		query2.where(CustomSpecification.joinList(cb, list, SearchArrayType.Any));
		result = entityManager.createQuery(query2).getResultList();
		assertEquals(result.size(), 9);
		assertArrayEquals(
				result.stream().map((item)->item.getId()).sorted().toArray(Integer[]::new), 
				new Integer[] {1,2,3,4,5,7,8,9,10});
		//not any
		query2.where(CustomSpecification.joinList(cb, list, SearchArrayType.NotAny));
		result = entityManager.createQuery(query2).getResultList();
		assertEquals(result.size(), 1);
		assertArrayEquals(
				result.stream().map((item)->item.getId()).sorted().toArray(Integer[]::new), 
				new Integer[] {6});
		
		
		list.clear();
		list.add(cb.equal(root2.get("id"), 7));
		list.add(cb.equal(root2.get("id"), 5));
		//in
		query2.where(CustomSpecification.joinList(cb, list, SearchArrayType.In));
		result = entityManager.createQuery(query2).getResultList();
		assertEquals(result.size(), 2);
		assertArrayEquals(
				result.stream().map((item)->item.getId()).sorted().toArray(Integer[]::new), 
				new Integer[] {5,7});
		//not in
		query2.where(CustomSpecification.joinList(cb, list, SearchArrayType.NotIn));
		result = entityManager.createQuery(query2).getResultList();
		assertEquals(result.size(), 8);
		assertArrayEquals(
				result.stream().map((item)->item.getId()).sorted().toArray(Integer[]::new), 
				new Integer[] {1,2,3,4,6,8,9,10});
	} 
	
	@Test
	void testGetFilter() {
		EntityTestFilter filter = new EntityTestFilter();
		CustomSpecification<EntityTest> specification = new CustomSpecification<>(filter);
		assertSame(filter, specification.getFilter());
	}

}
