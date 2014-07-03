package parser;

import java.util.HashMap;


/**
 * JSON属性名,类型,和子元素类型Map
 * */
public class KeyVo {

	public String key;
	
	public String keyType;
	
	@SuppressWarnings("rawtypes")
	public HashMap subKeyMap = new HashMap<>();
	
	@SuppressWarnings("rawtypes")
	public static KeyVo createKeyVo(Object key, String keyType)
	{
		KeyVo keyVo = new KeyVo();
		keyVo.key = key.toString();
		keyVo.keyType = keyType;
//		keyVo.subKeyMap = subKeyMap;
		return keyVo;
	}
	
	/**
	 * 放入一个子类型KeyVo
	 * */
	@SuppressWarnings("unchecked")
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
