package common.utils;

import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonUtils {
	public static Map<String, Object> toMap(String jsonString){
		
		ObjectMapper objectMapper = new ObjectMapper();
		try {
			return objectMapper.readValue(jsonString, new TypeReference<Map<String, Object>>() {});
		} catch (Exception e) {
			return null;
		}
	}
	public static String fromMap(Map<String, Object> map) {
		ObjectMapper objectMapper = new ObjectMapper();
		try {
			return objectMapper.writeValueAsString(map);
		}catch(JsonProcessingException e) {
			return null;
		}
	}
}
