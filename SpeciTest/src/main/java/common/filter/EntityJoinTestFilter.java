package common.filter;

import javax.persistence.Id;

import lombok.Data;

@Data
public class EntityJoinTestFilter {
	@SearchColumn
	private Integer id;
	@SearchColumn(compare = CompareType.HasContains)
	private String name;
}
