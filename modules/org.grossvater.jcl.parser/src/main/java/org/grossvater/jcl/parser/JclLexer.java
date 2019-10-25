package org.grossvater.jcl.parser;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.IntStream;
import org.antlr.v4.runtime.LexerNoViableAltException;
import org.antlr.v4.runtime.Token;

public class JclLexer extends JclBaseLexer {
    public static final String PARAM_DLM_TEXT = "DLM";

    private ParamDetector de = new ParamDetector(MODE_PARAM, PARAM_TOKEN, EQ, PARAM_DLM_TEXT);

    public JclLexer(CharStream input) {
        super(input);
    }

    public JclLexer(CharStream input, JclParserOpts opts) {
        super(input, opts);
    }

    public JclLexer(CharStream input, JclParserOpts opts, String delimiter, int mode) {
        super(input, opts, delimiter, mode);
    }

    /**
     *  Based on {@link org.antlr.v4.runtime.Lexer#nextToken()}.
     */
    @Override
    public Token nextToken() {
        if (_input == null) {
            throw new IllegalStateException("nextToken requires a non-null input stream.");
        }

        // Mark start location in char stream so unbuffered streams are
        // guaranteed at least have text of current token
        int tokenStartMarker = _input.mark();
        try {
            outer:
            while (true) {
                if (_hitEOF) {
                    emitEOF();
                    return _token;
                }

                _token = null;
                _channel = Token.DEFAULT_CHANNEL;
                _tokenStartCharIndex = _input.index();
                _tokenStartCharPositionInLine = _interp.getCharPositionInLine();
                _tokenStartLine = _interp.getLine();
                _text = null;
                do {
                    _type = Token.INVALID_TYPE;

                    int ttype = -1;
                    boolean delimiterFound = false;

                    // TODO: check why looking ahead and moving the input index doesn't work in base parser
                    if (this._mode == MODE_INSTREAM_DATA
                            && _tokenStartCharPositionInLine == 0) {
                        delimiterFound = matchDelimiterAndAdvanceStream(this._input);

                        if (delimiterFound) {
                            _interp.setCharPositionInLine(_tokenStartCharPositionInLine + this.delimiter.length());

                            ttype = FIELD_INSTREAM_DELIM;
                            this.instreamType = InstreamType.None;

                            _mode(MODE_INSTREAM_COMMENT);
                        }
                    }

                    if (!delimiterFound) {
                        try {
                            ttype = getInterpreter().match(_input, _mode);

                            // TODO: move this to base lexer maybe?
                            if (ttype == FIELD_OP) {
                                this.lastOp = getText();

                                if (this.lastOp.equals(OP_DD_TEXT)) {
                                    setType(OP_DD);
                                } else if (this.lastOp.equals(OP_XMIT_TEXT)) {
                                    setType(OP_XMIT);
                                    this.instreamType = InstreamType.Raw;
                                } else if (this.lastOp.equals(OP_IF_TEXT)) {
                                    setType(OP_IF);

                                    _mode(MODE_IF);
                                }
                            } else if (ttype == PARAM_TOKEN) {
                                // TODO: check by type here
                                // for XMIT, instream type is always raw
                                if (this.lastOp.equals(OP_DD_TEXT)) {
                                    String text = getText();

                                    if (text.equals(PARAM_DD_STAR_TEXT)) {
                                        ttype = PARAM_DD_STAR;
                                        this.instreamType = InstreamType.Standard;
                                    } else if (text.equals(PARAM_DD_DATA_TEXT)) {
                                        ttype = PARAM_DD_DATA;
                                        this.instreamType = InstreamType.Raw;
                                    }
                                }
                            }
                        } catch (LexerNoViableAltException e) {
                            notifyListeners(e);        // report error
                            recover(e);
                            ttype = SKIP;
                        }
                    }

                    if ( _input.LA(1) == IntStream.EOF ) {
                        _hitEOF = true;
                    }
                    if ( _type == Token.INVALID_TYPE ) _type = ttype;

                    if ( _type ==SKIP ) {
                        continue outer;
                    }

                    {
                        // TODO: avoid calling getText() second time
                        String re = this.de.input(this._mode, this._type, delimiterFound ? this.delimiter : getText());

                        if (re != null) {
                            this.delimiter = re;
                        }
                    }
                } while ( _type ==MORE );
                if ( _token == null ) emit();
                return _token;
            }
        }
        finally {
            // make sure we release marker after match or
            // unbuffered char stream will keep buffering
            _input.release(tokenStartMarker);
        }
    }

    private final boolean matchDelimiterAndAdvanceStream(IntStream input) {
        for (int i = 0; i < this.delimiter.length(); i++) {
            int e = this.delimiter.charAt(i);
            int la = input.LA(1 + i);

            if (e != la) {
                return false;
            }
        }

        input.seek(input.index() + this.delimiter.length());
        return true;
    }
}
