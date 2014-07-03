package etc;


public class ConfigManager {

	public static String excelTemplatePath()
	{
		return ConfigManager.class.getResource("").getPath()+"\\template.xls";
	}
	
	public static String jsonTemplatePath()
	{
		return ConfigManager.class.getResource("").getPath()+"\\template.xls";
	}
	
	public static String readMePath()
	{
		return ConfigManager.class.getResource("").getPath()+"\\template.xls";
	}
}
