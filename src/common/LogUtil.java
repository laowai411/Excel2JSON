package common;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.JTextField;

public class LogUtil {

	
	private static FileWriter writer;
	
	private static JTextField logTxt;
	
	public static void registerLogTxt(JTextField txt)
	{
		logTxt = txt;
	}
	
	@SuppressWarnings("deprecation")
	public static void log(String str)
	{
		if(writer == null)
		{
			File file = new File(System.getProperty("user.dir")+"\\log.txt");
			try {
				if(file.exists() == false && file.createNewFile() == false)
				{
					System.out.println("创建log文件失败!");
					return;
				}
				writer = new FileWriter(file);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		try {
			java.util.Date date = new java.util.Date();
			writer.append("["+date.getHours()+":"+date.getMinutes()+":"+date.getSeconds()+"] "+str+"\n");
			logTxt.setText("["+date.getHours()+":"+date.getMinutes()+":"+date.getSeconds()+"] "+str+"\n");
			writer.flush();
			System.out.println("["+date.getHours()+":"+date.getMinutes()+":"+date.getSeconds()+"] "+str+"\n");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
