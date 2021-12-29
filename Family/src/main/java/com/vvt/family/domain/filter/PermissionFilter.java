package com.vvt.family.domain.filter;

import org.hibernate.type.EntityType;

import common.filter.CompareType;
import common.filter.FilterColumn;
import common.type.EntityName;
import lombok.Data;
@Data
public class PermissionFilter {
	public PermissionFilter(int id) {
		this.id=id;
	}

	public PermissionFilter(int id, EntityName entityName) {
		this.id=id;
		this.entityName= entityName;
	}

	@FilterColumn(compare = CompareType.Equal)
	private Integer id;
	@FilterColumn(compare = CompareType.Equal)
	private EntityName entityName;
}
