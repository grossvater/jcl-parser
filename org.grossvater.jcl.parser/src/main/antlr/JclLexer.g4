lexer grammar JclLexer;

@header {
package org.grossvater.jcl.parser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
}

@members {
	private Logger L = LoggerFactory.getLogger(this.getClass());
}

NL: [\r]?[\n] -> channel(HIDDEN)
;