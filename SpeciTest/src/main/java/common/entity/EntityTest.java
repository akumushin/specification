package common.entity;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import common.EnumTest;
import lombok.Data;

@Entity
@Table
@Data
public class EntityTest {
	@Id
	private Integer id;
	private Double double1;
	private String string1;
	private LocalDate dateLocal;
	private Date dateUtil;
	private java.sql.Date dateSql;
	private LocalDateTime timeLocal;
	private Timestamp timestamp;
	@Enumerated(EnumType.STRING)
	private EnumTest enumTest;
	private Boolean boolean1;
	
	@ManyToOne
	@JoinColumn
	private EntityJoinTest entityJoinTest;
}
