package parser;
import java.io.File;
import java.io.IOException;
import java.security.cert.Extension;
import java.util.Vector;
import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;

public class ExcelParser implements IParser 
{
	
	/**
	 * 3维数组
	 * [
	 * 		sheetIndex:[
	 * 			colIndex[
	 * 				rowIndex string
	 * 				 ]
	 * 		 ]
	 *  ]
	 * */
	private Vector<Vector<Vector<String>>> gSheetValueList;

	public ExcelParser()
	{
		gSheetValueList = new Vector<>();
	}
	
	/**
	 * 解析Excel文件
	 * */
	public void parse(File file)
	{
		Workbook book = null;
		try {
			book = Workbook.getWorkbook(file);
		} catch (BiffException | IOException e) {
			e.printStackTrace();
			return;
		}
		
		Sheet[] sheets = book.getSheets();
		Sheet sheet = null;
		for(int i=0; i<sheets.length; i++)
		{
			try
			{
				sheet = book.getSheet(i);
			}
			catch(NullPointerException nullE)
			{
				//sheet数据不正确
//				nullE.printStackTrace();
				continue;
			}
			Vector<Vector<String>> valueList = new Vector<Vector<String>>();
			gSheetValueList.add(i, valueList);
			if(sheet != null)
			{
				int totalCol = sheet.getColumns();
				for(int colIndex=0; colIndex<totalCol; colIndex++)
				{
					Vector<String> cellList = new Vector<>();
					valueList.add(colIndex, cellList);
					int totalRow = sheet.getRows();
					for(int rowIndex=0; rowIndex<totalRow; rowIndex++)
					{
						if(sheet.getRow(rowIndex) != null && sheet.getColumn(colIndex) != null)
						{
							Cell c = sheet.getCell(colIndex, rowIndex);
							if(c != null)
							{
								if(c != null)
								{
									cellList.add(rowIndex, c.getContents());
								}
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
	 * */
	public Vector<Vector<Vector<String>>> getSheetValueList(File file)
	{
		gSheetValueList = new Vector<>();
		parse(file);
		return gSheetValueList;
	}

	public String get_type()
	{
		return "excel";
	}
	
}
