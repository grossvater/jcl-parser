package org.grossvater.jcl.parser;

import org.junit.Test;

public class JcContLineLexer {
   @Test
   public void testPosParam() {
       AntlrUtils.match("//XXX YYY A,\n// B", new int[] { JclLexer.FIELD_ID, JclLexer.FIELD_NAME, JclLexer.BLANK, JclLexer.FIELD_OP,
                                                          JclLexer.BLANK, JclLexer.PARAM_TOKEN, JclLexer.COMMA, JclLexer.NL, 
                                                          JclLexer.FIELD_ID, JclLexer.BLANK, JclLexer.PARAM_TOKEN });
   }
   
   @Test
   public void testKwParam() {
       AntlrUtils.match("//XXX YYY B=,\n// C=", 
                        new int[] { JclLexer.FIELD_ID, JclLexer.FIELD_NAME, JclLexer.BLANK, JclLexer.FIELD_OP, JclLexer.BLANK, 
                                    JclLexer.PARAM_TOKEN, JclLexer.EQ, JclLexer.COMMA, JclLexer.NL,
                                    JclLexer.FIELD_ID, JclLexer.BLANK, JclLexer.PARAM_TOKEN, JclLexer.EQ });
   }
   
   @Test
   public void testEndLineComment() {
       AntlrUtils.match("//XXX YYY A,B HELLO\n// WORLD", new int[] { JclLexer.FIELD_ID, JclLexer.FIELD_NAME, JclLexer.BLANK, JclLexer.FIELD_OP,
                                                          JclLexer.BLANK, JclLexer.PARAM_TOKEN, JclLexer.COMMA, JclLexer.PARAM_TOKEN, 
                                                          JclLexer.BLANK, JclLexer.COMMENT, JclLexer.NL,
                                                          JclLexer.FIELD_ID, JclLexer.BLANK, JclLexer.COMMENT});
   }   
}