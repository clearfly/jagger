package co.cfly.jagger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class JarTextUtil {
	public String getFile(String inFile, String searchPattern) throws IOException
	  {
	    InputStream is = getClass().getResourceAsStream(inFile);
	    InputStreamReader isr = new InputStreamReader(is);
	    BufferedReader br = new BufferedReader(isr);
	    StringBuffer sb = new StringBuffer();
	    String line;
	    while ((line = br.readLine()) != null) 
	    {
	    	if (searchPattern == null) {
	    		sb.append(line + "\n");
	    	}
	    	else {
	    		if (line.toLowerCase().contains(searchPattern.toLowerCase())) { sb.append(line + "\n"); }
	    	}
	    }
	    br.close();
	    isr.close();
	    is.close();
	    return sb.toString();
	  }
	
	public String dumpFile(String inFile) throws IOException
	{
		JarTextUtil thisFile = new JarTextUtil();
		return thisFile.getFile(inFile,null);
	}
	
	public String searchFile(String inFile, String searchPattern) throws IOException
	{
		String searchResults = null;
		JarTextUtil thisFile = new JarTextUtil();
		searchResults = thisFile.getFile(inFile,searchPattern);
		return searchResults;
	}
}