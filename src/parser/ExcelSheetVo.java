package parser;

import java.util.ArrayList;

/**
 * 每个Sheet中的数据
 * */
public class ExcelSheetVo {
	
	/**
	 * Sheet行数
	 * */
	public int col;
	
	/**
	 * Sheet列数
	 * */
	public int row;
	
	/**
	 * Sheet单元格数据
	 * */
	@SuppressWarnings("rawtypes")
	public ArrayList sheetData;
	
	/**
	 * Sheet对应的要生成的json文件名
	 * */
	public String jsonName;
	
	/**
	 * 路径
	 * */
	public String path;
	
	/**
	 * Sheet名
	 * */
	public String sheetName;
	
	/**
	 * 生成Json文件的路径
	 * */
	public String getJSONPath()
	{
		return path+"\\"+jsonName;
	}
}
