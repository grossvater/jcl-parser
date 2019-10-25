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
lexer grammar JclBaseLexer;

tokens {
    FIELD_INSTREAM_DELIM,

    OP_DD,
    OP_XMIT,
    OP_IF,
    OP_ELSE,
    OP_ENDIF,

    PARAM_DD_DATA,
    PARAM_DD_STAR
}

@header {
package org.grossvater.jcl.parser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

}

@members {
    private Logger L = LoggerFactory.getLogger(this.getClass());
    
    private JclParserOpts opts;

    /**
     *  Parameters, comments and strings may be continued on the next line. Continuation submode is entered by calling
     * {@see #_submode(Cont)} or {@see #_mode(int, Cont)}. In the last case, subsequent calls with continuation
     * argument null will preserve the continuation submode.
     * <p>
     *  The submode is exited by explicitly entering another continuation submode (via <code>_submode(newcont)</code>)
     * or when exiting DEFAULT_MODE by calling
     * <code>_mode(newmode, null)</code> or <code>_mode(newmode)</code>.
     * </p>
     */
    private Cont cont = Cont.None;
    
    private enum Cont {
        None,
        Param,
        Comment,
        String,
        IfExpr
    }

    private int rightMargin = JclParserOpts.RIGHT_MARGIN_DEFAULT;

    public static final String INSTREAM_DELIM_DEFAULT = "/*";

    protected String delimiter = INSTREAM_DELIM_DEFAULT;

    protected enum InstreamType {
        None,

        // JCL statements terminate the instream (DD *)
        Standard,

        // JCL statements don't terminates the instream (DD DATA & XMIT)
        Raw
    }

    protected InstreamType instreamType = InstreamType.None;

    protected static final String OP_IF_TEXT = "IF";
    protected static final String OP_ELSE_TEXT = "ELSE";
    protected static final String OP_ENDIF_TEXT = "ENDIF";
    protected static final String OP_DD_TEXT = "DD";
    protected static final String OP_XMIT_TEXT = "XMIT";

    protected static final String PARAM_DD_STAR_TEXT = "*";
    protected static final String PARAM_DD_DATA_TEXT = "DATA";
    protected static final String PARAM_DLM_TEXT = "DLM";

    protected String lastOp = null;

    public JclBaseLexer(CharStream input, JclParserOpts opts) {
        this(input, opts, INSTREAM_DELIM_DEFAULT, DEFAULT_MODE);
    }

    public JclBaseLexer(CharStream input, JclParserOpts opts, String delimiter, int mode) {
        this(input);

        if (delimiter != null && delimiter.length() == 0) {
            throw new IllegalArgumentException("delimiter");
        }

        this.opts = opts != null ? opts : JclParserOpts.newBuilder().build();
        this.rightMargin = this.opts.getRightMargin();
        this.delimiter = delimiter == null ? INSTREAM_DELIM_DEFAULT : delimiter;

        _mode(mode);
    }

    @Override
    public void reset() {
        super.reset();

        this.cont = Cont.None;
        this.delimiter = INSTREAM_DELIM_DEFAULT;
    }

    /**
     * 	Set new mode. Clear continuation if the target mode is the DEFAULT_MODE.
     */        
    protected final void _mode(int mode) {
        _mode(mode, null);
    }
    
    /**
     * 	Set new mode and submode. If the continuation parameter is not provided,
     * clear continuation if the current mode is the DEFAULT_MODE.
     */    
 	protected final void _mode(int newmode, Cont cont) {
        L.trace("Mode {}=>{}", this.modeNames[this._mode], this.modeNames[newmode]);

        if (cont != null) {
            if (this.cont != cont) {
                this.cont = cont;
                L.trace("Continuation type: {}", cont);
            }            
        } else {
        	// preserve continuation mode unless current mode is the default mode
        	if (_mode == DEFAULT_MODE && this.cont != Cont.None) {
		        this.cont = Cont.None;
		        L.trace("Exit continuation");
		    }
        }
        
        mode(newmode);        
    }

    private final void _submode(Cont cont) {
        if (this.cont != cont) {
            if (cont == null) {
                this.cont = Cont.None;
                L.trace("Exit continuation");
            } else {
                L.trace("Continuation type: {}", cont);
            }
        }

        this.cont = cont;
    }

    private void syntaxError(String msg) {
    	String text = _input.getText(Interval.of(_tokenStartCharIndex, _input.index()));
		String displayMsg = "Lexical error: '" + msg + "' at '" + getErrorDisplay(text) + "'.";
		ANTLRErrorListener listener = getErrorListenerDispatch();
		
		listener.syntaxError(this, null, _tokenStartLine, _tokenStartCharPositionInLine, displayMsg, null);
	}
	
    private void lookAheadSyntaxError(String msg) {
        String text = _input.getText(Interval.of(_tokenStartCharIndex, _input.index()));
		String displayMsg = "Lexical error: '" + msg + "' after '" + getErrorDisplay(text) + "'.";
		ANTLRErrorListener listener = getErrorListenerDispatch();
		
		listener.syntaxError(this, null, _tokenStartLine, _tokenStartCharPositionInLine, displayMsg, null);
	}

	private boolean isPosition(int pos, String pred) {
	    return getInterpreter().getCharPositionInLine() == pos;
	}
}

/* Identifier field detection mode (ANTLR default mode, no declaration needed */
/* mode MODE_FIELD, aka DEFAULT_MODE; */

fragment
F_BLANK: [ \t]+
;

FIELD_ID: '//' {
    if (this.cont != Cont.None) {
        if (_input.LA(1) == ' ') {
            _mode(MODE_CONT_EAT_SPACE, this.cont);
        } else {
            // TODO: test me
            lookAheadSyntaxError("Continuation line must start with '// '");
            _mode(MODE_NAME);
        }
    } else {
        _mode(MODE_NAME);
    }
}
;

FIELD_COMMENT: '//*' {
    if (cont != Cont.None && cont != Cont.Param) {
        syntaxError("Comment not allowed, continuation line expected");
    }
         
    _mode(MODE_COMMENT, this.cont);
}
;

NL: '\r'? '\n' -> channel(HIDDEN)
;

mode MODE_NAME;

// can't avoid symbol duplication, ANTLR doesn't allow token reference in a set
// TODO: what about /
FIELD_NAME: ~[ \t\n\r/] ~[ \t\r\n]*
;

BLANK: F_BLANK { _mode(MODE_OP); } -> channel(HIDDEN)
;

NAME_NL: '\r'? '\n' { _mode(DEFAULT_MODE); } -> channel(HIDDEN), type(NL)
;

mode MODE_COMMENT;
COMMENT: ~[\r\n]+
;

COMMENT_NL: '\r'? '\n' { _mode(DEFAULT_MODE, this.cont); } -> type(NL)
;

// standard says don't do it, though it doesn't say whether it is supported:
//
// "Do not continue a comment statement using continuation conventions. Instead, code
// additional comment statements."
/*

COMMENT_NL: {getInterpreter().getCharPositionInLine() <= this.rightMargin}? '\r'? '\n' { _mode(DEFAULT_MODE, this.cont); } -> type(NL)
;

COMMENT_NL_CONT: {getInterpreter().getCharPositionInLine() > this.rightMargin}? '\r'? '\n'
    // if already in continuation, keep the submode
    { _mode(DEFAULT_MODE, this.cont != Cont.None ? this.cont : Cont.Comment); }
    -> channel(HIDDEN), type(NL)
;
*/

mode MODE_OP;

// can't avoid symbol duplication, ANTLR doesn't allow token reference in a set
FIELD_OP: ~[ \t\n\r]+
;

OP_BLANK: F_BLANK { _mode(MODE_PARAM); } -> channel(HIDDEN), type(BLANK)
;

OP_NL: '\r'? '\n'
    {
        if (this.instreamType != InstreamType.None) {
            _mode(MODE_INSTREAM_DATA);
        } else {
            _mode(DEFAULT_MODE);
        }
    }
    -> channel(HIDDEN), type(NL)
;

mode MODE_PARAM;

LP: '('
;

RP: ')'
;

ASSIGN: '='
;

COMMA: ','
;

PARAM_STRING_TOKEN: '\'' (~(['] | [\r\n]) | '\'\'')*? '\''
;

PARAM_STRING_START_TOKEN: '\'' (~([\r\n] | '\'') | '\'\'')* {
    // set in advance, then new line will make the transition to default mode
    _submode(Cont.String);
}
;

PARAM_STRING_END_TOKEN: {this.cont == Cont.String}? (~([\r\n] | '\'') | '\'\'')* '\'' {
    _submode(Cont.None);
}
;

PARAM_STRING_MIDDLE_TOKEN: {this.cont == Cont.String}? (~([\r\n] | '\'') | '\'\'')*
    // still keep String continuation mode    
;

// can't avoid symbol duplication, ANTLR doesn't allow token reference in a set
PARAM_TOKEN: ~('\'' | '=' | [ \t\n\r] | ',' | '(' | ')') ~('=' | [ \t\n\r] | ',' | '(' | ')')*
;
 
PARAM_NL: {_input.LA(-1) != ','}? '\r'? '\n'
    {
        if (this.instreamType != InstreamType.None) {
            _mode(MODE_INSTREAM_DATA);
        } else {
            _mode(DEFAULT_MODE);
        }
    }
    -> channel(HIDDEN), type(NL)
;

PARAM_CONT_LINE: {_input.LA(-1) == ','}? '\r'? '\n' { _mode(DEFAULT_MODE, Cont.Param); }
    -> channel(HIDDEN), type(NL)
;

PARAM_BLANK: F_BLANK { _mode(MODE_END_LINE_COMMENT); } -> channel(HIDDEN), type(BLANK)
;

mode MODE_END_LINE_COMMENT;

END_LINE_COMMENT: ~[\n\r]+ {
    if (getInterpreter().getCharPositionInLine() < this.rightMargin) {
        if (this.instreamType != InstreamType.None) {
            _mode(MODE_INSTREAM_DATA);
        } else {
            _mode(DEFAULT_MODE);
        }
    } else {
        _mode(DEFAULT_MODE, Cont.Comment);
    }
} -> type(COMMENT)
;

END_LINE_COMMENT_NL: '\r'? '\n' { _mode(DEFAULT_MODE); } -> channel(HIDDEN), type(NL)
;

mode MODE_CONT_EAT_SPACE;

// the standard says the line is continued somewhere between columns 4 and 16,
// but we don't care about the upper limit
CONT_EAT_SPACE_BLANK: F_BLANK { 
    int mode = DEFAULT_MODE;
    Cont cont = Cont.None;
    
    if (this.cont == Cont.Comment) {
        // FIXME: should go in end line comment, comment statment can't be continued
        mode = MODE_COMMENT;
    } else if (this.cont == Cont.Param) {
        mode = MODE_PARAM;
    } else if (this.cont == Cont.String) {
        mode = MODE_PARAM;
        cont = Cont.String;
    } else if (this.cont == Cont.IfExpr) {
        mode = MODE_IF;
    }
    
    _mode(mode, cont);
} -> channel(HIDDEN), type(BLANK)
;

mode MODE_INSTREAM_DATA;

INSTREAM_JCL: {isPosition(0, "INSTREAM_JCL") && this.instreamType == InstreamType.Standard}? '//'
    { _mode(MODE_NAME); } -> type(FIELD_ID)
;

INSTREAM_NON_JCL: {isPosition(0, "INSTREAM_NON_JCL") && this.instreamType != InstreamType.Standard}? '//' ~[\r\n]*
    -> type(INSTREAM_DATA_LINE)
;

INSTREAM_DATA_LINE: {isPosition(0, "INSTREAM_DATA_LINE")}? '/'? ~'/' ~[\r\n]+
;

INSTREAM_NL: '\r'? '\n' -> type(NL)
;

mode MODE_INSTREAM_COMMENT;

DELIM_COMMENT: {getInterpreter().getCharPositionInLine() > 2}? ~[\n\r]* { _mode(DEFAULT_MODE); } -> type(COMMENT)
;

DELIM_BLANK: {getInterpreter().getCharPositionInLine() == 2}? F_BLANK -> channel(HIDDEN), type(BLANK)
;

DELIM_NL: '\r'? '\n' { _mode(DEFAULT_MODE); } -> channel(HIDDEN), type(NL)
;

mode MODE_IF;

THEN: 'THEN'
    {
        if (_input.LA(1) == '\r' || _input.LA(1) == '\n') {
            _mode(MODE_IF_EAT_NL);
        } else {
            _mode(MODE_END_LINE_COMMENT);
        }
    }
;

IF_BLANK: F_BLANK -> channel(HIDDEN), type(BLANK)
;

EXPR_TOKEN: [a-zA-Z_$#@] [a-zA-Z_$#@0-9]*
;

EXPR_DOT: '.'
;

NOT_OP: 'NOT' | '¬'
;

GT_OP: 'GT' | '>'
;

LT_OP: 'LT' | '<'
;

NG_OP: 'NG' | '¬>'
;

NL_OP: 'NL' | '¬<'
;

EQ_OP: 'EQ' | '='
;

NE_OP: 'NE' | '¬='
;

GE_OP: 'GE' | '>='
;

LE_OP: 'LE' | '<='
;

AND_OP: 'AND' | '&'
;

OR_OP: 'OR' | '|'
;

NUMBER: [0-9]+
;

EXPR_LP: '('
;

EXPR_RP: ')'
;

IF_NL: '\r'? '\n'
    { _mode(DEFAULT_MODE, Cont.IfExpr); } -> channel(HIDDEN), type(NL)
;

mode MODE_IF_EAT_NL;

IF_EAT_NL: '\r'? '\n' { _mode(DEFAULT_MODE); } -> channel(HIDDEN), type(NL)
;