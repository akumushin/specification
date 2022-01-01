package common.filter;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
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
class CustomSpecificationTest2 {
	@Autowired EntityManager entityManager;
	CriteriaBuilder cb;
	CriteriaQuery<EntityJoinTest> query1;
	Root<EntityJoinTest> root1;
	CriteriaQuery<EntityTest> query2;
	Root<EntityTest> root2;
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
				entityTest.setBoolean1((i+5*k)%2==0);
				entityTest.setDateLocal(LocalDate.of(2021, i+5*k, 1));
				entityTest.setDateSql(Date.valueOf(entityTest.getDateLocal()));
				entityTest.setDateUtil(Date.valueOf(entityTest.getDateLocal()));
				entityTest.setDouble1((double)(i+5*k));
				if((i+5*k)%2==0)
					entityTest.setEnumTest(EnumTest.test1);
				else
					entityTest.setEnumTest(EnumTest.test2);
				entityTest.setString1("string"+(i+5*k));
				entityTest.setTimeLocal(LocalDateTime.of(2021, i+5*k, 1, 0, 0));
				entityTest.setTimestamp(Timestamp.valueOf(entityTest.getTimeLocal()));
				entityTest.setEntityJoinTest(entityJoinTest);
				entityManager.persist(entityTest);
			}
		}
		cb= entityManager.getCriteriaBuilder();
		query1= cb.createQuery(EntityJoinTest.class);
		root1 = query1.from(EntityJoinTest.class);
		query2= cb.createQuery(EntityTest.class);
		root2 = query2.from(EntityTest.class);
	}

	@AfterEach
	void tearDown() throws Exception {
		entityManager.createQuery("delete from EntityTest").executeUpdate();
		entityManager.createQuery("delete from EntityJoinTest").executeUpdate();
	}

	@Test
	void testAddSubFetch() {
		//fail("Not yet implemented");
	}

	@Test
	void testCustomSpecification() {
		//fail("Not yet implemented");
	}

	@Test
	void testToPredicate() {
		//fail("Not yet implemented");
	}

	@Test
	void testToCustomPredicate() {
		//fail("Not yet implemented");
	}



}
