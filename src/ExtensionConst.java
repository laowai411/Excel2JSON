
import java.io.File;


public class ExtensionConst 
{
	
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
		return filename.substring(filename.lastIndexOf("."));
	}
	
	/**
	 * 判断一个文件是否是Json文件
	 * */
	public static boolean get_isJSON(File file)
	{
		String extension = get_extension(file);
		if(extension == "json")
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
		if(extension == "xls" || extension == "xlsx" || extension == "xlsm")
		{
			return true;
		}
		return false;
	}
}
