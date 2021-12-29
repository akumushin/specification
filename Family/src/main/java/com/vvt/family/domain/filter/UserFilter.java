package com.vvt.family.domain.filter;

import java.util.List;

import javax.persistence.criteria.JoinType;

import common.filter.CompareType;
import common.filter.FilterArray;
import common.filter.FilterArrayType;
import common.filter.FilterColumn;
import lombok.Data;
@Data
public class UserFilter {
	@FilterColumn(compare = CompareType.EqualLessThan)
	private Integer id;
	@FilterColumn(compare = CompareType.Like)
	private String username;
	@FilterArray(arrayType = FilterArrayType.Any)
	@FilterColumn(compare = CompareType.Equal)
	private String[] email;
	@FilterColumn(compare = CompareType.Equal)
	private Boolean isStaff;
	private Boolean isSuper;
	private Boolean isEnabled;
	@FilterArray(arrayType = FilterArrayType.Any)
	@FilterColumn(isJoinColumn = true, joinType = JoinType.LEFT, compare = CompareType.Equal)
	private List<PermissionFilter> permissions;
	private List<PermissionGroupFilter> permissionGroups;
}
