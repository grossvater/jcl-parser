package org.grossvater.jcl.parser;

import org.junit.Test;

public class JclMultiLineLexer {
	@Test
	public void testSingleFieldId() {
		AntlrUtils.match("//\n//", new int[] { JclLexer.FIELD_ID, JclLexer.NL, JclLexer.FIELD_ID });
	}
	
   @Test
    public void testName() {
        AntlrUtils.match("//XXX\n//XXX", new int[] { JclLexer.FIELD_ID, JclLexer.FIELD_NAME, JclLexer.NL,
                                                     JclLexer.FIELD_ID, JclLexer.FIELD_NAME });
    }
   
   @Test
   public void testOperation() {
       AntlrUtils.match("//XXX YYY\n//XXX YYY", new int[] { JclLexer.FIELD_ID, JclLexer.FIELD_NAME, JclLexer.BLANK, JclLexer.FIELD_OP, JclLexer.NL,
                                                            JclLexer.FIELD_ID, JclLexer.FIELD_NAME, JclLexer.BLANK, JclLexer.FIELD_OP });
   }
   
   @Test
   public void testPosParam() {
       AntlrUtils.match("//XXX YYY A\n//XXX YYY A", new int[] { JclLexer.FIELD_ID, JclLexer.FIELD_NAME, JclLexer.BLANK, JclLexer.FIELD_OP,
                                                                JclLexer.BLANK, JclLexer.PARAM_TOKEN, JclLexer.NL,
                                                                JclLexer.FIELD_ID, JclLexer.FIELD_NAME, JclLexer.BLANK, JclLexer.FIELD_OP,
                                                                JclLexer.BLANK, JclLexer.PARAM_TOKEN });
   }
   
   @Test
   public void testEndComment() {
       AntlrUtils.match("//X Y A COMM\n//X", new int[] { JclLexer.FIELD_ID, JclLexer.FIELD_NAME, JclLexer.BLANK, JclLexer.FIELD_OP,
                                                         JclLexer.BLANK, JclLexer.PARAM_TOKEN, JclLexer.BLANK, JclLexer.COMMENT, JclLexer.NL,
                                                         JclLexer.FIELD_ID, JclLexer.FIELD_NAME });
   }
}