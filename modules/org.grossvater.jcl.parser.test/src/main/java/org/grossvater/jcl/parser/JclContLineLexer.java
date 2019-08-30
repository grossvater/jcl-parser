package org.grossvater.jcl.parser;

import static org.grossvater.jcl.parser.LineUtils.lines;
import org.junit.Test;

import static org.grossvater.jcl.parser.TestUtils.marginOpts;

public class JclContLineLexer {
   @Test
   public void testPosParam() {
       AntlrUtils.match("//XXX YYY A,\n// B", new int[] { JclLexer.FIELD_ID, JclLexer.FIELD_NAME, JclLexer.BLANK, JclLexer.FIELD_OP,
                                                          JclLexer.BLANK, JclLexer.PARAM_TOKEN, JclLexer.COMMA, JclLexer.NL, 
                                                          JclLexer.FIELD_ID, JclLexer.BLANK, JclLexer.PARAM_TOKEN });
   }

    @Test
    public void testPosParamWithComment() {
        AntlrUtils.match(lines(
                    "//XXX YYY A,",
                    "//* COMMENT",
                    "// B"),
                new int[] { JclLexer.FIELD_ID, JclLexer.FIELD_NAME, JclLexer.BLANK, JclLexer.FIELD_OP,
                JclLexer.BLANK, JclLexer.PARAM_TOKEN, JclLexer.COMMA, JclLexer.NL,
                JclLexer.FIELD_COMMENT, JclLexer.COMMENT, JclLexer.NL,
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
       AntlrUtils.match(lines("//XXX YYY A,B HELLO X",
                              "// WORLD"
                        ), new int[] { JclLexer.FIELD_ID, JclLexer.FIELD_NAME, JclLexer.BLANK, JclLexer.FIELD_OP,
                                                          JclLexer.BLANK, JclLexer.PARAM_TOKEN, JclLexer.COMMA, JclLexer.PARAM_TOKEN, 
                                                          JclLexer.BLANK, JclLexer.COMMENT, JclLexer.NL,
                                                          JclLexer.FIELD_ID, JclLexer.BLANK, JclLexer.COMMENT},
                        marginOpts(21));
   }
   
   @Test
   public void testString() {
       AntlrUtils.match("//XXX YYY 'A\n// B'", new int[] { JclLexer.FIELD_ID, JclLexer.FIELD_NAME, JclLexer.BLANK, JclLexer.FIELD_OP,
                                                          JclLexer.BLANK, JclLexer.PARAM_STRING_START_TOKEN, JclLexer.NL, 
                                                          JclLexer.FIELD_ID, JclLexer.BLANK, JclLexer.PARAM_STRING_END_TOKEN });
   }
   
   @Test
   public void testMultiString() {
       AntlrUtils.match("//XXX YYY 'A\n// B\n// C'", new int[] { JclLexer.FIELD_ID, JclLexer.FIELD_NAME, JclLexer.BLANK, JclLexer.FIELD_OP,
                                                          JclLexer.BLANK, JclLexer.PARAM_STRING_START_TOKEN, JclLexer.NL, 
                                                          JclLexer.FIELD_ID, JclLexer.BLANK, JclLexer.PARAM_STRING_MIDDLE_TOKEN, JclLexer.NL,
                                                          JclLexer.FIELD_ID, JclLexer.BLANK, JclLexer.PARAM_STRING_END_TOKEN });
   }      
}