package parser;
import java.util.Vector;

public class ListToJson implements IListToFile
{
	
	/**
	 * 字段名
	 * */
	private Vector<String> keyList;
	
	/**
	 * 字段的数据类型列表
	 * */
	private Vector<String> keyTypeList;
	
	public ListToJson(Vector<Vector<Vector<String>>> valueList)
	{
		parse(valueList);
	}
	
	/**
	 * 将从Excel得到的列表数据转换成json
	 * */
	private void parse(Vector<Vector<Vector<String>>> valueList)
	{
		for(int i=0; i<valueList.size(); i++)
		{
			Vector<Vector<String>> sheetData = valueList.get(i);
			for(int colIndex=0; colIndex<sheetData.size(); colIndex++)
			{
				Vector<String> colList = sheetData.get(colIndex);
				for(int rowIndex=0; rowIndex<colList.size(); rowIndex++)
				{
					if(colIndex < 20 && colList.get(rowIndex) != "0" && colList.get(rowIndex) != "")
					{
//						System.out.println("sheetIndex: "+i+"  colIndex: "+colIndex+"    rowIndex: "+rowIndex +"  value: "+colList.get(rowIndex));
					}
				}
			}
		}
	}
	
	/**
	 * 从固定的行获取属性名
	 * */
	private void getKey(Vector<Vector<Vector<String>>> valueList)
	{
		
	}
	
	/**
	 * 从固定的行获取属性数据类型
	 * */
	private void getKeyType(Vector<Vector<Vector<String>>> valueList)
	{
		
	}
}
