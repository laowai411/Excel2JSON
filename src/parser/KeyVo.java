package parser;

import java.util.HashMap;


/**
 * JSON属性名,类型,和子元素类型Map
 * */
public class KeyVo {

	public String key;
	
	public String keyType;
	
	@SuppressWarnings("rawtypes")
	public HashMap subKeyMap;
	
	@SuppressWarnings("rawtypes")
	public static KeyVo createKeyVo(Object key, String keyType, HashMap subKeyMap)
	{
		KeyVo keyVo = new KeyVo();
		keyVo.key = key.toString();
		keyVo.keyType = keyType;
		keyVo.subKeyMap = subKeyMap;
		return keyVo;
	}
}
