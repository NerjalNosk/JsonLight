package com.github.nerjalnosk.jsonlight.mapper.errors;

public class JsonMapperFieldRequiredError extends Exception {
    public JsonMapperFieldRequiredError(String fieldName) {
        super("JSON field " + fieldName + " is required!");
    }
}
