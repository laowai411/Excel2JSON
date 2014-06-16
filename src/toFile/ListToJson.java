package toFile;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JOptionPane;

import jxl.Cell;
import jxl.CellType;

public class ListToJson implements IListToFile {

	/**
	 * 字段名
	 * 
	 */
	private Vector<String> keyList;

	public ListToJson() {
	}

	/**
	 * 将从Excel得到的列表数据转换成json
	 * 
	 */
	@SuppressWarnings("rawtypes")
	public void writeFile(HashMap data) {
		String dataStr = get_str(data);
		if (dataStr != null) {
			FileWriter writter = null;
			try {
				File file = new File((String) data.get("name") + ".json");
				if (file.exists() == false) {
					try {
						file.createNewFile();
					} catch (IOException ex) {
						Logger.getLogger(ListToJson.class.getName()).log(
								Level.SEVERE, null, ex);
					}
				}
				writter = new FileWriter(file);
				writter.write(dataStr);
				writter.close();
			} catch (IOException ex) {
				Logger.getLogger(ListToJson.class.getName()).log(Level.SEVERE,
						null, ex);
			} finally {
				try {
					writter.close();
				} catch (IOException ex) {
					Logger.getLogger(ListToJson.class.getName()).log(
							Level.SEVERE, null, ex);
				}
			}
		}
	}

	@SuppressWarnings({ "rawtypes" })
	private String get_str(HashMap data) {
		String result = "";
		getKey(data);
		ArrayList sheetList = (ArrayList) data.get("data");
		for (int i = 0; i < sheetList.size(); i++) {
			result = result.concat("{" + getJSONFormatStr());
			ArrayList sheetData = (ArrayList) sheetList.get(i);
			int row = (int) data.get("row");
			int col = (int) data.get("col");
			for (int rowIndex = 2; rowIndex < row; rowIndex++) {
				ArrayList colList = (ArrayList) sheetData.get(0);
				// id
				result = result
						.concat("\""
								+ ((Cell) colList.get(rowIndex)).getContents()
								+ "\":{");
				for (int colIndex = 1; colIndex < col; colIndex++) {
					colList = (ArrayList) sheetData.get(colIndex);
					if (keyList.get(colIndex) == null
							|| keyList.get(colIndex).equals("") == true) {
						continue;
					}
					Cell cell = (Cell) colList.get(rowIndex);
					// 单元格为空字符串则输出空字符串
					if (cell.getContents().equals("") == true) {
						result = result.concat("\"" + keyList.get(colIndex)
								+ "\":\"\",");
					} else {
						CellType type = cell.getType();
						String cellValue = "";
						if (type == CellType.BOOLEAN
								|| type == CellType.BOOLEAN_FORMULA
								|| type == CellType.NUMBER
								|| type == CellType.NUMBER_FORMULA
								|| type == CellType.EMPTY) {
							cellValue = cell.getContents();
						} else {
							cellValue = cell.getContents();
							if (cellValue.startsWith("[")
									&& cellValue.endsWith("]")) {
								// 是数组
							} else if (cellValue.startsWith("{")
									&& cellValue.endsWith("}")) {
								// 是对象
							} else {
								cellValue = "\"" + cell.getContents() + "\"";
							}
						}

						result = result.concat("\"" + keyList.get(colIndex)
								+ "\":" + cellValue + ",");
					}
				}
				result = result.substring(0, result.length() - 1);
				result = result.concat("}," + getJSONFormatStr());
			}
			try {
				result = result.substring(0, result.lastIndexOf(",")).concat(
						getJSONFormatStr());
			} catch (Exception e) {
				JOptionPane.showMessageDialog(null, "Excel中没有有效数据!");
			}
			result = result.substring(0, result.length() - 1);
			result = result.concat("}");
		}
		return result;
	}

	private String getJSONFormatStr() {
		return "\n\t";
	}

	/**
	 * 从固定的行获取属性名
	 * 
	 */
	@SuppressWarnings({ "rawtypes" })
	private void getKey(HashMap data) {
		keyList = new Vector<String>();
		ArrayList valueList = (ArrayList) data.get("data");
		for (int i = 0; i < valueList.size(); i++) {
			ArrayList sheetData = (ArrayList) valueList.get(i);
			int col = (int) data.get("col");
			for (int colIndex = 0; colIndex < col; colIndex++) {
				ArrayList colList = (ArrayList) sheetData.get(colIndex);
				keyList.add(colIndex, ((Cell) colList.get(1)).getContents());
			}
		}
	}
}
