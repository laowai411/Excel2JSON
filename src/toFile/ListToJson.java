package toFile;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ListToJson implements IListToFile {

    /**
     * 字段名
	 *
     */
    private Vector<String> keyList;
    public ListToJson() {
    }

    /**
     * 将从Excel得到的列表数据转换成json
	 *
     */
    @SuppressWarnings("rawtypes")
	public void writeFile(HashMap data) {
        String dataStr = get_str(data);
        if (dataStr != null) {
            FileWriter writter = null;
            try {
                File file = new File((String) data.get("name") + ".json");
                if (file.exists() == false) {
                    try {
                        file.createNewFile();
                    } catch (IOException ex) {
                        Logger.getLogger(ListToJson.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                writter = new FileWriter(file);
                writter.write(dataStr);
                writter.close();
            } catch (IOException ex) {
                Logger.getLogger(ListToJson.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                try {
                    writter.close();
                } catch (IOException ex) {
                    Logger.getLogger(ListToJson.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
	private String get_str(HashMap data) {
        String result = "";
        getKey(data);
        Vector<Vector<Vector<String>>> valueList = (Vector<Vector<Vector<String>>>) data.get("data");
        for (int i = 0; i < valueList.size(); i++) {
            result = result.concat("{"+getJSONFormatStr());
            Vector<Vector<String>> sheetData = valueList.get(i);
            int row = (int) data.get("row");
            int col = (int) data.get("col");
            for(int rowIndex=2; rowIndex<row; rowIndex++)
            {
                Vector<String> colList = sheetData.get(0);
                result = result.concat("\""+colList.get(rowIndex)+"\":{");
                for(int colIndex=1; colIndex<col; colIndex++)
                {
                    colList = sheetData.get(colIndex);
                    if(keyList.get(colIndex) == null || keyList.get(colIndex).equals("") == true)
                    {
                        continue;
                    }
                    if(colList.get(rowIndex).equals("") == true)
                    {
                    	result = result.concat("\""+keyList.get(colIndex)+"\":\"\",");
                    }
                    else
                    {
                    	result = result.concat("\""+keyList.get(colIndex)+"\":"+colList.get(rowIndex)+",");
                    }
                }
                result = result.substring(0, result.length()-1);
                result = result.concat("},"+getJSONFormatStr());
            }
            result = result.substring(0, result.lastIndexOf(",")).concat(getJSONFormatStr());
            result = result.concat("}");
        }
        return result;
    }

    private String getJSONFormatStr()
    {
        return "\n\t";
    }
    
    /**
     * 从固定的行获取属性名
	 *
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
	private void getKey(HashMap data) {
        keyList = new Vector<String>();
        Vector<Vector<Vector<String>>> valueList = (Vector<Vector<Vector<String>>>) data.get("data");
        for (int i = 0; i < valueList.size(); i++) {
            Vector<Vector<String>> sheetData = valueList.get(i);
            int col = (int) data.get("col");
            for(int colIndex=0; colIndex<col; colIndex++)
            {
                Vector<String> colList = sheetData.get(colIndex);
                keyList.add(colIndex, colList.get(1));
            }
        }
    }
}
