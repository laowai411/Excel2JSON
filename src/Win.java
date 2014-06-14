import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.filechooser.FileNameExtensionFilter;

public class Win implements ActionListener 
{
	
	final JLabel label = new JLabel("");
	
	/**
	 * 文件选择窗口
	 * */
	private JFileChooser fileChooser = new JFileChooser();
	
	
	public Component createComponents() 
	{
        JButton button = new JButton("选择Excel或者json");
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
		filter = new FileNameExtensionFilter("JSON", "json");
		fileChooser.addChoosableFileFilter(filter);
		fileChooser.setMultiSelectionEnabled(true);
	}

	/**
	 * 选择了文件之后,启动计时器,一个个解析
	 * */
    public void actionPerformed(ActionEvent e) 
    {
    	fileChooser.showOpenDialog(label);
    	ParserUtil.parse(fileChooser.getSelectedFiles());
    }
}
