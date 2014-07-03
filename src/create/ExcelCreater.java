package create;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import javax.swing.JOptionPane;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
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
	 * sheet索引
	 * */
	@SuppressWarnings("unused")
	private int sheetIndex = 0;
	
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
	
	private Workbook _templateBook = null;
	
	@SuppressWarnings("rawtypes")
	public void writeFile(IParser parser, File srcFile) {
		initTemplateBook();
		HashMap data = parser.getData(srcFile);
		if(data == null)
		{
			return;
		}
		sheetIndex = 0;
		sheets = new ArrayList<>();
		parseData(data);
	}

	/**
	 * 取出json数据中有用的部分
	 * */
	@SuppressWarnings({ "rawtypes", "unused", "unchecked" })
	private void parseData(HashMap data)
	{
		int row = (int) data.get("row");
		int col = (int) data.get("col");
		String name = (String) data.get("name");
		attKeyMap = (HashMap) data.get("cols");
		ArrayList indexList = (ArrayList) data.get("rows");
		Object sheetData = data.get("data");
		createFile(name);
		try {
			book = Workbook.createWorkbook(file, _templateBook);
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, name+"创建写入进程失败!");
			return;
		}catch(Exception e1)
		{
			JOptionPane.showMessageDialog(null, "文件正在被其他程序使用!");
			return;
		}
		try {
			sheets = new ArrayList<>();
			book.copySheet(0, "template", 1);
			WritableSheet sheet = book.getSheet(0);
			sheets.add(0, sheet);
			sheet.setName((String) data.get("sheetName"));
			//主Sheet属性字段单元格写入
			writeHead(0, attKeyMap);
			if(sheetData instanceof HashMap)
			{
				//写入配置信息(是否输出,配置表名...等等)
				writeConfig((String) data.get("sheetName"), true, JSONConst.TYPE_OBJECT, 0);
				writeByHashMap((HashMap) sheetData);
			}
			else if(sheetData instanceof ArrayList)
			{
				//写入配置信息(是否输出,配置表名...等等)
				writeConfig((String) data.get("sheetName"), true, JSONConst.TYPE_OBJECT, 0);
				writeByArray((ArrayList) sheetData);
			}
			//删除模版Sheet页
			book.removeSheet(book.getNumberOfSheets()-1);
			book.write();
			book.close();
			file = null;
		} catch (IOException | WriteException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 写某一分页的头信息
	 * */
	@SuppressWarnings("rawtypes")
	private void writeHead(int sheetIndex, HashMap attKeyMap) {
		Sheet s = book.getSheet(sheetIndex);
		Iterator keys = attKeyMap.keySet().iterator();
		int keyIndex = 0;
		while(keys.hasNext())
		{
			//写入字段名
			String keyName = (String) keys.next();
			Cell label = s.getCell(keyIndex+ExcelConst.CONTENT_START.x+1 , ExcelConst.HEAD_END_ROW_INDEX);
			if(label == null)
			{
				label = new Label(keyIndex+ExcelConst.CONTENT_START.x+1, ExcelConst.HEAD_END_ROW_INDEX, keyName.toString());
			}
			else
			{
				Label tempLabel = new Label(keyIndex+ExcelConst.CONTENT_START.x+1, ExcelConst.HEAD_END_ROW_INDEX, keyName.toString());
				if(label.getCellFeatures() != null)
				{
					tempLabel.setCellFeatures((WritableCellFeatures) label.getCellFeatures());
				}
				if(label.getCellFormat() != null)
				{
					tempLabel.setCellFormat(label.getCellFormat());
				}
				label = tempLabel;
			}
			try {
				//字段数据类型
				KeyVo keyVo = (KeyVo) attKeyMap.get(keyName);
				Label keyTypeLabel = new Label(label.getColumn(), label.getRow()-1, keyVo.keyType);
				((WritableSheet) s).addCell(keyTypeLabel);
				//字段名
				((WritableSheet) s).addCell((WritableCell) label);
			} catch (RowsExceededException e) {
				e.printStackTrace();
			} catch (WriteException e) {
				e.printStackTrace();
			}
			keyIndex++;
			keys.remove();
		}
	}

	/**
	 * json数据是对象
	 * */
	@SuppressWarnings("rawtypes")
	private void writeByHashMap(HashMap map)
	{
		Sheet s = book.getSheet(0);
		//主键类型为String
		Label label = new Label(ExcelConst.CONTENT_START.x, ExcelConst.CONTENT_START.y-2, JSONConst.TYPE_STRING);
		try {
			((WritableSheet) s).addCell(label);
		} catch (RowsExceededException e) {
			e.printStackTrace();
		} catch (WriteException e) {
			e.printStackTrace();
		}
		Iterator rowList = map.keySet().iterator();
		int rowIndex = 0;
		while(rowList.hasNext())
		{
			Object index = rowList.next();
			Object rowItem = map.get(index);
			writeString(ExcelConst.CONTENT_START.x, rowIndex, "", index.toString(), 0);
			writeRow(rowIndex, rowItem, 0);
			rowList.remove();
			rowIndex++;
		}
	}
	
	/**
	 * json数据是数组
	 * */
	@SuppressWarnings("rawtypes")
	private void writeByArray(ArrayList list)
	{
		Sheet s = book.getSheet(0);
		//主键类型为Number
		Label label = new Label(ExcelConst.CONTENT_START.x, ExcelConst.CONTENT_START.y-2, JSONConst.TYPE_NUMER);
		try {
			((WritableSheet) s).addCell(label);
		} catch (RowsExceededException e) {
			e.printStackTrace();
		} catch (WriteException e) {
			e.printStackTrace();
		}
		int len = list.size();
		for(int index=0; index<len; index++)
		{
			writeNumber(ExcelConst.CONTENT_START.x, index, "", index, 0);
			writeRow(index, list.get(index), 0);
		}
	}
	
	/**
	 * 写入一行
	 * @param rowIndex 行索引
	 * @param rowData 一行的数据
	 * */
	@SuppressWarnings("rawtypes")
	private void writeRow(int rowIndex, Object rowData, int sheetIndex) {
		if(rowData instanceof Number)
		{
			writeNumber(0, rowIndex, "", rowData, sheetIndex);
		}
		else if(rowData instanceof String)
		{
			writeString(0, rowIndex, "", rowData, sheetIndex);
		}
		else if(rowData instanceof ArrayList)
		{
			ArrayList list = (ArrayList) rowData;
			int len = list.size();
			for(int i=0; i<len; i++)
			{
				writeRow2(i, rowIndex, i, list.get(i), sheetIndex);
			}
		}
		else if(rowData instanceof HashMap)
		{
			HashMap map = (HashMap) rowData;
			Iterator keys = map.keySet().iterator();
			int colIndex = 0;
			while(keys.hasNext())
			{
				Object key = keys.next();
				Object cellValue = map.get(key);
				writeRow2(colIndex, rowIndex, key, cellValue, sheetIndex);
				keys.remove();
				colIndex++;
			}
		}
	}
	
	@SuppressWarnings("rawtypes")
	private void writeRow2(int colIndex, int rowIndex, Object key, Object cellValue, int sheetIndex)
	{
		if(cellValue instanceof Number)
		{
			writeNumber(colIndex, rowIndex, key, cellValue, sheetIndex);
		}
		else if(cellValue instanceof String)
		{
			writeString(colIndex, rowIndex, key, cellValue, sheetIndex);
		}
		else if(cellValue instanceof ArrayList)
		{
			writeArray(colIndex, rowIndex, key, (ArrayList)cellValue, sheetIndex);
		}
		else if(cellValue instanceof HashMap)
		{
			writeHashMap(colIndex, rowIndex, key, (HashMap)cellValue, sheetIndex);
		}
	}

	/**
	 * 此单元格对应的内容为一个对象, 需要写到另外一个Sheet中去
	 * */
	@SuppressWarnings("rawtypes")
	private void writeHashMap(int colIndex, int rowIndex, Object parentKey, HashMap cellValue, int sheetIndex)
	{
		checkAndUpdateSubSheet(parentKey, cellValue);
		Iterator keys = cellValue.keySet().iterator();
		while(keys.hasNext())
		{
			Object key = keys.next();
			Object value = cellValue.get(key);
			if(value instanceof Number)
			{
				
			}
			else if(value instanceof String)
			{
				
			}
			else if(value instanceof ArrayList)
			{
				
			}
			else if(value instanceof HashMap)
			{
				
			}
		}
	}
	
	/**
	 * 此单元格对应的内容为一个数组, 其中元素有可能是对象(如果是则需要写到另外Sheet中)
	 * */
	private void writeArray(int colIndex, int rowIndex, Object key, ArrayList cellValue, int sheetIndex)
	{
		
	}
	
	/**
	 * 是否有对应的sheet存在,如果没有,则创建,有并且属性不全,则将新属性加入
	 * */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void checkAndUpdateSubSheet(Object parentKey, Object value)
	{
		Sheet sheet = book.getSheet(parentKey.toString());
		if(sheet == null)
		{
			//复制模版Sheet到倒数第二页, JSON导出Excel只生成一个主表
			if(parentKey != null && parentKey.equals("")==false)
			{
				book.copySheet(book.getNumberOfSheets()-1, parentKey.toString(), book.getNumberOfSheets()-1);
			}
			else
			{
				book.copySheet(book.getNumberOfSheets()-1, "Sheet"+book.getNumberOfSheets(), book.getNumberOfSheets()-1);
			}
			sheet = book.getSheet(book.getNumberOfSheets()-1);
			sheets.add(sheet);
		}
		if(value instanceof HashMap)
		{
			HashMap map = (HashMap) value;
			Iterator keys = map.keySet().iterator();
			writeHead(book.getNumberOfSheets()-2, map);
		}
//		else if(value instanceof ArrayList)
//		{
//			
//		}
//		int col = sheet.getColumns();
//		for(int i=0; i<col; i++)
//		{
//			Cell label = sheet.getCell(ExcelConst.CONTENT_START.x, ExcelConst.CONTENT_START.y-1);
//			if(label == null)
//			{
//				label = new Label(ExcelConst.CONTENT_START.x + i, ExcelConst.CONTENT_START.y, )
//			}
//		}
	}
	
	/**
	 * 此单元格对应内容为字符串, 可直接写
	 * */
	private void writeString(int colIndex, int rowIndex, Object key, Object value, int sheetIndex)
	{
		writeCell(colIndex, rowIndex, key.toString(), value.toString(), sheetIndex);
	}
	
	/**
	 * 此单元格对应内容为数字, 可直接写
	 * */
	private void writeNumber(int colIndex, int rowIndex, Object key, Object value, int sheetIndex)
	{
		writeCell(colIndex, rowIndex, key.toString(), value.toString(), sheetIndex);
	}
	
	private void writeCell(int colIndex, int rowIndex, String key, String value, int sheetIndex)
	{
		WritableSheet sheet = (WritableSheet) sheets.get(sheetIndex);
		if(key == null || key.equals("")==true)
		{
			colIndex = ExcelConst.CONTENT_START.x;
		}else{
			Cell keyCell = sheet.findLabelCell(key);
			if(keyCell != null)
			{
				colIndex = keyCell.getColumn();
			}
		}
		Label label = new Label(colIndex, ExcelConst.CONTENT_START.y+rowIndex, value);
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
	private void createFile(String name)
	{
		file = new File(name+".xls");
		if(file.exists())
		{
			file.delete();
		}
		try {
			file.createNewFile();
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, "创建 "+name+".xls 失败!");
		}
	}
	/**
	 * 写入配置信息
	 * 复制Sheet1的配置说明到其他Sheet页
	 * */
	@SuppressWarnings("unused")
	private void writeConfig(String jsonName, Boolean outFlag, String jsonType, int sheetIndex)
	{
		Sheet sheet = (Sheet) sheets.get(sheetIndex);
		Label label = new Label(ExcelConst.CLIENT_CONFIG_NAME.x, ExcelConst.CLIENT_CONFIG_NAME.y, jsonName);
		try {
			((WritableSheet) sheet).addCell(label);
			label = new Label(ExcelConst.CLIENT_OUT_FLAG.x, ExcelConst.CLIENT_OUT_FLAG.y, (outFlag==true?1:0)+"");
			((WritableSheet) sheet).addCell(label);
			label = new Label(ExcelConst.CLIENT_CONFIG_TYPE.x, ExcelConst.CLIENT_CONFIG_TYPE.y, jsonType);
			((WritableSheet) sheet).addCell(label);
		} catch (RowsExceededException e) {
			e.printStackTrace();
		} catch (WriteException e) {
			e.printStackTrace();
		}
		
	}
	/**
	 * 写入excel内容
	 * */
	@SuppressWarnings("unused")
	private void writeContent()
	{
		
	}
	
	public void initTemplateBook()
	{
		if(_templateBook == null)
		{
			try {
				_templateBook = Workbook.getWorkbook(new File(ConfigManager.excelTemplatePath()));
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
	private Object getDataByKey(Object data, Object key)
	{
		if(data instanceof HashMap)
		{
			return ((HashMap) data).get(key);
		}
		else if(data instanceof ArrayList)
		{
			return ((ArrayList) data).get((int) key);
		}
		return null;
	}
}
