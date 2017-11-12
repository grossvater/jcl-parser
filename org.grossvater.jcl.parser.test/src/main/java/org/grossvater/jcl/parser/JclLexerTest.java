package org.grossvater.jcl.parser;

import java.lang.invoke.MethodHandles;
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
	public void testSingleFieldId() {
		test("//", new ExToken(JclLexer.FIELD_ID));
	}

	@Test
	public void testNoName() {
		test("// ", new int[] { JclLexer.FIELD_ID, JclLexer.BLANK });
	}

	@Test
	public void testName() {
		test("//XXX", new int[] { JclLexer.FIELD_ID, JclLexer.FIELD_NAME });
	}

	@Test
	public void testDelimiterEmpty() {
		test("/*", new int[] { JclLexer.FIELD_INSTREAM_DELIM });
	}

	@Test
	public void testDelimiter() {
		test("/*XXX", new int[] { JclLexer.FIELD_INSTREAM_DELIM, JclLexer.COMMENT });
	}

	@Test
	public void testDelimiterWithSpace() {
		test("/* XXX", new int[] { JclLexer.FIELD_INSTREAM_DELIM, JclLexer.BLANK, JclLexer.COMMENT });
	}
	
	@Test
	public void testCommentEmpty() {
		test("//*", new int[] { JclLexer.FIELD_COMMENT });
	}

	@Test
	public void testComment() {
		test("//*XXX", new int[] { JclLexer.FIELD_COMMENT, JclLexer.COMMENT });
	}

	private static void test(String content, ExToken expected) {
		test(content, new ExToken[] { expected });
	}
	
	@SuppressWarnings("unchecked")
	private static void test(String content, ExToken[] expected) {
		JclLexer l = new JclLexer(CharStreams.fromString(content));
		List<Token> tokens;
		
		tokens = (List<Token>)l.getAllTokens();
		L.debug("Tokens: {}", ParseUtils.toString(tokens));
		
		assertEquals(tokens, expected);
	}
	
	@SuppressWarnings("unchecked")
	private static void test(String content, int[] expected) {
		JclLexer l = new JclLexer(CharStreams.fromString(content));
		List<Token> tokens;
		
		tokens = (List<Token>)l.getAllTokens();
		L.debug("Tokens: {}", ParseUtils.toString(tokens));
		
		assertEquals(tokens, expected);
	}	
}