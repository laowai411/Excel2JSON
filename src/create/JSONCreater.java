package create;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import parser.ExcelSheetVo;
import parser.IParser;

import common.ExcelConst;
import common.JSONConst;


/**
 * JSON文件生成器
 * */
public class JSONCreater implements ICreater {

	@SuppressWarnings("rawtypes")
	private HashMap excelData;
	
	@SuppressWarnings({ "rawtypes", "unused" })
	@Override
	public void writeFile(IParser parser, File srcFile) {
		HashMap data = parser.getData(srcFile);
		excelData = (HashMap) data.get("excelData");
		String tipExcelName = (String) data.get("excelName");
		
		Iterator sheetIterator = excelData.keySet().iterator();
		while(sheetIterator.hasNext())
		{
			String excelName = sheetIterator.next().toString();
			ArrayList sheetList = (ArrayList) excelData.get(excelName);
			int len = sheetList.size();
			for(int i=0; i<len; i++)
			{
				ExcelSheetVo sheet = (ExcelSheetVo) sheetList.get(i);
				if(sheet != null && sheet.jsonName != null)
				{
					createJSONFile(sheet);
				}
			}
		}
	}

	/**
	 * 创建json文件
	 * .创建json文件(路径+jsonName)
	 * .获取json拼接后的字符串
	 * .写入文件
	 * */
	private void createJSONFile(ExcelSheetVo sheet) {
		File file = new File(sheet.getJSONPath());
		if(file.exists() == true)
		{
			file.delete();
		}
		try {
			file.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			FileWriter writer = new FileWriter(file);
			writer.write(getJSONStr(sheet));
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	
	/**
	 * 拼接一个json文件需要的字符串
	 * .字符串和数字直接取出
	 * .数组需要检测其中是否是对象,如果是则循环递进,关联子表索引替代
	 * .对象则用关联子表的索引替代
	 * */
	private String getJSONStr(ExcelSheetVo sheet)
	{
		String str = "{";
		str += getContentStr(sheet);
		str = str.substring(0, str.length()-1)+"\n}";
		return str;
	}
	
	@SuppressWarnings("rawtypes")
	private String getContentStr(ExcelSheetVo sheet)
	{
		String str = "\n\t";
		for(int rowIndex=ExcelConst.CONTENT_START.y; rowIndex<sheet.row; rowIndex++)
		{
			String tempStr = getRowStr(sheet, rowIndex, JSONConst.TYPE_OBJECT);
			
			if(tempStr.equals("")==true)
			{
				continue;
			}
			String id = (String) ((ArrayList)sheet.sheetData.get(ExcelConst.CONTENT_START.x)).get(rowIndex);
			if(id.equals("")==false)
			{
				id = "\""+id+"\":{";
			}
			str = str.concat("\n\t"+id+tempStr.substring(0, tempStr.length()-1)).concat("},");
		}
		str = str.substring(0, str.length()-1).concat("\n}");
		return str;
	}
	
	@SuppressWarnings("rawtypes")
	private String getRowStr(ExcelSheetVo sheet, int rowIndex, String type)
	{
		String str = "";
		for(int colIndex=ExcelConst.CONTENT_START.x; colIndex<sheet.col; colIndex++)
		{
			String outFlag = (String) ((ArrayList)sheet.sheetData.get(colIndex)).get(ExcelConst.CLIENT_PARAM_OUT_ROW_INDEX);
			if(outFlag.equals("0")==true)
			{
				continue;
			}
			ArrayList colList = (ArrayList) sheet.sheetData.get(colIndex);
			Object cellValue = colList.get(rowIndex);
			if(cellValue == null || cellValue.toString().equals("")==true)
			{
				continue;
			}
			String cellStr = cellValue.toString();
			String attKeyType = (String) colList.get(ExcelConst.ATT_KEY_TYPE_INDEX);
			String attKey = (String) colList.get(ExcelConst.ATT_KEY_INDEX);
			if(attKeyType.equals(JSONConst.TYPE_ARRAY)==true)
			{
				str += replaceArray(sheet, colIndex, rowIndex)+",";
			}
			else if(attKeyType.equals(JSONConst.TYPE_OBJECT)==true)
			{
				str += reaplaceObject(sheet, colIndex, rowIndex)+",";
			}
			else if(attKeyType.equals(JSONConst.TYPE_NUMER)==true || attKeyType.equals(JSONConst.TYPE_STRING))
			{
				str += "\""+attKey+"\":"+cellStr+",";
			}
			else if(attKey.equals("")==true)
			{
				//不处理
				System.out.println("不处理  keyType "+attKeyType+"  key "+attKey);
			}
		}
		if(str.endsWith(",")==true)
		{
//			if(type.equals(JSONConst.TYPE_ARRAY)==true)
//			{
//				str = str.substring(0, str.length()-1).concat("]");
//			}
//			else if(type.equals(JSONConst.TYPE_OBJECT)==true)
//			{
//				str = str.substring(0, str.length()-1).concat("}");
//			}
			str = str.substring(0, str.length()-1);
		}
		return str;
	}
	
	/**
	 * 字段为Object类型则找到其引用数据替换
	 * */
	@SuppressWarnings("rawtypes")
	private String reaplaceObject(ExcelSheetVo sheet, int colIndex, int rowIndex)
	{
		ArrayList colList = (ArrayList) sheet.sheetData.get(colIndex);
		String value = (String) ((ArrayList)sheet.sheetData.get(colIndex)).get(rowIndex);
		String linkExcelName = (String) colList.get(ExcelConst.LINK_EXCEL_ROW_INDEX);
		String str = "";
		String linkExcelIndex = (String) colList.get(ExcelConst.LINK_SHEET_ROW_INDEX);
		if(excelData.get(linkExcelName) != null && ((ArrayList)excelData.get(linkExcelName)).size()>0)
		{
			ExcelSheetVo linkSheet = (ExcelSheetVo) ((ArrayList)excelData.get(linkExcelName)).get(Integer.parseInt(linkExcelIndex));
			if(linkSheet != null)
			{
				String type =  (String) ((ArrayList)sheet.sheetData.get(colIndex)).get(ExcelConst.ATT_KEY_TYPE_INDEX);
				str +="\""+linkSheet.sheetName+"\":{";
				for(int rowInd=ExcelConst.CONTENT_START.y; rowInd<linkSheet.row; rowInd++)
				{
					if(((ArrayList)linkSheet.sheetData.get(ExcelConst.CONTENT_START.x)).get(rowInd).equals(value) == true)
					{
						for(int colInd=ExcelConst.CONTENT_START.x; colInd<linkSheet.col; colInd++)
						{
							if(((ArrayList)linkSheet.sheetData.get(colInd)).get(rowInd).equals(value)==true)
							{
								str += getRowStr(linkSheet, rowInd, type)+",";
							}
						}
					}
				}
			}
			if(str.equals("")==false)
			{
				str = str.substring(0, str.length()-1).concat("}");
			}
		}
		return str;
	}
	
	/**
	 * 字段为Array类型则找到其引用数据替换
	 * */
	@SuppressWarnings("rawtypes")
	private String replaceArray(ExcelSheetVo sheet, int colIndex, int rowIndex)
	{
		ArrayList colList = (ArrayList) sheet.sheetData.get(colIndex);
		String value = (String) ((ArrayList)sheet.sheetData.get(colIndex)).get(rowIndex);
		String linkExcelName = (String) colList.get(ExcelConst.LINK_EXCEL_ROW_INDEX);
		String str = "";
		String linkExcelIndex = (String) colList.get(ExcelConst.LINK_SHEET_ROW_INDEX);
		if(excelData.get(linkExcelName) != null && ((ArrayList)excelData.get(linkExcelName)).size()>0)
		{
			ExcelSheetVo linkSheet = (ExcelSheetVo) ((ArrayList)excelData.get(linkExcelName)).get(Integer.parseInt(linkExcelIndex));
			if(linkSheet != null)
			{
				if(value.equals("e"))
				{
					System.out.println(value);
				}
				String type =  (String) ((ArrayList)sheet.sheetData.get(colIndex)).get(ExcelConst.ATT_KEY_TYPE_INDEX);
				str +="\""+linkSheet.sheetName+"\":[";
				for(int rowInd=ExcelConst.CONTENT_START.y; rowInd<linkSheet.row; rowInd++)
				{
					if(((ArrayList)linkSheet.sheetData.get(ExcelConst.CONTENT_START.x)).get(rowInd).equals(value) == true)
					{
						for(int colInd=ExcelConst.CONTENT_START.x; colInd<linkSheet.col; colInd++)
						{
							if(((ArrayList)linkSheet.sheetData.get(colInd)).get(rowInd).equals(value)==true)
							{
								str += getRowStr(linkSheet, rowInd, type)+",";
							}
						}
					}
				}
			}
			if(str.equals("")==false)
			{
				str = str.substring(0, str.length()-1).concat("]");
			}
		}
		return str;
	}
	
}
