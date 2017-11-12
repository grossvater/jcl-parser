package org.grossvater.jcl.parser;

import java.lang.invoke.MethodHandles;
import java.util.Arrays;
import java.util.List;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.Token;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.grossvater.jcl.parser.TestUtils.*;

public class JclLexerTest {
	private static Logger L = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
	
	@Test
	public void test1() {
	}
	
	@SuppressWarnings("unchecked")
	private static void lex(String content, ExToken[] expected) {
		JclLexer l = new JclLexer(CharStreams.fromString(content));
		List<Token> tokens;
		
		tokens = (List<Token>)l.getAllTokens();
		L.debug("Tokens: {}", ParseUtils.toString(tokens));
		
		assertEquals(tokens, Arrays.asList(expected));
	}
}