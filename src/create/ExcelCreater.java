package create;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import javax.swing.JOptionPane;

import jxl.Cell;
import jxl.CellType;
import jxl.Sheet;
import jxl.Workbook;
import jxl.biff.EmptyCell;
import jxl.read.biff.BiffException;
import jxl.write.Label;
import jxl.write.WritableCell;
import jxl.write.WritableCellFeatures;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;
import parser.IParser;
import parser.KeyVo;

import common.ExcelConst;
import common.JSONConst;

import etc.ConfigManager;

/**
 * Excel文件生成器
 * */
public class ExcelCreater implements ICreater {

	/**
	 * excel文件
	 * */
	private File file;
	/**
	 * 写Excel文件的代理
	 * */
	private WritableWorkbook book = null;
	/**
	 * sheet存储
	 * */
	@SuppressWarnings("rawtypes")
	private ArrayList sheets;

	@SuppressWarnings("rawtypes")
	private HashMap attKeyMap;

	@SuppressWarnings("unused")
	private int col;
	@SuppressWarnings("unused")
	private int row;
	private Object sheetData;

	private Workbook _templateBook = null;

	@SuppressWarnings({ "rawtypes", "unused" })
	public void writeFile(IParser parser, File srcFile) {
		initTemplateBook();
		HashMap data = parser.getData(srcFile);
		if (data == null) {
			return;
		}
		sheets = new ArrayList<>();
		row = (int) data.get("row");
		col = (int) data.get("col");
		String name = (String) data.get("name");
		attKeyMap = (HashMap) data.get("cols");
		ArrayList indexList = (ArrayList) data.get("rows");
		sheetData = data.get("data");
		createFile(name);
		createSheet((String) data.get("sheetName"));
		try {
			book.write();
			book.close();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (WriteException e) {
			e.printStackTrace();
		} catch (Exception e) {

		}
	}

	/**
	 * 创建多个Sheet
	 * 
	 * @param String
	 *            sheetName 主Sheet名
	 * */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void createSheet(String sheetName) {
		try {
			book = Workbook.createWorkbook(file, _templateBook);
			sheets = new ArrayList<>();
			book.copySheet(0, "template", 1);
			WritableSheet sheet = book.getSheet(0);
			sheets.add(0, sheet);
			sheet.setName(sheetName);
			// 主Sheet属性字段单元格写入并创建子分页
			writeHeadAndCreateSubSheet(null, attKeyMap, 0);

			if (sheetData instanceof HashMap) {
				// 写入配置信息(是否输出,配置表名...等等)
				writeConfig(sheetName, JSONConst.TYPE_OBJECT);
				writeByHashMap((HashMap) sheetData);
			} else if (sheetData instanceof ArrayList) {
				// 写入配置信息(是否输出,配置表名...等等)
				writeConfig(sheetName, JSONConst.TYPE_OBJECT);
				writeByArray((ArrayList) sheetData);
			}
			// 删除模版Sheet页
			book.removeSheet(book.getNumberOfSheets() - 1);
		} catch (FileNotFoundException useE) {
			JOptionPane.showMessageDialog(null, "另一个程序正在使用" + file.getName()
					+ "，进程无法访问");
		} catch (IOException e) {

		}
	}

	/**
	 * 创建分页并写入字段名和类型
	 * */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void writeHeadAndCreateSubSheet(KeyVo arrRoot, HashMap keyMap, int sheetIndex) {
		Sheet sheet = book.getSheet(sheetIndex);
		Iterator keys = keyMap.keySet().iterator();
		int keyIndex = 0;
		while (keys.hasNext()) {
			// 写入字段名
			Object key = keys.next();
			String keyName = key.toString();
			System.out.print("keyName = ");
			System.out.println(keyName);
			Cell label = sheet.getCell(keyIndex + ExcelConst.CONTENT_START.x + 1,
					ExcelConst.HEAD_END_ROW_INDEX);
			if (label == null) {
				label = new Label(keyIndex + ExcelConst.CONTENT_START.x + 1,
						ExcelConst.HEAD_END_ROW_INDEX, keyName.toString());
			} else {
				Label tempLabel = new Label(keyIndex
						+ ExcelConst.CONTENT_START.x + 1,
						ExcelConst.HEAD_END_ROW_INDEX, keyName.toString());
				if (label.getCellFeatures() != null) {
					tempLabel.setCellFeatures((WritableCellFeatures) label
							.getCellFeatures());
				}
				if (label.getCellFormat() != null) {
					tempLabel.setCellFormat(label.getCellFormat());
				}
				label = tempLabel;
			}
			try {
				// 字段数据类型
				KeyVo keyVo = (KeyVo) keyMap.get(key);
				Label keyTypeLabel = new Label(label.getColumn(),
						label.getRow() - 1, keyVo.keyType);
				((WritableSheet) sheet).addCell(keyTypeLabel);
				// 字段名
				((WritableSheet) sheet).addCell((WritableCell) label);
				Sheet subSheet = book.getSheet(keyName);
				int tempSheetIndex = book.getNumberOfSheets() - 1;
				if (keyVo.keyType.equals(JSONConst.TYPE_OBJECT) == true) {
					if (keyVo.subKeyMap != null && keyVo.subKeyMap.size() > 0) {
						if (subSheet == null) {
							if(arrRoot != null)
							{
								keyName = arrRoot.key;
							}
							book.copySheet(tempSheetIndex, keyName, 
									tempSheetIndex);
							subSheet = book.getSheet(tempSheetIndex);
							sheets.add(tempSheetIndex, subSheet);
							writeHeadAndCreateSubSheet(arrRoot, keyVo.subKeyMap,
									tempSheetIndex);
							//在父表写入引用关系
							for(int i=0; i<book.getNumberOfSheets(); i++)
							{
								Sheet parentSheet = book.getSheet(i);
								label = parentSheet.findCell(keyName);
								if(label != null)
								{
									Label linkCell = new Label(label.getColumn(), ExcelConst.LINK_EXCEL_ROW_INDEX, file.getName());
									((WritableSheet) parentSheet).addCell(linkCell);
									linkCell = new Label(label.getColumn(), ExcelConst.LINK_SHEET_ROW_INDEX, tempSheetIndex+"");
									((WritableSheet) parentSheet).addCell(linkCell);
								}
							}
						}
					}
				} else if (keyVo.keyType.equals(JSONConst.TYPE_ARRAY) == true) {
					if (keyVo.subKeyMap != null && keyVo.subKeyMap.size() > 0) {
						arrRoot = keyVo;
						if (subSheet == null) {
							writeHeadAndCreateSubSheet(arrRoot,
									keyVo.subKeyMap,
									tempSheetIndex);
						}
						arrRoot = null;
					}
				}
			} catch (RowsExceededException e) {
				e.printStackTrace();
			} catch (WriteException e) {
				e.printStackTrace();
			}
			keyIndex++;
		}
	}

	/**
	 * json数据是对象
	 * */
	@SuppressWarnings("rawtypes")
	private void writeByHashMap(HashMap map) {
		Iterator rowList = map.keySet().iterator();
		int rowIndex = 0;
		while (rowList.hasNext()) {
			Object index = rowList.next();
			Object rowItem = map.get(index);
			writeString(ExcelConst.CONTENT_START.x, rowIndex, "",
					index.toString(), 0);
			writeRow(rowIndex, rowItem, 0);
			rowList.remove();
			rowIndex++;
		}
	}

	/**
	 * json数据是数组
	 * */
	@SuppressWarnings("rawtypes")
	private void writeByArray(ArrayList list) {
		int len = list.size();
		for (int index = 0; index < len; index++) {
			writeNumber(ExcelConst.CONTENT_START.x, index, "", index, 0);
			writeRow(index, list.get(index), 0);
		}
	}

	/**
	 * 写入一行
	 * 
	 * @param rowIndex
	 *            行索引
	 * @param rowData
	 *            一行的数据
	 * */
	@SuppressWarnings("rawtypes")
	private void writeRow(int rowIndex, Object rowData, int sheetIndex) {
		if (rowData instanceof Number) {
			writeNumber(0, rowIndex, "", rowData, sheetIndex);
		} else if (rowData instanceof String) {
			writeString(0, rowIndex, "", rowData, sheetIndex);
		} else if (rowData instanceof ArrayList) {
			ArrayList list = (ArrayList) rowData;
			int len = list.size();
			for (int i = 0; i < len; i++) {
				writeRow2(i, rowIndex, i, list.get(i), sheetIndex);
			}
		} else if (rowData instanceof HashMap) {
			HashMap map = (HashMap) rowData;
			Iterator keys = map.keySet().iterator();
			int colIndex = 0;
			while (keys.hasNext()) {
				Object key = keys.next();
				Object cellValue = map.get(key);
				writeRow2(colIndex, rowIndex, key, cellValue, sheetIndex);
				keys.remove();
				colIndex++;
			}
		}
	}

	@SuppressWarnings("rawtypes")
	private void writeRow2(int colIndex, int rowIndex, Object key,
			Object cellValue, int sheetIndex) {
		if (cellValue instanceof Number) {
			writeNumber(colIndex, rowIndex, key, cellValue, sheetIndex);
		} else if (cellValue instanceof String) {
			writeString(colIndex, rowIndex, key, cellValue, sheetIndex);
		} else if (cellValue instanceof ArrayList) {
			String str = writeArray(colIndex, rowIndex, key,
					(ArrayList) cellValue, sheetIndex);
			writeString(colIndex, rowIndex, key, str, sheetIndex);
		} else if (cellValue instanceof HashMap) {
			int subIndex = writeHashMap(colIndex, rowIndex, key,
					(HashMap) cellValue, sheetIndex);
			if (subIndex >= 0) {
				// 写入主表引用
				Sheet sheet = book.getSheet(sheetIndex);
				if (sheet == null) {
					JOptionPane.showMessageDialog(null, "找不到index为"
							+ sheetIndex + "的Sheet!");
					return;
				}
			}
			writeNumber(colIndex, rowIndex, key, subIndex, sheetIndex);
		}
	}

	/**
	 * 此单元格对应的内容为一个对象, 需要写到另外一个Sheet中去
	 * */
	@SuppressWarnings("rawtypes")
	private int writeHashMap(int colIndex, int rowIndex, Object parentKey,
			HashMap cellValue, int parentSheetIndex) {
		int subSheetIndex = -1;
		Sheet subSheet = null;
		if (parentKey == null
				|| parentKey.toString().equals("") == true) {
			subSheetIndex = 0;
			subSheet = book.getSheet(0);
		} else if (parentKey instanceof Number) {
			subSheetIndex = 0;
			subSheet = book.getSheet(0);
		} else {
			subSheet = book.getSheet(parentKey.toString());
			if (subSheet != null) {
				subSheetIndex = sheets.indexOf(subSheet);
			}
		}
		if (subSheetIndex < 0 || subSheet == null) {
			JOptionPane.showMessageDialog(null,
					"找不到Sheet   name=" + parentKey.toString() + "   index="
							+ subSheetIndex);
			return -1;
		}
		Iterator keys = cellValue.keySet().iterator();
		int subColIndex = 0;
		int subRowIndex = subSheet.getRows() - (ExcelConst.CONTENT_START.y);
		// 子表索引
		writeString(subColIndex, subRowIndex, "", subRowIndex, subSheetIndex);
		// 子表一行数据
		while (keys.hasNext()) {
			Object key = keys.next();
			Object value = cellValue.get(key);
			if (value instanceof Number) {
				writeNumber(subColIndex, subRowIndex, key, value, subSheetIndex);
			} else if (value instanceof String) {
				writeString(subColIndex, subRowIndex, key, value, subSheetIndex);
			} else if (value instanceof ArrayList) {
				String str = writeArray(subColIndex, subRowIndex, key,
						(ArrayList) value, subSheetIndex);
				writeString(subColIndex, subRowIndex, key, str, subSheetIndex);
			} else if (value instanceof HashMap) {
				int subIndex = writeHashMap(subColIndex, subRowIndex, key, (HashMap) value,
						subSheetIndex);
				writeNumber(subColIndex, subRowIndex, key, subIndex, subSheetIndex);
				continue;
			}
			subColIndex++;
		}
		return subRowIndex;
	}

	/**
	 * 此单元格对应的内容为一个数组, 其中元素有可能是对象(如果是则需要写到另外Sheet中)
	 * */
	@SuppressWarnings("rawtypes")
	private String writeArray(int colIndex, int rowIndex, Object parentKey,
			ArrayList cellValue, int sheetIndex) {
		int subSheetIndex = -1;
		Sheet subSheet = null;
		if (parentKey.toString() == null
				|| parentKey.toString().equals("") == true) {
			subSheetIndex = 0;
		} else if (parentKey instanceof Number) {
			subSheetIndex = 0;
		} else {
			subSheet = book.getSheet(parentKey.toString());
			if (subSheet != null) {
				subSheetIndex = sheets.indexOf(subSheet);
			}
		}
		int len = cellValue.size();
		String str = "";
		if (len > 0) {
			// 元素类型必须相同
			Object value = cellValue.get(0);
			if (value instanceof Number) {
				for (int subColIndex = 0; subColIndex < len; subColIndex++) {
					value = cellValue.get(subColIndex);
					str += value + ",";
				}
			} else if (value instanceof String) {
				for (int subColIndex = 0; subColIndex < len; subColIndex++) {
					value = cellValue.get(subColIndex);
					str += value + ",";
				}
			} else if (value instanceof ArrayList) {
				str += "[";
				for (int subColIndex = 0; subColIndex < len; subColIndex++) {
					value = cellValue.get(subColIndex);
					String tempStr = writeArray(0, 0, subColIndex,
							(ArrayList) value, subSheetIndex);
					str += tempStr + "],[";
				}
				if (str.length() < 4) {
					str = ",";
				} else {
					str = str.substring(0, str.length() - 1);
				}
			} else if (value instanceof HashMap) {
				for (int subColIndex = 0; subColIndex < len; subColIndex++) {
					value = cellValue.get(subColIndex);
					int subRowIndex = writeHashMap(0, 0, parentKey,
							(HashMap) value, subSheetIndex);
					if (subRowIndex >= 0) {
						str += subRowIndex + ",";
					}
				}
			}
		}
		if (str.length() < 2) {
			str = "";
		} else {
			str = str.substring(0, str.length() - 1);
		}
		return str;
	}

	/**
	 * 此单元格对应内容为字符串, 可直接写
	 * */
	private void writeString(int colIndex, int rowIndex, Object key,
			Object value, int sheetIndex) {
		writeCell(colIndex, rowIndex, key.toString(), value.toString(),
				sheetIndex);
	}

	/**
	 * 此单元格对应内容为数字, 可直接写
	 * */
	private void writeNumber(int colIndex, int rowIndex, Object key,
			Object value, int sheetIndex) {
		writeCell(colIndex, rowIndex, key.toString(), value.toString(),
				sheetIndex);
	}

	/**
	 * 向某个单元格写入内容
	 * 
	 * @param int colIndex 列索引
	 * @param int rowIndex 行索引
	 * @param String
	 *            key 对应的字段类型
	 * @param String
	 *            value 内容
	 * @param int sheetIndex Sheet索引
	 * */
	private void writeCell(int colIndex, int rowIndex, String key,
			String value, int sheetIndex) {
		WritableSheet sheet = (WritableSheet) sheets.get(sheetIndex);
		if (key == null || key.equals("") == true) {
			colIndex = ExcelConst.CONTENT_START.x;
		} else {
			Cell keyCell = sheet.findLabelCell(key);
			if (keyCell != null) {
				colIndex = keyCell.getColumn();
			}
		}
		Label label = new Label(colIndex,
				ExcelConst.CONTENT_START.y + rowIndex, value);
		try {
			sheet.addCell(label);
		} catch (RowsExceededException e) {
			e.printStackTrace();
		} catch (WriteException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 创建excel文件
	 * */
	private void createFile(String name) {
		file = new File(name + ".xls");
		if (file.exists()) {
			file.delete();
		}
		try {
			file.createNewFile();
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, "创建 " + name + ".xls 失败!");
		}
	}

	/**
	 * 写入配置信息 复制Sheet1的配置说明到其他Sheet页
	 * */
	private void writeConfig(String jsonName, String jsonType) {
		for (int i = 0; i < sheets.size(); i++) {
			Sheet sheet = (Sheet) sheets.get(i);
			jsonName = i == 0 ? jsonName : "";
			int outFlag = i == 0 ? 1 : 0;
			jsonType = i == 0 ? jsonType : JSONConst.TYPE_OBJECT;
			try {
				Label label = new Label(ExcelConst.CLIENT_CONFIG_NAME.x,
						ExcelConst.CLIENT_CONFIG_NAME.y, jsonName);
				((WritableSheet) sheet).addCell(label);
				label = new Label(ExcelConst.CLIENT_OUT_FLAG.x,
						ExcelConst.CLIENT_OUT_FLAG.y, outFlag + "");
				((WritableSheet) sheet).addCell(label);
				label = new Label(ExcelConst.CLIENT_CONFIG_TYPE.x,
						ExcelConst.CLIENT_CONFIG_TYPE.y, jsonType);
				((WritableSheet) sheet).addCell(label);
				int maxCol = sheet.getColumns()
						- ExcelConst.CLIENT_PARAM_OUT_COL_INDEX;
				// 字段是否输出
				for (int c = 0; c < maxCol; c++) {
					Cell keyLabel = sheet.getCell(c + ExcelConst.CLIENT_PARAM_OUT_COL_INDEX, ExcelConst.CONTENT_START.y-1);
					if(keyLabel != null && ((keyLabel instanceof EmptyCell)==false || keyLabel.getType() != CellType.EMPTY))
					{
						label = new Label(
								c + ExcelConst.CLIENT_PARAM_OUT_COL_INDEX,
								ExcelConst.CLIENT_PARAM_OUT_ROW_INDEX, "1");
						((WritableSheet) sheet).addCell(label);
					}
				}

			} catch (RowsExceededException e) {
				e.printStackTrace();
			} catch (WriteException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 写入excel内容
	 * */
	@SuppressWarnings("unused")
	private void writeContent() {

	}

	public void initTemplateBook() {
		if (_templateBook == null) {
			try {
				_templateBook = Workbook.getWorkbook(new File(ConfigManager
						.excelTemplatePath()));
			} catch (BiffException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 返回容器中对应于key的value
	 * */
	@SuppressWarnings({ "unused", "rawtypes" })
	private Object getDataByKey(Object data, Object key) {
		if (data instanceof HashMap) {
			return ((HashMap) data).get(key);
		} else if (data instanceof ArrayList) {
			return ((ArrayList) data).get((int) key);
		}
		return null;
	}
}
