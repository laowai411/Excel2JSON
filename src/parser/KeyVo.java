package parser;

import java.util.HashMap;


/**
 * JSON属性名,类型,和子元素类型Map
 * */
public class KeyVo {

	public static final String SPECIAL_KEY = "2014-07_special_key";
	
	public String key;
	
	public String keyType;
	
	@SuppressWarnings("rawtypes")
	public HashMap subKeyMap = new HashMap<>();
	
	public static KeyVo createKeyVo(Object key, String keyType)
	{
		KeyVo keyVo = new KeyVo();
		keyVo.key = key.toString();
		keyVo.keyType = keyType;
		return keyVo;
	}
	
	/**
	 * 放入一个子类型KeyVo
	 * */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void putSubKey(Object key, KeyVo subKeyVo)
	{
		if(this.subKeyMap != null && subKeyVo != null)
		{
			if(this.subKeyMap.get(key) != null)
			{
				KeyVo tSubKeyVo = (KeyVo) this.subKeyMap.get(key);
				if(tSubKeyVo.keyType.equals(subKeyVo.keyType) == true)
				{
					if(subKeyVo.subKeyMap != null && subKeyVo.subKeyMap.size()>0)
					{
						if(tSubKeyVo.subKeyMap.keySet().size() < subKeyVo.subKeyMap.keySet().size())
						{
							this.subKeyMap.put(key, subKeyVo);
						}
					}
					return;
				}
			}
		}
		else if(subKeyMap == null)
		{
			subKeyMap = new HashMap();
		}
		this.subKeyMap.put(key, subKeyVo);
	}
}
