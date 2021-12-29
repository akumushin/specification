package common;

public class OrderBy {
	private String field;
	private boolean isDesc;
	public boolean isAscending() {
		return !isDesc;
	}
	public boolean isDescending() {
		return isDesc;
	}
	public void ascending() {
		isDesc = false;
	}
	public void descending() {
		isDesc = true;
	}
	public String getField() {
		return field;
	}
	public void setField(String field) {
		this.field = field;
	}
}
