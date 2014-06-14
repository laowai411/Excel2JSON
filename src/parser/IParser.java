package parser;
import java.io.File;
import java.util.HashMap;


public interface IParser 
{
	public String get_type();
	
	public void parse(File file);
	
	@SuppressWarnings("rawtypes")
	public HashMap getData(File file);
}
