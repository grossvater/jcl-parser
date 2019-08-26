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
        String
    }
    
    public JclLexer(CharStream input, JclParserOpts opts) {
        this(input);
        
        this.opts = opts != null ? opts 
                                 : JclParserOpts.newBuilder().build();
    }
    
    @Override
    public void reset() {
        super.reset();
        this.cont = Cont.None;
    }

    /**
     * 	Set new mode. Clear continuation if the target mode is the DEFAULT_MODE.
     */        
    private final void _mode(int mode) {
        _mode(mode, null);
    }
    
    /**
     * 	Set new mode and submode. If the continuation parameter is not provided,
     * clear continuation if the current mode is the DEFAULT_MODE.
     */    
 	private final void _mode(int newmode, Cont cont) {
        L.trace("Mode {}=>{}", this.modeNames[this._mode], this.modeNames[newmode]);

        if (cont != null) {
            if (this.cont != cont) {
                this.cont = cont;
                L.trace("Continuation type: {}.", cont);
            }            
        } else {
        	// preserve continuation mode unless current mode is the default mode
        	if (_mode == DEFAULT_MODE && this.cont != Cont.None) {
		        this.cont = Cont.None;
		        L.trace("Exit continuation.");
		    }
        }
        
        mode(newmode);        
    }

    private final void _submode(Cont cont) {
        if (this.cont != cont) {
            if (cont == null) {
                this.cont = Cont.None;
                L.trace("Exit continuation.");
            } else {
                L.trace("Continuation type: {}.", cont);
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
            lookAheadSyntaxError("Continuation line must start with '// '");
            _mode(MODE_NAME);
        }
    } else {
        _mode(MODE_NAME);
    }
}
;

FIELD_INSTREAM_DELIM: '/*' { 
    if (this.cont != Cont.None) {
        syntaxError("Instream data not allowed, continuation line expected");
    }
    
    _mode(MODE_INSTREAM_DELIM);
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

OP_BLANK: F_BLANK { _mode(MODE_PARAM); } -> channel(HIDDEN), type(BLANK)
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
 
PARAM_NL: {_input.LA(-1) != ','}? '\r'? '\n' { _mode(DEFAULT_MODE); } -> channel(HIDDEN), type(NL)
;

PARAM_CONT_LINE: {_input.LA(-1) == ','}? '\r'? '\n' { _mode(DEFAULT_MODE, Cont.Param); }
    -> channel(HIDDEN), type(NL)
;

PARAM_BLANK: F_BLANK { _mode(MODE_END_LINE_COMMENT); } -> channel(HIDDEN), type(BLANK)
;

mode MODE_END_LINE_COMMENT;

END_LINE_COMMENT: ~[\n\r]+ {
    String t = getText();
     
    if (t.charAt(t.length() - 1) == ' ') {
        _mode(DEFAULT_MODE);
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
        mode = MODE_COMMENT;
    } else if (this.cont == Cont.Param) {
        mode = MODE_PARAM;
    } else if (this.cont == Cont.String) {
        mode = MODE_PARAM;
        cont = Cont.String;
    }
    
    _mode(mode, cont);
} -> channel(HIDDEN), type(BLANK)
;