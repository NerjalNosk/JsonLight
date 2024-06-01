package io.github.nerjalnosk.jsonlight.mapper.errors;

public class CreationException extends Exception {
    public CreationException(String s) {
        super(s);
    }

    public CreationException(String s, Exception e) {
        super(s, e);
    }
}
