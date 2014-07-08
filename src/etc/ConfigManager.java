package etc;

import common.Global;



public class ConfigManager {

	public static String excelTemplatePath()
	{
		return Global.getRunPath()+"etc/template.xls";
	}
	
	public static String cmdOpenTemplateDir()
	{
		return "cmd /c start "+Global.getRunPath()+"etc/";
	}
}
