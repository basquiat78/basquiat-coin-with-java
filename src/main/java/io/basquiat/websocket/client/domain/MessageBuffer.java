package io.basquiat.websocket.client.domain;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * MessageBuffer
 * 임시방편....
 * created by basquiat
 *
 */
public class MessageBuffer {
	
	private static final Map<String, StringBuffer> MESSGE_BUFFER = new ConcurrentHashMap<>();
	
	/**
	 * add MESSGE_BUFFER
	 * @param key
	 * @param value
	 */
	public static void addMessageBuffer(String key, String value) {
		StringBuffer stringBuffer = MESSGE_BUFFER.get(key);
		if(stringBuffer == null) {
			stringBuffer = new StringBuffer();
		}
		stringBuffer.append(value);
		MESSGE_BUFFER.put(key, stringBuffer);
	}
	
	/**
	 * get MESSGE_BUFFER
	 * @param key
	 * @return String
	 */
	public static String getMessageBuffer(String key) {
		return MESSGE_BUFFER.get(key).toString();
	}
	
	/**
	 * clear MessageBuffer
	 * @param key
	 */
	public static void clearMessageBuffer(String key) {
		MESSGE_BUFFER.remove(key);
	}

}
