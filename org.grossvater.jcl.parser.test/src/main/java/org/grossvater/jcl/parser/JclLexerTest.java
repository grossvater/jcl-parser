package org.grossvater.jcl.parser;

import org.junit.Ignore;
import org.junit.Test;

public class JclLexerTest {
	@Test
	public void testSingleFieldId() {
		AntlrUtils.match("//", new ExToken(JclLexer.FIELD_ID));
	}

	@Test
	public void testNoName() {
		AntlrUtils.match("// ", new int[] { JclLexer.FIELD_ID, JclLexer.BLANK });
	}

	@Test
	public void testName() {
		AntlrUtils.match("//XXX", new int[] { JclLexer.FIELD_ID, JclLexer.FIELD_NAME });
	}

	@Test
	public void testDelimiterEmpty() {
		AntlrUtils.match("/*", new int[] { JclLexer.FIELD_INSTREAM_DELIM });
	}

	@Test
	@Ignore
	public void testDelimiter() {
		AntlrUtils.match("/*XXX", new int[] { JclLexer.FIELD_INSTREAM_DELIM, JclLexer.COMMENT });
	}

	@Test
	public void testDelimiterWithSpace() {
		AntlrUtils.match("/* XXX", new int[] { JclLexer.FIELD_INSTREAM_DELIM, JclLexer.BLANK, JclLexer.COMMENT });
	}
	
	@Test
	public void testCommentEmpty() {
		AntlrUtils.match("//*", new int[] { JclLexer.FIELD_COMMENT });
	}

	@Test
	public void testComment() {
		AntlrUtils.match("//*XXX", new int[] { JclLexer.FIELD_COMMENT, JclLexer.COMMENT });
	}
	
	public void testOperation() {
		AntlrUtils.match("//XXX YYY", new int[] { JclLexer.FIELD_ID, JclLexer.BLANK, JclLexer.FIELD_OP });
	}
	
	public void testPosParam() {
		AntlrUtils.match("//XXX YYY A", new int[] { JclLexer.FIELD_ID, JclLexer.BLANK, JclLexer.FIELD_OP,
													JclLexer.PARAM_TOKEN });
	}

	public void testPosParams() {
		AntlrUtils.match("//XXX YYY A, B", new int[] { JclLexer.FIELD_ID, JclLexer.BLANK, JclLexer.FIELD_OP,
													   JclLexer.PARAM_TOKEN, JclLexer.COMMA, JclLexer.PARAM_TOKEN });
	}
	
	public void testKwParam() {
		AntlrUtils.match("//XXX YYY A=B", new int[] { JclLexer.FIELD_ID, JclLexer.BLANK, JclLexer.FIELD_OP,
													  JclLexer.PARAM_TOKEN, JclLexer.EQ, JclLexer.PARAM_TOKEN });
	}
	
	public void testKwEmptyParam() {
		AntlrUtils.match("//XXX YYY A=", new int[] { JclLexer.FIELD_ID, JclLexer.BLANK, JclLexer.FIELD_OP,
													 JclLexer.PARAM_TOKEN, JclLexer.EQ });
	}
	
	public void testKwParamS() {
		AntlrUtils.match("//XXX YYY A=B,X=Y", new int[] { JclLexer.FIELD_ID, JclLexer.BLANK, JclLexer.FIELD_OP,
													      JclLexer.PARAM_TOKEN, JclLexer.EQ, JclLexer.PARAM_TOKEN,
													      JclLexer.COMMA, 
													      JclLexer.PARAM_TOKEN, JclLexer.EQ, JclLexer.PARAM_TOKEN });
	}
	
	public void testEndComment() {
		AntlrUtils.match("//XXX YYY A END OF LINE", new int[] { JclLexer.FIELD_ID, JclLexer.BLANK, JclLexer.FIELD_OP,
													   JclLexer.PARAM_TOKEN, JclLexer.COMMENT });
	}
}