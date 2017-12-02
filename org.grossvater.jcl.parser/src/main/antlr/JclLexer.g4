/**
 * Copyright 2017 grosvater
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
lexer grammar JclLexer;

@header {
package org.grossvater.jcl.parser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
}

@members {
	private Logger L = LoggerFactory.getLogger(this.getClass());
	
	private JclParserOpts opts;
	
	public JclLexer(CharStream input, JclParserOpts opts) {
		this(input);
		
		this.opts = opts != null ? opts 
							     : JclParserOpts.newBuilder().build();
	}
}

/* BEGIN mode: id (default) */
fragment
F_BLANK: [ \t]+
;

FIELD_ID: '//'
;

FIELD_INSTREAM_DELIM: '/*' -> mode(MODE_DELIM)
;

FIELD_COMMENT: '//*' -> mode(MODE_COMMENT)
;

BLANK: F_BLANK -> channel(HIDDEN), mode(MODE_OP)
;

NL: '\n' '\r'? -> channel(HIDDEN)
;
/* END mode: id (default) */

/* BEGIN mode: name */
// can't avoid symbol duplication, ANTLR doesn't allow token reference in a set
// TODO: what about /
FIELD_NAME: ~[ \t\n\r/] ~[ \t\r\n]*
;

NAME_BLANK: F_BLANK -> channel(HIDDEN), type(BLANK), mode(MODE_OP)
;

NAME_NL: '\n' '\r'? -> channel(HIDDEN), type(NL), mode(DEFAULT_MODE)
;
/* END mode: name */

mode MODE_COMMENT;
COMMENT: ~[\n]+
;

COMMENT_NL: '\n' '\r'? -> type(NL), mode(DEFAULT_MODE)
;

mode MODE_DELIM;
DELIM_COMMENT: {getInterpreter().getCharPositionInLine() > 2}? ~[\n\r]* -> type(COMMENT), mode(DEFAULT_MODE /* mode: id */)
;

DELIM_BLANK: {getInterpreter().getCharPositionInLine() == 2}? F_BLANK -> channel(HIDDEN), type(BLANK)
;

DELIM_NL: '\n' '\r'? -> channel(HIDDEN), type(NL), mode(DEFAULT_MODE)
;

mode MODE_OP;

// can't avoid symbol duplication, ANTLR doesn't allow token reference in a set
FIELD_OP: ~[ \t\n\r]+
;

OP_BANK: F_BLANK -> type(BLANK), mode(MODE_PARAM)
;

OP_NL: '\n' '\r'? -> channel(HIDDEN), type(NL), mode(DEFAULT_MODE)
;

mode MODE_PARAM;

LP: '('
;

RP: ')'
;

EQ: '='
;

COMMA: ','
;

// can't avoid symbol duplication, ANTLR doesn't allow token reference in a set
PARAM_TOKEN: ~('=' | [ \t\n\r] | ',' | '(' | ')')+
;

PARAM_NL: '\n' '\r'? -> channel(HIDDEN), type(NL), mode(DEFAULT_MODE)
;

PARAM_BANK: F_BLANK -> type(BLANK), mode(MODE_COMMENT)
;

mode MODE_COMMENT;

END_LINE_COMMENT: ~[\n\r]+ -> type(COMMENT), mode(DEFAULT_MODE)
;

END_LINE_COMMENT_NL: '\n' '\r'? -> channel(HIDDEN), type(NL), mode(DEFAULT_MODE)
;