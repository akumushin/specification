package common.utils;

public class ValueUtils {
	public static Long toLong(String value) {
		try {
			return Long.parseLong(value);
		}catch(Exception e) {
			return null;
		}
	}
	public static Integer toInteger(String value) {
		try {
			return Integer.parseInt(value);
		}catch(Exception e) {
			return null;
		}
	}
	public static Boolean toBoolean(String value) {
		try {
			return Boolean.parseBoolean(value);
		}catch(Exception e) {
			return null;
		}
	}
	public static boolean isBlank(String value) {
		return value== null|| value.length()==0;
	}
}
