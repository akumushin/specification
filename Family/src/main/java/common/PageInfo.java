package common;

import java.util.Arrays;
import java.util.List;

public class PageInfo {
	private int page;
	private int pageSize;
	private int count;
	private int pageTotal;
	private List<OrderBy> orderBys;
	
	
	public PageInfo() {
		this(1,10);
	}
	public PageInfo(int page) {
		this(page,10);
	}
	public PageInfo(int page, int pageSize) {
		this.page=page;
		this.pageSize = pageSize;
	}
	public PageInfo(int page, int pageSize, OrderBy ...orderBys) {
		this.page=page;
		this.pageSize = pageSize;
		this.setOrderBys(Arrays.asList(orderBys));
	}
	public int getPage() {
		return page;
	}
	public void setPage(int page) {
		this.page = page;
	}
	public int getPageSize() {
		return pageSize;
	}
	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	public int getPageTotal() {
		return pageTotal;
	}
	public void setPageTotal(int pageTotal) {
		this.pageTotal = pageTotal;
	}
	public List<OrderBy> getOrderBys() {
		return orderBys;
	}
	public void setOrderBys(List<OrderBy> orderBys) {
		this.orderBys = orderBys;
	}
	

}
