package org.grossvater.jcl.validator;

public class Args {
    public static void notNull(Object arg, String argName) {
        if (argName == null) {
            throw new IllegalArgumentException(String.format("'%s' may not be null", argName));
        }
        
        if (arg == null) {
            throw new IllegalArgumentException(String.format("Null argument: %s", argName));
        }
    }
    
    public static void notNull(Object arg) {
        if (arg == null) {
            throw new IllegalArgumentException("Null argument");
        }
    }    
}
