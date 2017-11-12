package org.grossvater.jcl.parser;

import java.io.File;
import java.io.Reader;
import java.io.StringReader;
import java.net.URISyntaxException;
import java.util.Iterator;
import java.util.List;
import org.antlr.v4.runtime.Token;
import org.junit.Assert;

import static org.grossvater.jcl.validator.Args.*;

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
	
	public static void assertEquals(List<Token> tokens, List<ExToken> expected) {
		notNull(tokens);
		notNull(expected);
		
		Assert.assertEquals(expected.size(), tokens.size());
		
		Iterator<Token> it = tokens.iterator();
		Iterator<ExToken> exIt = expected.iterator();
		int i = 0;
		
		while (it.hasNext() && exIt.hasNext()) {
			Token t = it.next();
			ExToken et = exIt.next();
			
			if (et.type != null) {
				Assert.assertEquals(String.format("token %d: type failed; expected %d found %d", i, et.type, t.getType()), 
										          et.type.intValue(), t.getType());
			}
			if (et.text != null) {
				Assert.assertEquals(String.format("token %d: text failed; expected %s found %s", i, et.text, t.getText()),
										          et.text, t.getText());
			}
			if (et.channel != null) {
				Assert.assertEquals(String.format("token %d: channel failed; expected %d found %d", i, et.channel, t.getChannel()), 
										          et.channel.intValue(), t.getChannel());
			}
			
			i++;
		}
	}
}
