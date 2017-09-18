package org.grossvater.jcl.parser;

import java.io.File;
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
}
