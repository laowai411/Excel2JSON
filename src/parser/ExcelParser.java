package parser;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;
import common.ExcelConst;
import common.ExtensionConst;
import common.LogUtil;

public class ExcelParser implements IParser {
	
	/**
	 * 当前要生成json的Excel文件
	 * */
	private File gExcel;

	/**
	 * 用于转换json的数据
	 * */
	@SuppressWarnings("rawtypes")
	private HashMap data = new HashMap();
	
	/**
	 * 解析Excel得到的数据,包含了关联表的数据
	 * */
	@SuppressWarnings({ "rawtypes" })
	private HashMap gExcelData;
	
	@SuppressWarnings("rawtypes")
	public ExcelParser() {
		gExcelData = new HashMap();
	}

	/**
	 * 解析Excel文件
	 * 
	 */
	public void parse(File file) {
		gExcel = file;
		parseExcel(file);
	}
	
	/**
	 * 解析一个Excel文件, 及其关联的Excel
	 * */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void parseExcel(File file)
	{
		Workbook book = null;
		try {
			book = Workbook.getWorkbook(file);
		} catch (BiffException e) {
			return;
		}catch(IOException e)
		{
			return;
		}

		Sheet sheet = null;
		ArrayList sheetList = new ArrayList();
		for (int i = 0; i < book.getNumberOfSheets(); i++) {
			try {
				sheet = book.getSheet(i);
			} catch (NullPointerException nullE) {
				// sheet数据不正确
				LogUtil.error(file.getName().substring(0, file.getName().lastIndexOf("."))+"  sheet数据不正确!!");
				continue;
			}
			
			ExcelSheetVo sheetVo = new ExcelSheetVo();
			sheetVo.sheetData = new ArrayList();
			sheetVo.path = file.getParent();
			sheetList.add(i, sheetVo);
			sheetVo.sheetName = sheet.getName();
			parseSheet(sheet, sheetVo);
		}
		gExcelData.put(file.getName(), sheetList);
		book.close();
	}
	
	/**
	 * 解析一个Sheet
	 * */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void parseSheet(Sheet sheet, ExcelSheetVo sheetVo)
	{
		if (sheet != null) {
			parseLinkExcel(sheet);
			int col = sheet.getColumns();
			sheetVo.col = col;
			for (int colIndex = 0; colIndex < col; colIndex++) {
				ArrayList cellList = new ArrayList();
				sheetVo.sheetData.add(colIndex, cellList);
				int row = sheet.getRows();
				sheetVo.row = sheetVo.row>row?sheetVo.row:row;
				for (int rowIndex = 0; rowIndex < row; rowIndex++) {
					if (sheet.getRow(rowIndex) != null
							&& sheet.getColumn(colIndex) != null) {
						Cell cell = sheet.getCell(colIndex, rowIndex);
						if (cell != null) {
							if(colIndex == ExcelConst.CLIENT_OUT_FLAG.x && rowIndex == ExcelConst.CLIENT_OUT_FLAG.y)
							{
								if(cell.getContents() != null && cell.getContents().equals("1")==true)
								{
									cell = sheet.getCell(ExcelConst.CLIENT_CONFIG_NAME.x, ExcelConst.CLIENT_CONFIG_NAME.y);
									if(cell.getContents() !=null && cell.getContents().equals("")==false)
									{
										sheetVo.jsonName = cell.getContents();
									}
								}
							}
							cellList.add(rowIndex, cell.getContents());
						}
					}
				}
			}
		}
	}
	
	/**
	 * 解析关联的excel
	 * */
	private void parseLinkExcel(Sheet sheet)
	{
		for(int i=ExcelConst.LINK_EXCEL_COL_INDEX; i<sheet.getColumns(); i++)
		{
			Cell cell = sheet.getCell(i, ExcelConst.LINK_EXCEL_ROW_INDEX);
			if(cell != null && cell.getContents()!=null && cell.getContents().equals("")==false)
			{
				String excelName = cell.getContents();
				if(excelName.equals(gExcel.getName())==false)
				{
					cell = sheet.getCell(i, ExcelConst.LINK_EXCEL_ROW_INDEX);
					if(cell != null && cell.getContents()!=null && cell.getContents().equals("")==false)
					{
						File file = new File(gExcel.getAbsolutePath()+excelName);
						if(file.exists()==true && ExtensionConst.get_isExcel(file)==true)
						{
							parseExcel(file);
						}
					}
				}
			}
		}
	}

	/**
	 * 获取Excel的格子数据列表,返回的是一个三维数组
	 * 
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public HashMap getData(File file) {
		gExcelData.clear();
		data.clear();
		parse(file);
		String fileName = file.getName();
		fileName = fileName.substring(0, fileName.lastIndexOf("."));
		data.put("excelData", gExcelData);
		data.put("excelName", file.getName());
		file = null;
		return data;
	}

	@Override
	public String get_type() {
		return "excel";
	}
}
