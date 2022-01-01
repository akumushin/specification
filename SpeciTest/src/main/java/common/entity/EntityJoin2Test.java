package common.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Entity
@Table
@Data
public class EntityJoin2Test {
	@Id
	private Integer id;
}
