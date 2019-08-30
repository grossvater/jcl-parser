package org.grossvater.jcl.parser;

import org.grossvater.jcl.validator.Args;

public class JclParserOptsBuilder {
    private JclParserOpts opts = new JclParserOpts();

    public JclParserOptsBuilder setRightMargin(int rightMargin) {
        check();
        Args.check(rightMargin > 0, "Invalid margin.");

        this.opts.rightMargin = rightMargin;
        return this;
    }

    public JclParserOpts build() {
        check();
        
        JclParserOpts r = this.opts;
        
        r = this.opts;
        return r;
    }
    
    private final void check() {
        if (this.opts == null) {
            throw new IllegalStateException("Already built.");
        }
    }
}
