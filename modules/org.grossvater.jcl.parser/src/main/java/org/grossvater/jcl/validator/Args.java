package org.grossvater.jcl.validator;

public class Args {
    public static void check(boolean cond) {
        if (!cond) {
            throw new IllegalArgumentException();
        }
    }

    public static void check(boolean cond, String msg) {
        if (!cond) {
            throw new IllegalArgumentException(msg);
        }
    }

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

    public static void notEmpty(String arg, String argName) {
        if (argName == null) {
            throw new IllegalArgumentException(String.format("'%s' may not be null", argName));
        }

        if (arg == null) {
            throw new IllegalArgumentException(String.format("Null argument: %s", argName));
        }
        if (arg.length() == 0) {
            throw new IllegalArgumentException(String.format("Empty argument: %s", argName));
        }
    }
}
