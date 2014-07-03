package parser;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;

import common.LogUtil;

public class ExcelParser implements IParser {

	/**
	 * 3维数组 [ sheetIndex:[ colIndex[ rowIndex string ] ] ]
	 * 
	 */
	@SuppressWarnings("rawtypes")
	private ArrayList gSheetValueList;
	/**
	 * 列数
	 * */
	private int col;

	/**
	 * 行数
	 * */
	private int row;

	/**
	 * sheet的数据
	 * */
	@SuppressWarnings("rawtypes")
	private HashMap data;

	public ExcelParser() {
		gSheetValueList = new ArrayList<>();
	}

	/**
	 * 解析Excel文件
	 * 
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void parse(File file) {
		Workbook book = null;
		try {
			book = Workbook.getWorkbook(file);
		} catch (BiffException | IOException e) {
			// e.printStackTrace();
			return;
		}

		Sheet sheet = null;
		//只把第一个sheet解析
		for (int i = 0; i < 1; i++) {
			try {
				sheet = book.getSheet(i);
			} catch (NullPointerException nullE) {
				// sheet数据不正确
				LogUtil.error(file.getName().substring(0, file.getName().lastIndexOf("."))+"  sheet数据不正确!!");
				return;
			}
			
			ArrayList valueList = new ArrayList<>();
			gSheetValueList.add(i, valueList);
			if (sheet != null) {
				file.getName().substring(0, file.getName().lastIndexOf("."));
				col = sheet.getColumns();
				for (int colIndex = 0; colIndex < col; colIndex++) {
					ArrayList cellList = new ArrayList<>();
					valueList.add(colIndex, cellList);
					row = sheet.getRows();
					for (int rowIndex = 0; rowIndex < row; rowIndex++) {
						if (sheet.getRow(rowIndex) != null
								&& sheet.getColumn(colIndex) != null) {
							Cell c = sheet.getCell(colIndex, rowIndex);
							if (c != null) {
								cellList.add(rowIndex, c);
							}
						}
					}
				}
				book.close();
			}
		}
	}

	/**
	 * 获取Excel的格子数据列表,返回的是一个三维数组
	 * 
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public HashMap getData(File file) {
		if(gSheetValueList != null)
		{
			gSheetValueList.clear();
		}
		else
		{
			gSheetValueList = new ArrayList<>();
		}
		parse(file);
		data = new HashMap();
		String fileName = file.getName();
		fileName = fileName.substring(0, fileName.lastIndexOf("."));
		data.put("name", file.getParent() + "\\" + fileName);
		data.put("data", gSheetValueList);
		data.put("col", col);
		data.put("row", row);
		return data;
	}

	@Override
	public String get_type() {
		return "excel";
	}
}
