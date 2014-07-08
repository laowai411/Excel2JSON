package etc;



public class ConfigManager {

	public static String excelTemplatePath()
	{
		return ConfigManager.class.getResource("").getPath()+"\\template.xls";
	}
}
