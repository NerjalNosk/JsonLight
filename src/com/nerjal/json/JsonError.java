package com.nerjal.json;

public abstract class JsonError {
    public static class JsonElementTypeException extends Exception {
        public JsonElementTypeException(String s) {
            super(s);
        }
    }
    public static class JsonObjectChildNotFoundException extends Exception {
        public JsonObjectChildNotFoundException(String s) {
            super(s);
        }
    }
    public static class JsonParseException extends Exception {
        public JsonParseException(String s) {
            super(s);
        }
    }
}
