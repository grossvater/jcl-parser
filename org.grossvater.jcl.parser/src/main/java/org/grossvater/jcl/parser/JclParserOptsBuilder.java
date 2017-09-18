package org.grossvater.jcl.parser;

public class JclParserOptsBuilder {
	private JclParserOpts opts = new JclParserOpts();
	
	public JclParserOpts build() {
		checkState();
		
		JclParserOpts r = this.opts;
		
		r = this.opts;
		return r;
	}
	
	private void checkState() {
		if (this.opts == null) {
			throw new IllegalStateException("Already built.");
		}
	}
}
