package com.nerjal.json.mapper.errors;

public class JsonMapperFieldRequiredError extends Exception {
    public JsonMapperFieldRequiredError(String fieldName) {
        super("JSON field " + fieldName + " is required!");
    }
}
