package org.grossvater.jcl.parser;

import java.io.File;
import java.io.Reader;
import java.io.StringReader;
import java.net.URISyntaxException;

public class TestUtils {
	public static String makeFile(String base, String fileName) {
		File baseFile;
		File file;
		
		try {
			baseFile = new File(TestUtils.class.getResource(base).toURI());
		} catch (URISyntaxException e) {
			throw new RuntimeException(e);
		}
		
		file = new File(baseFile, fileName);
		
		return file.getAbsolutePath();
	}
	
	public static Reader makeReader(String... lines) {
		StringBuffer b = new StringBuffer();
		int i = 0;
		
		for (String l : lines) {
			if (i > 0) {
				b.append("\n");
			}
			b.append(l);
			i++;
		}
		
		return new StringReader(b.toString());
	}
}
