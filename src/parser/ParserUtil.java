package parser;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JOptionPane;

import common.ExtensionConst;
import common.Global;
import common.LogUtil;

import create.ExcelCreater;
import create.ICreater;
import create.JSONCreater;


public class ParserUtil {

	/**
	 * 解析器存储
	 * 
	 */
	private static HashMap<String, IParser> parserMap = new HashMap<>();
	/**
	 * listToFile存储
	 * 
	 */
	private static HashMap<String, ICreater> toFileList = new HashMap<>();
	/**
	 * 等待解析的文件列表
	 * 
	 */
	@SuppressWarnings("rawtypes")
	private static ArrayList waittingList;

	@SuppressWarnings("rawtypes")
	private static ArrayList delList;
	/**
	 * 剩余文件数量
	 * 
	 */
	private static int oddFileCount;

	@SuppressWarnings("unchecked")
	private static void getFileByType(File[] fileList, String type) {
		for (int i = 0; i < fileList.length; i++) {
			File file = fileList[i];
			if (file.isDirectory() == true
					&& file.getPath().indexOf("\\mmo\\map") == -1) {
				getFileByType(file.listFiles(), type);
			} else if (type == ExtensionConst.JSON_TYPE) {
				if (ExtensionConst.get_isJSON(file) == true) {
					waittingList.add(file);
				} else if (ExtensionConst.get_isExcel(file) == true) {
					// 删除无用的xls
					delList.add(file);
				}
			} else if (type == ExtensionConst.EXCEL_TYPE) {
				if (ExtensionConst.get_isExcel(file) == true) {
					waittingList.add(file);
				}
			}
		}
	}

	@SuppressWarnings("rawtypes")
	public static void parse(File[] fileList, String type) {
		waittingList = new ArrayList();
		delList = new ArrayList();
		getFileByType(fileList, type);
		delFile();
		parse();
	}

	private static void delFile() {
		for (int i = 0; i < delList.size(); i++) {
			File file = (File) delList.remove(delList.size() - 1);
			if (file.exists() == true) {
				file.delete();
			}
		}
	}

	public static void parse() {
		if (Global.isParsing == true) {
			return;
		}
		oddFileCount = waittingList == null ? 0 : waittingList.size();
		if (oddFileCount > 0) {
			final Timer timer = new Timer();
			// 2s一次去检测剩余文件, 如果有剩余则继续解析,否则停止计时器
			timer.schedule(new TimerTask() {
				@Override
				public void run() {
					if (oddFileCount < 1) {
						timer.cancel();
						JOptionPane.showMessageDialog(null, "转换结束!");
						Global.setWindowEnable(true);
						return;
					}
					parseFile();
				}
			}, 0, 500);
		}
	}

	/**
	 * 解析文件
	 */
	private static void parseFile() {
		if (Global.isParsing == true) {
			return;
		}
		File file = null;
		if (oddFileCount > 0) {
			file = (File) waittingList.get(oddFileCount - 1);
			IParser parser = get_parser(file);
			Global.isParsing = true;
			ICreater creater = get_toFile(file);
			creater.writeFile(parser, file);
		}
		oddFileCount--;
		LogUtil.log("剩余 " + oddFileCount
				+ (file != null ? ("   " + file.getName()) : ""));
		Global.isParsing = false;
	}

	/**
	 * 根据文件类型获取对应的解析器
	 * 
	 */
	private static IParser get_parser(File file) {
		IParser parser;
		if (ExtensionConst.get_isExcel(file) == true) {
			parser = parserMap.get(ExtensionConst.EXCEL_TYPE);
			if (parser == null) {
				parser = new ExcelParser();
				parserMap.put(ExtensionConst.EXCEL_TYPE, parser);
			}
			return parser;
		} else if (ExtensionConst.get_isJSON(file) == true) {
			parser = parserMap.get(ExtensionConst.JSON_TYPE);
			if (parser == null) {
				parser = new JSONParser();
				parserMap.put(ExtensionConst.JSON_TYPE, parser);
			}
			return parser;
		}
		return null;
	}

	/**
	 * 根据文件类型获取对应toFile工具 源文件为excel返回的是toJSON 源文件为json返回的是toExcel
	 * 
	 */
	private static ICreater get_toFile(File file) {
		ICreater creater;
		if (ExtensionConst.get_isExcel(file) == true) {
			// excel→json
			creater = toFileList.get(ExtensionConst.EXCEL_TYPE);
			if (creater == null) {
				creater = new JSONCreater();
				toFileList.put(ExtensionConst.EXCEL_TYPE, creater);
			}
			return creater;
		} else if (ExtensionConst.get_isJSON(file) == true) {
			// json→excel
			creater = toFileList.get(ExtensionConst.JSON_TYPE);
			if (creater == null) {
				creater = new ExcelCreater();
				toFileList.put(ExtensionConst.JSON_TYPE, creater);
			}
			return creater;
		}
		return null;
	}
}
