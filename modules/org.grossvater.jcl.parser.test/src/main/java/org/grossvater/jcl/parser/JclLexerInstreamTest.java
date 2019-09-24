package org.grossvater.jcl.parser;

import org.junit.Test;

import static org.grossvater.jcl.parser.LineUtils.lines;

public class JclLexerInstreamTest {
    @Test
    public void testDelimiter() {
        AntlrUtils.match("/*", new int[] { JclLexer.FIELD_INSTREAM_DELIM }, null, JclLexer.INSTREAM_DELIM_DEFAULT);
    }

    @Test
    public void testDelimiterWithSpace() {
        AntlrUtils.match("/* XXX", new int[] { JclLexer.FIELD_INSTREAM_DELIM, JclLexer.BLANK, JclLexer.COMMENT },
                null, JclLexer.INSTREAM_DELIM_DEFAULT);
    }

    @Test
    public void testCustomDelimiter() {
        AntlrUtils.match("AAA", new int[] { JclLexer.FIELD_INSTREAM_DELIM }, null, "AAA");
    }

    @Test
    public void testDdStar() {
        AntlrUtils.match(lines("// DD *", "free text", "/*"),
                new int[] { JclLexer.FIELD_ID, JclLexer.BLANK, JclLexer.FIELD_DD, JclLexer.BLANK, JclLexer.PARAM_DD_STAR, JclLexer.NL,
                            JclLexer.INSTREAM_DATA_LINE, JclLexer.NL,
                            JclLexer.FIELD_INSTREAM_DELIM });
    }

    @Test
    public void testDdCustomDelimiter() {
        AntlrUtils.match(lines("// DD *,DLM=XXX", "free text", "XXX"),
                new int[] { JclLexer.FIELD_ID, JclLexer.BLANK, JclLexer.FIELD_DD, JclLexer.BLANK, JclLexer.PARAM_DD_STAR,
                                JclLexer.COMMA, JclLexer.PARAM_TOKEN, JclLexer.EQ, JclLexer.PARAM_TOKEN, JclLexer.NL,
                            JclLexer.INSTREAM_DATA_LINE, JclLexer.NL,
                            JclLexer.FIELD_INSTREAM_DELIM });
    }
}
