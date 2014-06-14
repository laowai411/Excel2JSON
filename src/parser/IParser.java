package parser;
import java.io.File;
import java.util.Vector;


public interface IParser 
{
	public String get_type();
	
	public void parse(File file);
	
	public Vector<Vector<Vector<String>>> getSheetValueList(File file);
}
