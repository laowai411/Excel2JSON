package create;

import java.io.File;

import parser.IParser;

public interface ICreater {

	public void writeFile(IParser parser, File srcFile);
}
