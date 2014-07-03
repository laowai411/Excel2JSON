package common;


/**
 * json解析相关常量定义
 * */
public class JSONConst {

	/**
	 * Number类型
	 * */
	public static final String TYPE_NUMER = "num";
	
	/**
	 * String类型
	 * */
	public static final String TYPE_STRING = "str";
	
	/**
	 * Array类型
	 * */
	public static final String TYPE_ARRAY = "arr";
	
	/**
	 * Object类型
	 * */
	public static final String TYPE_OBJECT = "obj";
	
	/**
	 * 获取一个key对应的value是否为字符串类型
	 * */
	public static boolean get_isString(String type)
	{
		return type.indexOf(TYPE_STRING)>-1;
	}
	/**
	 * 获取一个key对应的value是否为数字类型
	 * */
	public static boolean get_isNumber(String type)
	{
		return type.indexOf(TYPE_NUMER)>-1;
	}
	/**
	 * 获取一个key对应的value是否为对象类型
	 * */
	public static boolean get_isObject(String type)
	{
		return type.indexOf(TYPE_OBJECT)>-1;
	}
	/**
	 * 获取一个key对应的value是否为数组类型
	 * */
	public static boolean get_isArray(String type)
	{
		return type.indexOf(TYPE_ARRAY)>-1;
	}
}
