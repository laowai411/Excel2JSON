package common;

import java.awt.Point;

/**
 * Excel相关常量定义
 * */
public class ExcelConst {
	
	/**
	 * 前端配置表名所在格子的索引(列索引, 行索引)
	 * */
	public static final Point CLIENT_CONFIG_NAME = new Point(1, 0);
	/**
	 * 后端配置表名所在格子的索引(列索引, 行索引)
	 * */
	public static final Point SERVER_CONFIG_NAME = new Point(1, 1);
	/**
	 * 前端配是否输出所在格子的索引(列索引, 行索引)
	 * */
	public static final Point CLIENT_OUT_FLAG = new Point(3, 0);
	/**
	 * 后端是否输出所在格子的索引(列索引, 行索引)
	 * */
	public static final Point SERVER_OUT_FLAG = new Point(3, 1);
	/**
	 * 前端表结构(Array或者Object)(列索引, 行索引)
	 * */
	public static final Point CLIENT_CONFIG_TYPE = new Point(5, 0);
	/**
	 * 后端表结构(Array或者Object)(列索引, 行索引)
	 * */
	public static final Point SERVER_CONFIG_TYPE = new Point(5, 1);
	/**
	 * 字段是否给前端输出列索引
	 * */
	public static final int CLIENT_PARAM_OUT_COL_INDEX = 1;
	/**
	 * 字段是否给前端输出行索引
	 * */
	public static final int CLIENT_PARAM_OUT_ROW_INDEX = 5;
	/**
	 * 字段是否给后端输出列索引
	 * */
	public static final int SERVER_PARAM_OUT_COL_INDEX = 1;
	/**
	 * 字段是否给后端输出行索引
	 * */
	public static final int SERVER_PARAM_OUT_ROW_INDEX = 6;
	/**
	 * 关联excel列索引
	 * */
	public static final int LINK_EXCEL_COL_INDEX = 1;
	/**
	 * 关联excel行索引
	 * */
	public static final int LINK_EXCEL_ROW_INDEX = 7;
	/**
	 * 关联Sheet在Excel中的索引      列索引
	 * */
	public static final int LINK_SHEET_COL_INDEX = 1;
	/**
	 * 关联Sheet在Excel中的索引      行索引
	 * */
	public static final int LINK_SHEET_ROW_INDEX = 8;
	/**
	 * Excel头信息结束行的索引
	 * */
	public static final int HEAD_END_ROW_INDEX = 10;
	/**
	 * 内容起始格子
	 * */
	public static final Point CONTENT_START = new Point(1, 11);
	
}
