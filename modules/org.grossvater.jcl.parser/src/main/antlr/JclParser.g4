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
parser grammar JclParser;

options { tokenVocab=JclBaseLexer; }

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

unit: records EOF
;

records: 
	record*
;

record:
    FIELD_ID FIELD_NAME? operation
    | FIELD_ID FIELD_NAME? instreamOperation
    | ifStmt
    | nullStmt
;

operation:
    FIELD_OP params?
    | FIELD_OP params comment?
;

instreamOperation:
    instreamHeader instreamBody? instreamEnd
;

instreamHeader:
    instreamOp params?
    | instreamOp params comment?
;

instreamOp:
    (OP_XMIT | OP_DD) (PARAM_DD_STAR | PARAM_DD_DATA)?
;

instreamBody:
    (INSTREAM_DATA_LINE | NL)+
;

instreamEnd:
    FIELD_INSTREAM_DELIM COMMENT?
;

params:
	posParamList (COMMA kwParamList)?
	| kwParamList
;

posParamList:
    posParam (COMMA posParam)*
;

posParam:
	token
	| paramString
	|
;

kwParamList:
    kwParamExpr (COMMA kwParamExpr)*
;

kwParamExpr:
    kwParam EQ kwParamValue
;

kwParam:
	token
;

kwParamValue:
	token (EQ+ token)*
;

paramString:
    string
    | multilineString
;

token:
    PARAM_TOKEN | PARAM_DD_STAR | PARAM_DD_DATA
;

string:
	PARAM_STRING_TOKEN
;

multilineString:
    PARAM_STRING_START_TOKEN (FIELD_ID PARAM_STRING_MIDDLE_TOKEN)* FIELD_ID PARAM_STRING_END_TOKEN
;

comment:
    COMMENT
;

ifStmt:
    ifHeader ifBody
    elseStmt?
    endIf
;

ifHeader:
    FIELD_ID FIELD_NAME? OP_IF expr THEN comment?
;

ifBody:
    records
;

elseStmt:
    elseHeader ifBody
;

elseHeader:
    FIELD_ID FIELD_NAME? OP_ELSE comment?
;

endIf:
    FIELD_ID FIELD_NAME? OP_ENDIF comment?
;

expr:
    NUMBER
    | exprToken
    | NOT_OP expr
    | expr op expr
    | EXPR_LP expr EXPT_RP
;

op:
    compOp
    | logOp
;

compOp: GT_OP | LT_OP | NG_OP | NL_OP | EQ_OP | NE_OP | GE_OP | LE_OP
;

logOp: AND_OP | OR_OP
;

exprToken:
    EXPR_TOKEN (EXPR_DOT EXPR_TOKEN)*
;

nullStmt:
    FIELD_ID
;
