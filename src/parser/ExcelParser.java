package parser;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;

public class ExcelParser implements IParser {

    /**
     * 3维数组 [ sheetIndex:[ colIndex[ rowIndex string ] ] ]
	 *
     */
    @SuppressWarnings("rawtypes")
	private ArrayList gSheetValueList;
    /**
     * 生成json名, 使用Sheet名字
         *
     */
    private String sheetName;
    
    /**
     * 列数
     * */
    private int col;
    
    /**
     * 行数
     * */
    private int row;
    
    /**
     * sheet的数据
     * */
    @SuppressWarnings("rawtypes")
	private HashMap data;

    public ExcelParser() {
        gSheetValueList = new ArrayList<>();
    }

    /**
     * 解析Excel文件
	 *
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
	public void parse(File file) {
        Workbook book = null;
        try {
            book = Workbook.getWorkbook(file);
        } catch (BiffException | IOException e) {
//			e.printStackTrace();
            return;
        }

        Sheet sheet = null;
        for (int i = 0; i < 1; i++) {
            try {
                sheet = book.getSheet(i);
            } catch (NullPointerException nullE) {
                //sheet数据不正确
//				nullE.printStackTrace();
                continue;
            }
            ArrayList valueList = new ArrayList<>();
            gSheetValueList.add(i, valueList);
            if (sheet != null) {
                sheetName = file.getParent() + "\\" + sheet.getName();
                col = sheet.getColumns();
                for (int colIndex = 0; colIndex < col; colIndex++) {
                    ArrayList cellList = new ArrayList<>();
                    valueList.add(colIndex, cellList);
                    row = sheet.getRows();
                    for (int rowIndex = 0; rowIndex < row; rowIndex++) {
                        if (sheet.getRow(rowIndex) != null && sheet.getColumn(colIndex) != null) {
                            Cell c = sheet.getCell(colIndex, rowIndex);
                            if (c != null) {
                                cellList.add(rowIndex, c);
                            }
                        }
                    }
                }
                book.close();
            }
        }
    }

    /**
     * 获取Excel的格子数据列表,返回的是一个三维数组
	 *
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
	public HashMap getData(File file) {
        gSheetValueList = new ArrayList<>();
        parse(file);
        data = new HashMap();
        data.put("name", sheetName);
        data.put("data", gSheetValueList);
        data.put("col", col);
        data.put("row", row);
        return data;
    }

    @Override
    public String get_type() {
        return "excel";
    }
}
