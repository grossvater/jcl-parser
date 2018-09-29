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
    private Cont submode = Cont.None;
    
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
        this.submode = Cont.None;
    }
    
    private void _mode(int mode) {
        _mode(mode, null);
    }
        
    private void _mode(int newmode, Cont submode) {
        if (L.isTraceEnabled()) {
            L.trace("Mode {}=>{}", this._mode, newmode);
        }
        
        if (submode != null) {
            if (this.submode != submode) {
                this.submode = submode;
                
                if (L.isTraceEnabled()) {
                    L.trace("Submode: {}.", submode);
                }
            }            
        } else if (_mode == DEFAULT_MODE && this.submode != Cont.None) {
            this.submode = Cont.None;
            
            if (L.isTraceEnabled()) {
                L.trace("Exit cont mode.");
            }            
        }
        
        mode(newmode);        
    }
}

fragment
F_BLANK: [ \t]+
;

FIELD_ID: '//' { 
    if (this.submode != Cont.None) {
        if (_input.LA(1) == ' ') {
            _mode(MODE_CONT_EAT_SPACE, this.submode);
        } else {
            // TODO: use a message logger
            System.err.println("Broken continuation line.");
            _mode(MODE_NAME);
        }
    } else {
        _mode(MODE_NAME);
    }
}
;

FIELD_INSTREAM_DELIM: '/*' { 
    if (this.submode != Cont.None) {
        System.err.println("Broken continuation line.");
    }
    
    _mode(MODE_INSTREAM_DELIM);
}
;

FIELD_COMMENT: '//*' {
    if (submode != Cont.None && submode != Cont.Param) {
        System.err.println("Broken continuation line.");
    }
         
    _mode(MODE_COMMENT);
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

COMMENT_NL: '\r'? '\n' { _mode(DEFAULT_MODE); } -> type(NL)
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

PARAM_STRING_TOKEN: '\'' (~(['] | [\r\n]) | '\'\'')*? '\''
;

PARAM_STRING_START_TOKEN: '\'' (~([\r\n] | '\'') | '\'\'')* {
    // set in advance, then new line will make the transition to default mode
    this.submode = Cont.String;    
}
;

PARAM_STRING_END_TOKEN: {this.submode == Cont.String}? (~([\r\n] | '\'') | '\'\'')* '\'' {
    this.submode = Cont.None; 
}
;

PARAM_STRING_MIDDLE_TOKEN: {this.submode == Cont.String}? (~([\r\n] | '\'') | '\'\'')*
    // still keep String submode    
;

// can't avoid symbol duplication, ANTLR doesn't allow token reference in a set
PARAM_TOKEN: ~('\'' | '=' | [ \t\n\r] | ',' | '(' | ')') ~('=' | [ \t\n\r] | ',' | '(' | ')')*
;
 
PARAM_NL: {_input.LA(-1) != ','}? '\r'? '\n' { _mode(DEFAULT_MODE); } -> channel(HIDDEN), type(NL)
;

PARAM_CONT_LINE: {_input.LA(-1) == ','}? '\r'? '\n' { _mode(DEFAULT_MODE, Cont.Param); }
    -> channel(HIDDEN), type(NL)
;

PARAM_BLANK: F_BLANK { _mode(MODE_END_LINE_COMMENT); } -> type(BLANK)
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
    Cont submode = Cont.None;
    
    if (this.submode == Cont.Comment) {
        mode = MODE_COMMENT;
    } else if (this.submode == Cont.Param) {
        mode = MODE_PARAM;
    } else if (this.submode == Cont.String) {
        mode = MODE_PARAM;
        submode = Cont.String;
    }
    
    _mode(mode, submode);
} -> channel(HIDDEN), type(BLANK)
;