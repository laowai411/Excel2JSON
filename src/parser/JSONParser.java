package parser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import javax.swing.JOptionPane;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import common.JSONConst;
import common.LogUtil;
import common.unicode.UnicodeReader;

public class JSONParser implements IParser {

	/**
	 * json文件
	 * 
	 */
	private File file;
	/**
	 * json文件字符串数据
	 * 
	 */
	private String jsonStr;
	/**
	 * Object形式的json文件数据
	 * 
	 */
	@SuppressWarnings("rawtypes")
	private HashMap objectJson = new HashMap<>();;
	
	/**
	 * array形式的json数据
	 * */
	@SuppressWarnings("rawtypes")
	private ArrayList arrayJson = new ArrayList<>();;
	
	/**
	 * 包含了excel名字,sheet名字和json数据
	 * 
	 */
	private HashMap<String, Serializable> data = new HashMap<>();;
	
	/**
	 * 行数
	 * */
	private int row;
	
	/**
	 * 列数
	 * */
	private int col;
	
	/**
	 * 属性名
	 * */
	@SuppressWarnings("rawtypes")
	private HashMap attKeyMap = new HashMap<>();;
	
	/**
	 * 条目索引
	 * */
	private ArrayList<Object> indexList = new ArrayList<>();;
	
	@Override
	public void parse(File file) {
		arrayJson.clear();
		objectJson.clear();
		data.clear();
		attKeyMap.clear();
		indexList.clear();
		row = 0;
		col = 0;
		jsonStr = "";
		
		this.file = file;
		readJsonFile(file);
		decodeJsonStr();
	}

	/**
	 * 将json字符串转化
	 */
	@SuppressWarnings({ "rawtypes" })
	private void decodeJsonStr() {
		jsonStr = checkAndReplaceMultiLine(jsonStr);
		jsonStr = jsonStr.trim();
		try {
			JSONObject jsonObj = new JSONObject(jsonStr);
			objectJson = new HashMap();
			decodeJSON_Object(objectJson, jsonObj);
		} catch (JSONException ex) {
			try {
				JSONArray jsonArray = new JSONArray(jsonStr);
				arrayJson = new ArrayList<>();
				decodeJSON_Array(arrayJson, jsonArray);
			} catch (JSONException e) {
				LogUtil.error(file.getName()+"  "+ex.getMessage());
				LogUtil.error(file.getName()+"  "+e.getMessage());
				JOptionPane.showMessageDialog(null, file.getName() + "格式错误!\n  " + ex.getMessage()+"\n"+e.getMessage());
				LogUtil.error(file.getName() + "格式错误!\n  ");
			}
		}
	}
	
	/**
	 * 表结构为Object
	 * */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void decodeJSON_Object(HashMap map, JSONObject value)
	{
		Iterator keys = value.keys();
		while(keys.hasNext())
		{
			Object key = keys.next();
			Object o;
			try {
				o = value.get(key.toString());
				String keyType = "";
				if(o instanceof Number)
				{
					keyType = JSONConst.TYPE_NUMER;
					KeyVo keyVo = (KeyVo) (attKeyMap.get(key)!=null?attKeyMap.get(key):KeyVo.createKeyVo(key, keyType));
					attKeyMap.put(key, keyVo);
					put(map, key, o);
					col++;
				}
				else if(o instanceof String)
				{
					keyType = JSONConst.TYPE_STRING;
					KeyVo keyVo = (KeyVo) (attKeyMap.get(key)!=null?attKeyMap.get(key):KeyVo.createKeyVo(key, keyType));
					attKeyMap.put(key, keyVo);
					decodeString(map, key, o);
					col++;
				}
				else if(o instanceof JSONArray)
				{
					ArrayList childList = new ArrayList<>();
					put(map, key, childList);
					keyType = JSONConst.TYPE_ARRAY;
					KeyVo keyVo = (KeyVo) (attKeyMap.get(key)!=null?attKeyMap.get(key):KeyVo.createKeyVo(key, keyType));
					attKeyMap.put(key, keyVo);
					decodeArray(childList, (JSONArray) o, keyVo);
					col++;
				}
				else if(o instanceof JSONObject)
				{
					HashMap childMap = new HashMap();
					put(map, key, childMap);
					decodeRow(childMap, (JSONObject) o);
				}
				indexList.add(key);
				row++;
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 表结构为Array
	 * */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void decodeJSON_Array(ArrayList list, JSONArray value)
	{
		int len = value.length();
		for(int i=0; i<len; i++)
		{
			Object o;
			try {
				o = value.get(i);
				String keyType = "";
				if(o instanceof Number)
				{
					keyType = JSONConst.TYPE_NUMER;
					KeyVo keyVo = (KeyVo) (attKeyMap.get(i)!=null?attKeyMap.get(i):KeyVo.createKeyVo(i, keyType));
					attKeyMap.put(i, keyVo);
					put(list, i, o);
					col++;
				}
				else if(o instanceof String)
				{
					keyType = JSONConst.TYPE_STRING;
					KeyVo keyVo = (KeyVo) (attKeyMap.get(i)!=null?attKeyMap.get(i):KeyVo.createKeyVo(i, keyType));
					attKeyMap.put(i, keyVo);
					decodeString(list, i, o);
					col++;
				}
				else if(o instanceof JSONArray)
				{
					ArrayList childList = new ArrayList<>();
					put(list, i, childList);
					keyType = JSONConst.TYPE_ARRAY;
					KeyVo keyVo = (KeyVo) (attKeyMap.get(i)!=null?attKeyMap.get(i):KeyVo.createKeyVo(i, keyType));
					attKeyMap.put(i, keyVo);
					decodeArray(childList, (JSONArray) o, keyVo);
					col++;
				}
				else if(o instanceof JSONObject)
				{
					HashMap childMap = new HashMap();
					put(list, i, childMap);
					decodeRow(childMap, (JSONObject) o);
				}
				indexList.add(i);
				row++;
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 解析Object类型
	 * */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void decodeRow(HashMap map, JSONObject value)
	{
		Iterator keys = value.keys();
		while(keys.hasNext())
		{
			Object key = keys.next();
			Object o;
			try {
				o = value.get(key.toString());
				String keyType = "";
				if(o instanceof Number)
				{
					keyType = JSONConst.TYPE_NUMER;
					KeyVo keyVo = (KeyVo) (attKeyMap.get(key)!=null?attKeyMap.get(key):KeyVo.createKeyVo(key, keyType));
					attKeyMap.put(key, keyVo);
					put(map, key, o);
				}
				else if(o instanceof String)
				{
					keyType = JSONConst.TYPE_STRING;
					KeyVo keyVo = (KeyVo) (attKeyMap.get(key)!=null?attKeyMap.get(key):KeyVo.createKeyVo(key, keyType));
					attKeyMap.put(key, keyVo);
					decodeString(map, key, o);
				}
				else if(o instanceof JSONArray)
				{
					keyType = JSONConst.TYPE_ARRAY;
					KeyVo keyVo = (KeyVo) (attKeyMap.get(key)!=null?attKeyMap.get(key):KeyVo.createKeyVo(key, keyType));
					attKeyMap.put(key, keyVo);
					ArrayList childList = new ArrayList<>();
					put(map, key, childList);
					decodeArray(childList, (JSONArray) o, keyVo);
				}
				else if(o instanceof JSONObject)
				{
					keyType = JSONConst.TYPE_OBJECT;
					KeyVo keyVo = (KeyVo) (attKeyMap.get(key)!=null?attKeyMap.get(key):KeyVo.createKeyVo(key, keyType));
					attKeyMap.put(key, keyVo);
					HashMap childMap = new HashMap();
					put(map, key, childMap);
					decodeObject(childMap, (JSONObject) o, keyVo);
				}
				col++;
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 解析Object类型
	 * */
	@SuppressWarnings({ "rawtypes" })
	private void decodeObject(HashMap map, JSONObject value, KeyVo keyVo)
	{
		Iterator keys = value.keys();
		while(keys.hasNext())
		{
			Object key = keys.next();
			Object o;
			try {
				o = value.get(key.toString());
				String keyType = "";
				if(o instanceof Number)
				{
					keyType = JSONConst.TYPE_NUMER;
					KeyVo subKeyVo = (KeyVo) ((keyVo.subKeyMap!=null && keyVo.subKeyMap.get(key)!=null)?keyVo.subKeyMap.get(key):KeyVo.createKeyVo(key, keyType));
					keyVo.putSubKey(key, subKeyVo);
					put(map, key, o);
				}
				else if(o instanceof String)
				{
					keyType = JSONConst.TYPE_STRING;
					KeyVo subKeyVo = (KeyVo) ((keyVo.subKeyMap!=null && keyVo.subKeyMap.get(key)!=null)?keyVo.subKeyMap.get(key):KeyVo.createKeyVo(key, keyType));
					keyVo.putSubKey(key, subKeyVo);
					decodeString(map, key, o);
				}
				else if(o instanceof JSONArray)
				{
					keyType = JSONConst.TYPE_ARRAY;
					KeyVo subKeyVo = (KeyVo) ((keyVo.subKeyMap!=null && keyVo.subKeyMap.get(key)!=null)?keyVo.subKeyMap.get(key):KeyVo.createKeyVo(key, keyType));
					keyVo.putSubKey(key, subKeyVo);
					ArrayList childList = new ArrayList<>();
					decodeArray(childList, (JSONArray) o, subKeyVo);
					put(map, key, childList);
				}
				else if(o instanceof JSONObject)
				{
					keyType = JSONConst.TYPE_NUMER;
					KeyVo subKeyVo = (KeyVo) ((keyVo.subKeyMap!=null && keyVo.subKeyMap.get(key)!=null)?keyVo.subKeyMap.get(key):KeyVo.createKeyVo(key, keyType));
					keyVo.putSubKey(key, subKeyVo);
					HashMap childMap = new HashMap();
					decodeObject(childMap, (JSONObject) o, subKeyVo);
					put(map, key, childMap);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 解析array类型
	 * */
	@SuppressWarnings({ "rawtypes" })
	private void decodeArray(ArrayList list, JSONArray value, KeyVo keyVo)
	{
		int len = value.length();
		for(int i=0; i<len; i++)
		{
			Object o;
			try {
				o = value.get(i);
				String keyType = "";
				if(o instanceof Number)
				{
					put(list, i, o);
				}
				else if(o instanceof String)
				{
					decodeString(list, i, o);
				}
				else if(o instanceof JSONArray)
				{
					keyType = JSONConst.TYPE_ARRAY;
					KeyVo subKeyVo = (KeyVo) ((keyVo.subKeyMap!=null && keyVo.subKeyMap.size()>0)?keyVo.subKeyMap.get(0):KeyVo.createKeyVo(0, keyType));
					keyVo.putSubKey(0, subKeyVo);
					ArrayList childList = new ArrayList<>();
					decodeArray(childList, (JSONArray) o, subKeyVo);
					put(list, i, childList);
				}
				else if(o instanceof JSONObject)
				{
					keyType = JSONConst.TYPE_OBJECT;
					KeyVo subKeyVo = (KeyVo) ((keyVo.subKeyMap!=null && keyVo.subKeyMap.size()>0)?keyVo.subKeyMap.get(0):KeyVo.createKeyVo(0, keyType));
					keyVo.putSubKey(0, subKeyVo);
					HashMap childMap = new HashMap();
					decodeObject(childMap, (JSONObject) o, subKeyVo);
					put(list, i, childMap);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 根据key和value put到容器中
	 * */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void put(Object content, Object key, Object value)
	{
		if(content instanceof HashMap)
		{
			((HashMap) content).put(key, value);
		}
		else if(content instanceof ArrayList)
		{
			((ArrayList) content).add((int) key, value);
		}
	}

	/**
	 * 解析字符串数据类型
	 */
	private void decodeString(Object content, Object key, Object value) {
		String str = value.toString();
		str = str.trim();
		if(str.startsWith("\"")==true && str.endsWith("\"")==true)
		{
			put(content, key, str);
		}
		else
		{
			put(content, key, "\""+str+"\"");
		}
	}
	
	/**
	 * 读取json文件
	 * 
	 */
	private void readJsonFile(File file) {
		BufferedReader reader = null;
		jsonStr = "";
		try {
			reader = new BufferedReader(new UnicodeReader(new FileInputStream(file), Charset.defaultCharset().name()));
			String tempString = null;
			// 一次读入一行，直到读入null为文件结束
			while ((tempString = reader.readLine()) != null) {
				tempString = checkAndReplaceSingleLine(tempString);
				tempString = checkAndReplaceMultiLine(tempString);
				jsonStr = jsonStr.concat(tempString);
			}
			reader.close();
		} catch (IOException e) {
			LogUtil.error("readJsonFile  "+file.getName()+"  读取失败");
			e.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e1) {
				}
			}
		}
	}

	/**
	 * 去除注释多行注释
	 * */
	private String checkAndReplaceMultiLine(String str) {
		int start = 0;
		int end = 1;
		while(start > -1 && end > -1 && start < end)
		{
			start = str.indexOf("/*");
			end = str.indexOf("*/");
			if(start > -1 && end > -1 && start<end)
			{
				str = str.substring(0, start)+str.substring(end+2);
			}
		}
		return str;
	}

	/**
	 * 去除单行注释
	 * */
	private String checkAndReplaceSingleLine(String str) {
		int start = str.indexOf("//");
		while(start > -1)
		{
			if(start>=5 && str.substring(start-5, start).equals("http:") == true)
			{
				start = str.indexOf("//", start+1);
			}
			else
			{
				str = str.substring(0, start);
				break;
			}
		}
		return str;
	}

	@Override
	public HashMap<String, Serializable> getData(File file) {
		parse(file);
		String fileName = file.getName();
		fileName = fileName.substring(0, fileName.lastIndexOf("."));
		data.put("name", file.getParent() + "\\" + fileName);
		data.put("sheetName", fileName);
		data.put("row", row);
		data.put("col", col);
		data.put("cols", attKeyMap);
		data.put("rows", indexList);
		if(objectJson != null)
		{
			data.put("data", objectJson);
		}
		else if(arrayJson != null)
		{
			data.put("data", arrayJson);
		}
		else
		{
			return null;
		}
		return data;
	}

	@Override
	public String get_type() {
		return "json";
	}
}
