package org.grossvater.jcl.parser;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.IntStream;
import org.antlr.v4.runtime.LexerNoViableAltException;
import org.antlr.v4.runtime.Token;

public class JclLexer extends JclBaseLexer {
    public JclLexer(CharStream input) {
        super(input);
    }

    public JclLexer(CharStream input, JclParserOpts opts) {
        super(input, opts);
    }

    public JclLexer(CharStream input, JclParserOpts opts, String delimiter, int mode) {
        super(input, opts, delimiter, mode);
    }

    @Override
    public Token nextToken() {
        if (_input == null) {
            throw new IllegalStateException("nextToken requires a non-null input stream.");
        }

        // Mark start location in char stream so unbuffered streams are
        // guaranteed at least have text of current token
        int tokenStartMarker = _input.mark();
        try{
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
                    if (this._mode == MODE_INSTREAM_DATA
                            && _tokenStartCharPositionInLine == 0) {
                        delimiterFound = matchDelimiterAndAdvanceStream(this._input);

                        if (delimiterFound) {
                            _interp.setCharPositionInLine(_tokenStartCharPositionInLine + this.delimiter.length());

                            ttype = FIELD_INSTREAM_DELIM;
                            _mode(MODE_INSTREAM_COMMENT);
                        }
                    }

                    if (!delimiterFound) {
                        try {
                            ttype = getInterpreter().match(_input, _mode);
                        } catch (LexerNoViableAltException e) {
                            notifyListeners(e);        // report error
                            recover(e);
                            ttype = SKIP;
                        }
                    }

                    if ( _input.LA(1)==IntStream.EOF ) {
                        _hitEOF = true;
                    }
                    if ( _type == Token.INVALID_TYPE ) _type = ttype;
                    if ( _type ==SKIP ) {
                        continue outer;
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
