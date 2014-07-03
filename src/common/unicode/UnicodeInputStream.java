package common.unicode;

import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;

public class UnicodeInputStream extends InputStream{
	PushbackInputStream internalIn;
	boolean isInited = false;
	String defaultEnc;
	String encoding;
	
	private static final int BOM_SIZE = 4;
	
	UnicodeInputStream(InputStream in, String defaultEnc)
	{
		internalIn = new PushbackInputStream(in, BOM_SIZE);
		this.defaultEnc = defaultEnc;
	}
	
	public String getDefaultEncoding()
	{
		return defaultEnc;
	}
	
	public String getEncoding()
	{
		if(!isInited)
		{
			try
			{
				init();
			}
			catch(IOException e)
			{
				IllegalStateException ise = new IllegalStateException("init method failed");
				ise.initCause(ise);
				throw ise;
			}
		}
		return encoding;
	}
	
	protected void init() throws IOException
	{
		if(isInited)
		{
			return;
		}
		byte bom[] = new byte[BOM_SIZE];
		int n, unred;
		n = internalIn.read(bom, 0, bom.length);
		if((bom[0] == (byte)0x00) && (bom[1] == (byte)0x00) && (bom[2] == (byte)0xFE) && (bom[3] == (byte)0xFF))
		{
			encoding = "UTF-32BE";
			unred = n - 4;
		}
		else if((bom[0] == (byte)0xFF) && (bom[1] == (byte)0xFE) && (bom[2] == (byte)0x00) && (bom[3] == (byte)0x00))
		{
			encoding = "UTF-32LE";
			unred = n - 4;
		}
		else if((bom[0] == (byte)0xEF) && (bom[1] == (byte)0xBB) && (bom[2] == (byte)0xBF))
		{
			encoding = "UTF-8";
			unred = n - 3;
		}
		else if((bom[0] == (byte)0xFE) && (bom[1] == (byte)0xFF))
		{
			encoding = "UTF-16BE";
			unred = n - 2;
		}
		else if((bom[0] == (byte)0xFF) && (bom[1] == (byte)0xFE))
		{
			encoding = "UTF-16LE";
			unred = n - 2;
		}
		else
		{
			encoding = defaultEnc;
			unred = n;
		}
		if(unred > 0)
		{
			internalIn.unread(bom, (n-unred), unred);
		}
		isInited = true;
	}
	
	public void close() throws IOException
	{
		isInited = true;
		internalIn.close();
	}
	
	public int read() throws IOException
	{
		isInited = true;
		return internalIn.read();
	}
}
