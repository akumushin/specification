package common.filter;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.JoinType;

public class FetchAttribute {
	public FetchAttribute(String attribute, JoinType joinType) {
		super();
		this.attribute = attribute;
		this.joinType = joinType;
	}
	private String attribute;
	private JoinType joinType;
	private final List<FetchAttribute> subFetchBuiders = new ArrayList<>();
	public String getAttribute() {
		return attribute;
	}
	public List<FetchAttribute> getSubFetchAttribute() {
		return subFetchBuiders;
	}
	/**
	 * return sub fetch
	 * @param name
	 * @param joinType
	 * @return
	 */
	public FetchAttribute addSubFetch(String name, JoinType joinType) {
		FetchAttribute sub= new FetchAttribute(name, joinType);
		sub.attribute=name;
		return sub;
	}
	public JoinType getJoinType() {
		return joinType;
	}
	
}
