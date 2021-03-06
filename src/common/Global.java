package common;

import java.util.HashMap;
import javax.swing.JButton;

/**
 * app配置
 * */
public class Global {

	/**
	 * DEBUG模式会创建配置文件
	 * */
	public static final boolean IS_DEBUG = true;

	
	public static final String readMe_path = "etc/readMe.txt";

	public static final String config_path = "etc/config.json";
	

	public static final int delay = 500;

	public static String file_path = "";

	public static boolean export_json_format = true;

	/**
	 * 是否正在转换
	 * */
	public static boolean isParsing;
	
	/**
	 * 程序运行路径
	 * */
	@SuppressWarnings("rawtypes")
	private static Class mainClass;
	
	@SuppressWarnings("rawtypes")
	public static void setRunPath(Class root)
	{
		mainClass = root;
	}
	
	/**
	 * 返回程序当前运行目录
	 * */
	public static String getRunPath()
	{
		return mainClass.getResource("").getPath().substring(1);
	}

	@SuppressWarnings("rawtypes")
	private static HashMap buttonMap = new HashMap();

	@SuppressWarnings("unchecked")
	public static void registerJButton(JButton btn) {
		buttonMap.put(btn, btn);
	}

	public static void setWindowEnable(boolean enable) {
		Object[] keys = buttonMap.keySet().toArray();
		for (int i = 0; i < keys.length; i++) {
			((JButton) buttonMap.get(keys[i])).setEnabled(enable);
		}
	}
}
