package com.nerjal.json;

public abstract class Tests {
    public static void main(String[] args) throws JsonError.JsonParseException, JsonError.JsonElementTypeException {
        System.out.println(JsonParser.parseJson(JsonParser.parseString("{a : 1}")));
        System.out.println(JsonParser.parseJson(JsonParser.parseString("[1,2,,,]")));
        System.out.println(JsonParser.parseJson(JsonParser.parseString("{'a':1,'b':2}")));
        System.out.println(JsonParser.parseJson(JsonParser.parseString("{a:'1'}")));
        System.out.println(JsonParser.parseJson(JsonParser.parseString("{a:\"1\\\n\"}")));
        System.out.println(JsonParser.parseJson(JsonParser.parseString("{a : 1,//a\nb:2}")));
        System.out.println(JsonParser.parseJson(JsonParser.parseString("{a : 1//a\nb:2}")));
    }
}
