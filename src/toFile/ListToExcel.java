package toFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JOptionPane;

import jxl.Cell;
import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;

public class ListToExcel implements IListToFile {

	/**
	 * 字段名存储的Map
	 * */
	private HashMap<String, Object> attNameMap;

	public void writeFile(Object data) {
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void writeFile(HashMap data) {
		if (data != null) {
			try {
				File file = new File((String) data.get("name") + ".xls");
				if (file.exists() == false) {
					try {
						file.createNewFile();
					} catch (IOException ex) {
						Logger.getLogger(ListToJson.class.getName()).log(
								Level.SEVERE, null, ex);
						return;
					}
				}
				WritableWorkbook book = Workbook.createWorkbook(file);
				WritableSheet sheet = book.createSheet(
						(String) data.get("sheetName"),
						book.getNumberOfSheets());
				writeRow(sheet, (HashMap) data.get("data"));
				book.write();
				try {
					book.close();
				} catch (WriteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} catch (FileNotFoundException fe) {
				JOptionPane.showMessageDialog(null, data.get("name")
						+ ".xls\n正在被使用!");
			} catch (IOException ex) {
				Logger.getLogger(ListToExcel.class.getName()).log(Level.SEVERE,
						null, ex);
			}
		}
	}

	@SuppressWarnings("rawtypes")
	private void writeRow(WritableSheet sheet, HashMap data) {
		if (data == null) {
			return;
		}
		attNameMap = new HashMap<>();
		Object[] keys = data.keySet().toArray();
		for (int rowIndex = 0; rowIndex < keys.length; rowIndex++) {
			Label label = new Label(0, rowIndex + 2, (String) keys[rowIndex]);
			try {
				sheet.addCell(label);
				HashMap colMap = (HashMap) data.get(keys[rowIndex]);
				Object[] attNames = colMap.keySet().toArray();
				for (int colIndex = 0; colIndex < attNames.length; colIndex++) {
					String attName = (String) attNames[colIndex];
					label = (Label) sheet.findLabelCell(attName);
					if (label == null) {
						label = new Label(sheet.getColumns(), 1, attName);
						sheet.addCell(label);
						attNameMap.put(attName, label);
					}
					Object att = colMap.get(attNames[colIndex]);
					label = new Label(
							((Cell) attNameMap.get(attName)).getColumn(),
							rowIndex + 2, getCellStr(att));
					sheet.addCell(label);
				}
			} catch (WriteException ex) {
				Logger.getLogger(ListToExcel.class.getName()).log(Level.SEVERE,
						null, ex);
				JOptionPane.showMessageDialog(null,
						"写入excel失败!\n" + ex.getMessage());
			}
		}
	}

	@SuppressWarnings({ "rawtypes" })
	private String getCellStr(Object o) {
		String str = "";
		if (o instanceof Object[]) {
			str = convertArray((Object[]) o, str);
		} else if (o instanceof HashMap) {
			str = convertHashMap((HashMap) o, str);
		} else if (o instanceof Integer) {
			str = convertInt((int) o, str);
		} else if (o instanceof String) {
			str = convertString(str, (String) o);
		}
		return str;
	}

	@SuppressWarnings({ "rawtypes" })
	private String convertArray(Object[] arr, String str) {
		str += "[";
		for (int i = 0; i < arr.length; i++) {
			Object o = arr[i];
			if (o instanceof Object[]) {
				str = convertArray((Object[]) o, str);
			} else if (o instanceof HashMap) {
				str = convertHashMap((HashMap) o, str);
			} else if (o instanceof Integer) {
				str = convertInt((int) o, str);
			} else if (o instanceof String) {
				str = convertString(str, (String) o);
			}
			str += ",";
		}
		int searchIndex = str.lastIndexOf(",");
		if(searchIndex > -1)
		{
			str = str.substring(0, str.lastIndexOf(","));
		}
		str += "]";
		return str;
	}

	@SuppressWarnings("rawtypes")
	private String convertHashMap(HashMap map, String str) {
		str += "{";
		Object[] keys = map.keySet().toArray();
		for (int i = 0; i < keys.length; i++) {
			if(keys[i] instanceof Integer)
			{
				
			}
			else
			{
				str += "\"" + keys[i].toString() + "\":";
			}
			Object o = map.get(keys[i]);
			if (o instanceof Object[]) {
				str = convertArray((Object[]) o, str);
			} else if (o instanceof HashMap) {
				Object tempKey = ((HashMap)o).keySet().toArray()[0];
				if(((HashMap)o ).size() == 1 && tempKey instanceof Integer && Integer.parseInt((String) tempKey) == 0)
				{
					str += "\""+tempKey.toString()+"\""+((HashMap)o).get(tempKey).toString();
				}
				else
				{
					str = convertHashMap((HashMap) o, str);
				}
			} else if (o instanceof Integer) {
				str = convertInt((int) o, str);
			} else if (o instanceof String) {
				str = convertString(str, (String) o);
			}
			str += ",";
		}
		int searchIndex = str.lastIndexOf(",");
		if(searchIndex > -1)
		{
			str = str.substring(0, str.lastIndexOf(","));
		}
		str += "}";
		return str;
	}

	private String convertString(String srcStr, String value) {
		srcStr += value;
		return srcStr;
	}

	private String convertInt(int value, String str) {
		str += value;
		return str;
	}
}
