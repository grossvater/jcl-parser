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
parser grammar JclParser;

options { tokenVocab=JclLexer; }

@header {
package org.grossvater.jcl.parser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
}

@members {
	private Logger L = LoggerFactory.getLogger(this.getClass());
	
	private JclParserOpts opts;
	
	public JclParser(TokenStream input, JclParserOpts opts) {
		this(input);
		
		this.opts = opts != null ? opts 
							     : JclParserOpts.newBuilder().build();
	}
}
unit: record*
;

record:
	FIELD_ID FIELD_NAME? FIELD_OP posParams? kwParams?
;

posParams:
	PARAM_TOKEN (COMMA PARAM_TOKEN)*
;

kwParams:
	kwParam (COMMA kwParam)*
;

kwParam:
	PARAM_TOKEN EQ PARAM_TOKEN
;