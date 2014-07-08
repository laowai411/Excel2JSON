package common;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

import javax.swing.JTextArea;

public class LogUtil {
	
	private static FileWriter logWritter;
	
	private static FileWriter errWritter;
	
	private static JTextArea gTxtState;
	
	public static void registerStateTxt(JTextArea txt)
	{
		gTxtState = txt;
	}
	
	/**
	 * 记录log
	 * */
	@SuppressWarnings({ "deprecation" })
	public static void log(String str)
	{
		File file = new File(Global.getRunPath()+"etc/log.txt");
		if (file.exists() == false) {
			try {
				file.createNewFile();
			} catch (IOException ex) {
			}
		}
		try {
			if(logWritter == null)
			{
				logWritter = new FileWriter(file);
			}
			Date date = new Date();
			str = "["+date.getHours()+":"+date.getMinutes()+":"+date.getSeconds()+"]  "+str+"\n";
			logWritter.append(str);
			logWritter.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		gTxtState.setText(str);
	}
	
	@SuppressWarnings({ "deprecation" })
	public static void error(String str)
	{
		File file = new File(Global.getRunPath()+"etc/error.txt");
		if (file.exists() == false) {
			try {
				file.createNewFile();
			} catch (IOException ex) {
			}
		}
		try {
			if(errWritter == null)
			{
				errWritter = new FileWriter(file);
			}
			Date date = new Date();
			str = "["+date.getHours()+":"+date.getMinutes()+":"+date.getSeconds()+"]  "+str+"\n";
			gTxtState.setText(str);
			errWritter.append(str);
			errWritter.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		gTxtState.setText(str);
	}
}
