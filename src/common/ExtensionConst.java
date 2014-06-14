package common;

import java.io.File;


public class ExtensionConst 
{
	
        public static final String EXCEL_TYPE = "excel";
        
        public static final String JSON_TYPE = "json";
    
	/**
	 * 获取文件扩展名
	 * */
	public static String get_extension(File file)
	{
		if(file == null)
		{
			return "";			
		}
		String filename = file.getName();
		return filename.substring(filename.lastIndexOf(".")+1);
	}
	
	/**
	 * 判断一个文件是否是Json文件
	 * */
	public static boolean get_isJSON(File file)
	{
		String extension = get_extension(file);
		if(extension.equals(JSON_TYPE) == true)
		{
			return true;
		}
		return false;
	}
	
	/**
	 * 判断一个文件是否是excel文件
	 * */
	public static boolean get_isExcel(File file)
	{
		String extension = get_extension(file);
		if(extension.equals("xls")==true || extension.equals("xlsx")==true || extension.equals("xlsm")==true)
		{
			return true;
		}
		return false;
	}
}
