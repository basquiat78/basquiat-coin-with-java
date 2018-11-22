package io.basquiat.util;

import java.util.Date;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 
 * created by basquiat
 *
 */
@Component
public class CommonUtil {

	/**
	 * Object convert to json String
	 * 
	 * @param object
	 * @return String
	 * @throws JsonProcessingException
	 */
	public static String convertJsonStringFromObject(Object object) throws JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();
		return mapper.writeValueAsString(object);
	}
	
	/**
	 * json String convert Object
	 * 
	 * @param content
	 * @param clazz
	 * @return T
	 * @throws Exception
	 */
	public static <T> T convertObjectFromJsonString(String content, Class<T> clazz) throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		T object = (T) mapper.readValue(content, clazz);
		return object;
	}

	/**
	 * Date to Timestamp
	 * @param blockTime
	 * @return String
	 */
	public static long convertUnixTime(Date date) {
         return date.getTime()/1000;  
	}

	/**
	 * valid String to Number
	 * @param value
	 * @return
	 */
	public static boolean validNumber(String value) {
		boolean isValid = true;
		try {
			Integer.parseInt(value);
		} catch(Exception e) {
			isValid = false;
		}
		return isValid;
	}
	
}
