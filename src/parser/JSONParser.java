package parser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
	private String dataStr;
	/**
	 * json文件数据 [key:[ key:[ ... ] ] ]
	 * 
	 */
	@SuppressWarnings("rawtypes")
	private HashMap jsonData;
	/**
	 * 包含了excel名字,sheet名字和json数据
	 * 
	 */
	private HashMap<String, Serializable> data;

	@Override
	public void parse(File file) {
		data = new HashMap<String, Serializable>();
		this.file = file;
		readJsonFile(file);
		decodeJsonStr();
	}

	/**
	 * 将json字符串转化
	 * 
	 */
	@SuppressWarnings("rawtypes")
	private void decodeJsonStr() {
		try {
			JSONObject jsonObj = new JSONObject(dataStr);
			jsonData = new HashMap();
			decodeJsonData(jsonData, jsonObj);
		} catch (JSONException ex) {
			// 格式错误
			JOptionPane.showMessageDialog(null, file.getName() + "格式错误!\n  "
					+ ex.getMessage());
		}
	}

	/**
	 * 解析Json数据并保存
	 * 
	 */
	@SuppressWarnings({ "rawtypes" })
	private HashMap decodeJsonData(HashMap map, JSONObject json) {
		Iterator keys = json.keys();
		while (keys.hasNext() == true) {
			String key = (String) keys.next();
			try {
				Object o = json.get(key);
				if (o instanceof JSONObject) {
					decodeObject(map, key, (JSONObject)o);
				} else if(o instanceof JSONArray)
				{
					decodeArray(map, key, (JSONArray) o);
				}
				else if(o instanceof Integer)
				{
					decodeSimple(map, key, (int)o);
				}
				else if (o instanceof String) {
					decodeSimple(map, key, (String) o);
				}
				keys.remove();
			} catch (JSONException ex) {
				Logger.getLogger(JSONParser.class.getName()).log(Level.SEVERE,
						null, ex);
			}
		}
		return map;
	}

	/**
	 * 解析JsonObject
	 * */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void decodeObject(HashMap parentMap, Object key, JSONObject value)
	{
		JSONObject jsonObj = (JSONObject) value;
		Iterator<Object> keys = jsonObj.keys();
		HashMap<Object, Object> selfMap = new HashMap<>();
		parentMap.put(key, selfMap);
		while(keys.hasNext())
		{
			Object childKey = keys.next();
			try {
				Object childObj = jsonObj.get((String) childKey);
				if(childObj instanceof JSONObject)
				{
					decodeObject(selfMap, childKey, (JSONObject)childObj);
				}
				else if(childObj instanceof JSONArray)
				{
					decodeArray(selfMap, childKey, (JSONArray)childObj);
				}
				if(childObj instanceof Integer)
				{
					decodeSimple(selfMap, childKey, (int)childObj);
				}
				else if(childObj instanceof String)
				{
					decodeSimple(selfMap, childKey, (String)childObj);
				}
				keys.remove();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/**
	 * 解析JsonArray
	 * 
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void decodeArray(HashMap parentMap, Object key, JSONArray jsonArray) {
		Object[] childArray = new Object[jsonArray.length()];
		parentMap.put(key, childArray);
		for(int i=0; i<jsonArray.length(); i++)
		{
			try {
				Object childObj = jsonArray.get(i);
//				if(childObj instanceof JSONObject)
//				{
//					
//				}
//				else if(childObj instanceof JSONArray)
//				{
//					
//				}
//				else 
				if(childObj instanceof Integer)
				{
					childArray[i] = childObj;
				}
				else if(childObj instanceof String)
				{
					childArray[i] = childObj;
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 解析简单数据类型
	 * 
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void decodeSimple(HashMap parentMap, Object key, Object value) {
		parentMap.put(key, value);
	}

	/**
	 * 读取json文件
	 * 
	 */
	private void readJsonFile(File file) {
		BufferedReader reader = null;
		dataStr = "";
		try {
			reader = new BufferedReader(new FileReader(file));
			String tempString = null;
			// 一次读入一行，直到读入null为文件结束
			while ((tempString = reader.readLine()) != null) {
				// 显示行号
				dataStr = dataStr.concat(tempString);
			}
			reader.close();
		} catch (IOException e) {
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

	@Override
	public HashMap<String, Serializable> getData(File file) {
		// TODO Auto-generated method stub
		parse(file);
		String fileName = file.getName();
		fileName = fileName.substring(0, fileName.lastIndexOf("."));
		data.put("name", file.getParent() + "\\" + fileName);
		data.put("sheetName", fileName);
		data.put("data", jsonData);
		return data;
	}

	@Override
	public String get_type() {
		return "json";
	}
}
