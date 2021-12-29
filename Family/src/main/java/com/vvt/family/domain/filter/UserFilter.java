package com.vvt.family.domain.filter;

import java.util.List;

import common.filter.CompareType;
import common.filter.FilterColumn;
import common.filter.FilterJoinColumn;
import lombok.Data;
@Data
public class UserFilter {
	@FilterColumn(compare = CompareType.EqualLessThan)
	private Integer id;
	@FilterColumn(compare = CompareType.Like)
	private String username;
	@FilterColumn(compare = CompareType.HasContains)
	private String email;
	@FilterColumn(compare = CompareType.Equal)
	private Boolean isStaff;
	private Boolean isSuper;
	private Boolean isEnabled;
	private List<PermissionFilter> permissions;
	private List<PermissionGroupFilter> permissionGroups;
}
