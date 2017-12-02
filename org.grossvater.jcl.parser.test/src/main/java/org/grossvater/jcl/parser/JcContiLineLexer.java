package org.grossvater.jcl.parser;

import org.junit.Test;

public class JcContiLineLexer {
   @Test
   public void testPosParam() {
       AntlrUtils.match("//XXX YYY A,\n// B", new int[] { JclLexer.FIELD_ID, JclLexer.FIELD_NAME, JclLexer.BLANK, JclLexer.FIELD_OP,
                                                          JclLexer.BLANK, JclLexer.PARAM_TOKEN, JclLexer.COMMA, JclLexer.NL, JclLexer.PARAM_TOKEN });
   }
   
   @Test
   public void testKwParam() {
       AntlrUtils.match("//XXX YYY B=,\n// C=", 
                        new int[] { JclLexer.FIELD_ID, JclLexer.FIELD_NAME, JclLexer.BLANK, JclLexer.FIELD_OP, JclLexer.BLANK, 
                                    JclLexer.PARAM_TOKEN, JclLexer.EQ, JclLexer.COMMA, JclLexer.NL,
                                    JclLexer.PARAM_TOKEN, JclLexer.EQ });
   }
}