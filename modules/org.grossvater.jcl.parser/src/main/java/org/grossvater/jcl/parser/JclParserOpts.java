package org.grossvater.jcl.parser;

public class JclParserOpts {
    public static final int RIGHT_MARGIN_DEFAULT = 72;

    int rightMargin = RIGHT_MARGIN_DEFAULT;

    public int getRightMargin() {
        return this.rightMargin;
    }

    public static JclParserOptsBuilder newBuilder() {
        return new JclParserOptsBuilder();
    }
}
