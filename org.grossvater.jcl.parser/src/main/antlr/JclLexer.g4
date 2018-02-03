/**
 * Copyright 2017 grossvater
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
	private boolean cont = false;
	
	public JclLexer(CharStream input, JclParserOpts opts) {
		this(input);
		
		this.opts = opts != null ? opts 
							     : JclParserOpts.newBuilder().build();
	}

	private void _mode(int mode) {
		_mode(mode, null);
	}
		
	private void _mode(int mode, Boolean cont) {
		if (L.isTraceEnabled()) {
			L.trace("Mode {}=>{}", this._mode, mode);
		}
		
		mode(mode);
		if (cont != null) {
			L.trace("Cont mode: {}.", cont);
			this.cont = cont;
		} else if (_mode == DEFAULT_MODE) {
			L.trace("Exit cont mode.");
			this.cont = false;
		}
	}
}

/* BEGIN mode: id (default) */
fragment
F_BLANK: [ \t]+
;

FIELD_ID: '//' { 
	if (this.cont) {
		if (_input.LA(1) == ' ') {
			_mode(MODE_CONT_EAT_SPACE);
		} else {
			// TODO: use a message logger
			System.err.println("Broken continuation line.");
			_mode(MODE_NAME, false);
		}		
	} else {
		_mode(MODE_NAME);
	}
}
;

FIELD_INSTREAM_DELIM: {!this.cont}? '/*' { _mode(MODE_INSTREAM_DELIM); }
;

FIELD_COMMENT: '//*' { _mode(MODE_COMMENT); }
;

NL: '\r'? '\n' -> channel(HIDDEN)
;

/* END mode: id (default) */

/* BEGIN mode: name */
mode MODE_NAME;

// can't avoid symbol duplication, ANTLR doesn't allow token reference in a set
// TODO: what about /
FIELD_NAME: ~[ \t\n\r/] ~[ \t\r\n]*
;

BLANK: F_BLANK { _mode(MODE_OP); } -> channel(HIDDEN)
;

NAME_NL: '\r'? '\n' { _mode(DEFAULT_MODE); } -> channel(HIDDEN), type(NL)
;
/* END mode: name */

mode MODE_COMMENT;
COMMENT: ~[\n]+
;

COMMENT_NL: '\r'? '\n' { _mode(DEFAULT_MODE, this.cont); } -> type(NL)
;

mode MODE_INSTREAM_DELIM;
DELIM_COMMENT: {getInterpreter().getCharPositionInLine() > 2}? ~[\n\r]* { _mode(DEFAULT_MODE); } -> type(COMMENT)
;

DELIM_BLANK: {getInterpreter().getCharPositionInLine() == 2}? F_BLANK -> channel(HIDDEN), type(BLANK)
;

DELIM_NL: '\r'? '\n' { _mode(DEFAULT_MODE); } -> channel(HIDDEN), type(NL)
;

mode MODE_OP;

// can't avoid symbol duplication, ANTLR doesn't allow token reference in a set
FIELD_OP: ~[ \t\n\r]+
;

OP_BANK: F_BLANK { _mode(MODE_PARAM); } -> type(BLANK)
;

OP_NL: '\r'? '\n' { _mode(DEFAULT_MODE); } -> channel(HIDDEN), type(NL)
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

PARAM_NL: {_input.LA(-1) != ','}? '\r'? '\n' { _mode(DEFAULT_MODE); } -> channel(HIDDEN), type(NL)
;

PARAM_CONT_LINE: {_input.LA(-1) == ','}? '\r'? '\n' { _mode(DEFAULT_MODE, true); }
	-> channel(HIDDEN), type(NL)
;

PARAM_BANK: F_BLANK { _mode(MODE_COMMENT); } -> type(BLANK)
;

mode MODE_COMMENT;

END_LINE_COMMENT: ~[\n\r]+ { _mode(DEFAULT_MODE); } -> type(COMMENT)
;

END_LINE_COMMENT_NL: '\r'? '\n' { _mode(DEFAULT_MODE); } -> channel(HIDDEN), type(NL)
;

mode MODE_CONT_EAT_SPACE;

// the standard says the line is continued somewhere between columns 4 and 16,
// but we don't care about the upper limit
CONT_BLANK: F_BLANK { _mode(MODE_PARAM, false); } -> channel(HIDDEN), type(BLANK)
;
