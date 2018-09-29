package org.grossvater.jcl.parser;

import static org.grossvater.jcl.validator.Args.*;

public class ExToken {
    public String text;
    public Integer type;
    public Integer channel;

    public ExToken(Integer type) {
        this(type, null, null);
    }

    public ExToken(Integer type, String text) {
        this(type, text, null);
    }
    
    public ExToken(Integer type, String text, Integer channel) {
        notNull(type);
        
        this.text = text;
        this.type = type;
        this.channel = channel;
    }
}