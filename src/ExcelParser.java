import java.io.File;
import java.io.IOException;
import java.util.Vector;
import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;


public class ExcelParser 
{
	
	/**
	 * 3ά����
	 * [
	 * 		sheetIndex:[
	 * 			colIndex[
	 * 				rowIndex string
	 * 				 ]
	 * 		 ]
	 *  ]
	 * */
	private static Vector<Vector<Vector<String>>> gSheetValueList;
	
	/**
	 * ���ڴ�����
	 * */
	private static boolean gIsParseing = false;
	
	public ExcelParser()
	{
		gSheetValueList = new Vector<>();
	}
	
	/**
	 * ��ʼת��excel����λ����
	 * */
	private static void parser(File file)
	{
		Workbook book = null;
		try {
			book = Workbook.getWorkbook(file);
		} catch (BiffException | IOException e) {
			e.printStackTrace();
			gIsParseing = false;
			return;
		}
		// ��õ�һ�����������
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
				//��sheet��ݲ���ȷ
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
		gIsParseing = false;
	}
	
	/**
	 * ��ȡExcelת�������ά���
	 * */
	public static Vector<Vector<Vector<String>>> getSheetValueList(File file)
	{
		gIsParseing = true;
		gSheetValueList = new Vector<>();
		parser(file);
		return gSheetValueList;
	}
	
	/**
	 * �Ƿ����ڴ���
	 * */
	public static boolean isParsing()
	{
		return gIsParseing;
	}
}
