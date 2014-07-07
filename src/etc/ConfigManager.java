package etc;

import java.net.URISyntaxException;
import java.net.URL;


public class ConfigManager {

	public static String excelTemplatePath()
	{
		return ConfigManager.class.getResource("").getPath()+"\\template.xls";
	}
	
	public static String jsonTemplatePath()
	{
		String url = ConfigManager.class.getResource("").getPath();
		url = url.substring(0, url.lastIndexOf("."));
		return url+"\\template.xls";
	}
	
	public static String readMePath()
	{
		String url = ConfigManager.class.getResource("").getPath();
		url = url.substring(0, url.lastIndexOf("."));
		return url+"\\template.xls";
	}
}
