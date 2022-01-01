package common.entity;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.Data;

@Entity
@Table
@Data
public class EntityJoinTest {
	@Id
	private Integer id;
	private String name;
	
	@ManyToMany
	@JoinTable(name = "join_join", joinColumns = @JoinColumn(name="join1_id"), inverseJoinColumns = @JoinColumn(name="join2_id"))
	private List<EntityJoin2Test> entityJoin2Tests;
}
