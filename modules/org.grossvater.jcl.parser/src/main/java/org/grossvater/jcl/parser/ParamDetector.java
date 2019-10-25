package org.grossvater.jcl.parser;

import org.grossvater.jcl.validator.Args;

public class ParamDetector {
    private final int paramMode;
    private final int token;
    private final int eq;
    private final String param;

    private enum State {
        INIT,
        WAIT_EQ,
        WAIT_TOKEN,
        TOKEN_RECEIVED
    }

    private int mode = -1;
    private State state = State.INIT;
    private StringBuilder buf;

    public ParamDetector(int paramMode, int token, int eq, String param) {
        Args.check(paramMode >= 0, "paramMode");
        Args.notEmpty(param, "param");

        this.paramMode = paramMode;
        this.token = token;
        this.eq = eq;
        this.param = param;
    }

    public String input(int mode, int type) {
        return input(mode, type, null);
    }

    public String input(int mode, int type, String text) {
        Args.check(mode >= 0, "mode");

        String re = null;

        if (mode != this.paramMode) {
            if (this.mode == this.paramMode && this.state == State.TOKEN_RECEIVED) {
                re = this.buf.toString();
                this.buf = null;
            }

            this.mode = mode;

            return re;
        }

        if (mode != this.mode) {
            this.state = State.INIT;
            this.buf = null;
        }

        this.mode = mode;

        switch (this.state) {
            case INIT:
                if (type == this.token && text != null && text.equals(this.param)) {
                    this.state = State.WAIT_EQ;
                }
                break;

            case WAIT_EQ:
                if (type == this.eq) {
                    this.state = State.WAIT_TOKEN;
                } else {
                    this.state = State.INIT;
                    this.buf = null;
                }
                break;

            case WAIT_TOKEN:
                if (type == this.token) {
                    this.state = State.TOKEN_RECEIVED;

                    Args.notEmpty(text, "text");
                    if (this.buf == null) {
                        this.buf = new StringBuilder();
                    }

                    this.buf.append(text);
                } else {
                    this.state = State.INIT;
                    this.buf = null;
                }
                break;

            case TOKEN_RECEIVED:
                if (type == this.eq) {
                    this.state = State.WAIT_TOKEN;

                    Args.notEmpty(text, "text");
                    if (this.buf == null) {
                        this.buf = new StringBuilder();
                    }

                    this.buf.append(text);
                } else {
                    this.state = State.INIT;

                    re = buf.toString();
                    this.buf = null;
                }
        }

        return re;
    }
}
