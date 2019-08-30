package org.grossvater.jcl.parser;

import org.junit.Test;

import static org.grossvater.jcl.parser.LineUtils.lines;
import static org.grossvater.jcl.parser.TestUtils.marginOpts;

public class JclLexerMultiLineTest {
    @Test
    public void testSingleFieldId() {
        AntlrUtils.match(lines("//",
                               "//"
                         ), new int[] { JclLexer.FIELD_ID, JclLexer.NL, JclLexer.FIELD_ID });
    }
    
    @Test
    public void testName() {
        AntlrUtils.match(lines("//XXX",
                               "//XXX"
                         ), new int[] { JclLexer.FIELD_ID, JclLexer.FIELD_NAME, JclLexer.NL,
                                                     JclLexer.FIELD_ID, JclLexer.FIELD_NAME });
    }
   
   @Test
   public void testOperation() {
       AntlrUtils.match(lines("//XXX YYY",
                              "//XXX YYY"
                        ), new int[] { JclLexer.FIELD_ID, JclLexer.FIELD_NAME, JclLexer.BLANK, JclLexer.FIELD_OP, JclLexer.NL,
                                                            JclLexer.FIELD_ID, JclLexer.FIELD_NAME, JclLexer.BLANK, JclLexer.FIELD_OP });
   }
   
   @Test
   public void testPosParam() {
       AntlrUtils.match(lines("//XXX YYY A",
                              "//XXX YYY A"
                        ), new int[] { JclLexer.FIELD_ID, JclLexer.FIELD_NAME, JclLexer.BLANK, JclLexer.FIELD_OP,
                                                                JclLexer.BLANK, JclLexer.PARAM_TOKEN, JclLexer.NL,
                                                                JclLexer.FIELD_ID, JclLexer.FIELD_NAME, JclLexer.BLANK, JclLexer.FIELD_OP,
                                                                JclLexer.BLANK, JclLexer.PARAM_TOKEN });
   }
   
   @Test
   public void testEndComment() {
       AntlrUtils.match(lines("//X Y A COMM X",
                              "// ENT"
                        ), new int[] { JclLexer.FIELD_ID, JclLexer.FIELD_NAME, JclLexer.BLANK, JclLexer.FIELD_OP,
                                                         JclLexer.BLANK, JclLexer.PARAM_TOKEN, JclLexer.BLANK, JclLexer.COMMENT, JclLexer.NL,
                                                         JclLexer.FIELD_ID, JclLexer.BLANK, JclLexer.COMMENT },
                        marginOpts(14));
   }
}