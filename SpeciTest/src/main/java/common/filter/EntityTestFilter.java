package common.filter;

import lombok.Data;
@Data
public class EntityTestFilter {
	private Integer id;
	@SearchColumn(compare = CompareType.HasContains)
	private String string1;
	@SearchJoinColumn
	private EntityJoinTestFilter entityJoinTest;
	
	@SearchColumn(compare = CompareType.HasContains, name = "string1")
	private String string2;
	@SearchJoinColumn(name="entityJoinTest")
	private EntityJoinTestFilter entityJoinTest2;
	
	private Integer noFilter;
}
