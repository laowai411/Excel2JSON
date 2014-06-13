import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.filechooser.FileNameExtensionFilter;

public class Win implements ActionListener 
{
	
	/**
	 * 选择的文件列表
	 * */
	private File[] gWatingList;
	    
	final JLabel label = new JLabel("");
	
	/**
	 * 文件选择窗口
	 * */
	private JFileChooser fileChooser = new JFileChooser();
	
	/**
	 * excel解析器
	 * */
	private static ExcelParser excelParser;
	
	/**
	 * 剩余未解析文件数量
	 * */
	private int oddFileCount;
	
	
	public Component createComponents() 
	{
        JButton button = new JButton("ѡ��excel");
        button.setMnemonic(KeyEvent.VK_I);
        button.addActionListener(this);
        label.setLabelFor(button);

        /*
         * An easy way to put space between a top-level container
         * and its contents is to put the contents in a JPanel
         * that has an "empty" border.
         */
        JPanel pane = new JPanel(new GridLayout(0, 1));
        pane.add(button);
        pane.add(label);
        pane.setBorder(BorderFactory.createEmptyBorder(
                                        30, //top
                                        30, //left
                                        10, //bottom
                                        30) //right
                                        );

        initFileChooser();
        
        return pane;
    }
	
	/**
	 * 初始化文件过滤器
	 * */
	private void initFileChooser()
	{
		 FileNameExtensionFilter filter = new FileNameExtensionFilter(
			        "Excel", "xls", "xlsm", "xlsx");

		fileChooser.setFileFilter(filter);
		fileChooser.setMultiSelectionEnabled(true);
	}

	/**
	 * 选择了文件之后,启动计时器,一个个解析
	 * */
    public void actionPerformed(ActionEvent e) 
    {
    	fileChooser.showOpenDialog(label);
    	gWatingList = fileChooser.getSelectedFiles();
    	oddFileCount = gWatingList!=null?gWatingList.length:0;
    	if(oddFileCount>0)
    	{
    		final Timer timer = new Timer();
    		timer.schedule(new TimerTask() {
				
				@Override
				public void run() 
				{
					if(oddFileCount<1)
					{
						timer.cancel();
					}
					parse();
				}
			}, 0, 2000);//立即执行,每两秒触发一次
    	}
    }
    
    /**
     * 解析excel并生成json
     * */
    private void parse()
    {
    	if(excelParser == null)
    	{
    		excelParser = new ExcelParser();
    	}
    	if(ExcelParser.isParsing())
    	{
    		return;
    	}
    	if(oddFileCount>0)
    	{
    		File file = gWatingList[oddFileCount-1];
    		Vector<Vector<Vector<String>>> valueList = ExcelParser.getSheetValueList(file);;
    		ListToJson json = new ListToJson(valueList);
    		System.out.println(new Date().toGMTString()+"   "+oddFileCount);
    	}
    	oddFileCount--;
    }
}
