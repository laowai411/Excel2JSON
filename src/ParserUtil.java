import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

import parser.ExcelParser;
import parser.IParser;
import parser.JSONParser;
import parser.ListToJson;


public class ParserUtil 
{
	
	/**
	 * 解析器存储
	 * */
	private static HashMap<String, IParser> parserMap = new HashMap<>();
	
	/**
	 * 是否正在解析
	 * */
	private static boolean gIsparsing=false;
	
	/**
	 * 等待解析的文件列表
	 * */
	private static File[] waittingList;
	
	/**
	 * 剩余文件数量
	 * */
	private static int oddFileCount;
	
	public static void parse(File[] fileList)
	{
		if(gIsparsing == true)
		{
			return;
		}
		if(oddFileCount > 0)
		{
			final Timer timer = new Timer();
			//2s一次去检测剩余文件, 如果有剩余则继续解析,否则停止计时器
			timer.schedule(
				new TimerTask() 
				{
					@Override
					public void run() 
					{
						if(oddFileCount<1)
						{
							timer.cancel();
						}
						parseFile();
					}
				}, 2000
			);
		}
	}
	
	/**
	 * 解析文件
	 * */
	private static void parseFile()
	{
    	if(gIsparsing == true)
    	{
    		return;
    	}
    	if(oddFileCount>0)
    	{
    		File file = waittingList[oddFileCount-1];
    		IParser parser = get_parser(file);
    		gIsparsing = true;
    		Vector<Vector<Vector<String>>> valueList = parser.getSheetValueList(file);;
    		if(parser.get_type() == "excel")
    		{
    			//excel→json
    			ListToJson json = new ListToJson(valueList);
        		System.out.println(new Date().toGMTString()+"   "+oddFileCount);
    		}
    		else if(parser.get_type() == "json")
    		{
    			//json→excel
    		}
    	}
    	oddFileCount--;
    	gIsparsing = false;
	}
	
	 /**
     * 根据文件类型获取对应的解析器
     * */
    private static IParser get_parser(File file)
    {
    	IParser parser;
    	if(ExtensionConst.get_isExcel(file) == true)
    	{
    		parser = parserMap.get("excel");
    		if(parser == null)
    		{
    			parser = new ExcelParser();
    			parserMap.put("excel", parser);
    		}
    		return parser;
    	}
    	else if(ExtensionConst.get_isJSON(file) == true)
    	{
    		parser = parserMap.get("json");
    		if(parser == null)
    		{
    			parser = new JSONParser();
    			parserMap.put("json", parser);
    		}
    		return parser;
    	}
    	return null;
    }
}
