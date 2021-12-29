package common.filter;

public enum CompareType {
	Equal,
	NotEqual,
	Like, // '%%'
	GreaterThan,
	LessThan,
	EqualLessThan,
	EqualGreaterThan,
	HasContains,
	In,
	NotIn
}
