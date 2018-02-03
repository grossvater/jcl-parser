package org.grossvater.jcl.parser;

import java.util.List;

import org.antlr.v4.runtime.Token;

public class ParseUtils {
	public static final int TOKEN_TS_F_NONE = 0;
	public static final int TOKEN_TS_F_NL_BEFORE = 1;

	public static String toString(List<Token> tokens) {
		return toString(tokens, TOKEN_TS_F_NONE);
	}
	
	public static String toString(List<Token> tokens, int flags) {
		StringBuffer b = new StringBuffer();
		
		if (tokens == null) {
			return null;
		}
		
		b.append("[");
		int i = 0;
		for (Token t : tokens) {
			if (i > 0) {
				b.append(',');
				if ((flags & TOKEN_TS_F_NL_BEFORE) > 0) {
					b.append("\n");
				} else {
					b.append(' ');
				}
			}
			
			b.append("[@");
			b.append(i);
			b.append(':');
			b.append(t.getLine());
			b.append(':');
			b.append(t.getStartIndex());
			b.append('-');
			b.append(t.getStopIndex());
			b.append(':');
			b.append(t.getText().replace("\r\n", "\\r\\n").replace("\n", "\\n"));
			b.append(':');
			b.append(t.getType());
			b.append("]");
			
			i++;
		}
		b.append("]");
		return b.toString();
	}
}
