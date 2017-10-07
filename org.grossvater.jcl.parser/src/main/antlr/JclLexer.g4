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

/* BEGIN mode: name (default) */
fragment
F_BLANK: [ \t]
;

FIELD_ID: '//'
;

FIELD_INSTREAM_DELIM: '/*'
;

FIELD_COMMENT: '//*'
;

BLANK: F_BLANK -> mode(MODE_OP)
;

// can't avoid symbol duplication, ANTLR doesn't allow token reference in a set
FIELD_NAME: ~[ \t/] ~[ \t/]*
;
/* END mode: name (default) */

mode MODE_OP;

// can't avoid symbol duplication, ANTLR doesn't allow token reference in a set
FIELD_OP: ~[ \t]+
;

OP_BANK: F_BLANK -> type(BLANK), mode(MODE_PARAM);

mode MODE_PARAM;

LP: '('
;

RP: ')'
;

EQ: '='
;

COMMA: F_COMMA
;

// can't avoid symbol duplication, ANTLR doesn't allow token reference in a set
PARAM_TOKEN: ~('=' | [ \t] | ',' | '(' | ')')+
;

fragment 
F_COMMA: ','
;