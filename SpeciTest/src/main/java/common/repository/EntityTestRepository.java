package common.repository;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import common.entity.EntityTest;
@Repository
public interface EntityTestRepository extends JpaSpecificationExecutor<EntityTest>{

}
