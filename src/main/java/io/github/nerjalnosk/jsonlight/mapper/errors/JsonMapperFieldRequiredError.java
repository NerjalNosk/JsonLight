package io.github.nerjalnosk.jsonlight.mapper.errors;

import io.github.nerjalnosk.jsonlight.elements.JsonObject;

public class JsonMapperFieldRequiredError extends JsonMapperError {
    public JsonMapperFieldRequiredError(String fieldName, JsonObject object) {
        super("JSON field " + fieldName + " is required!", object);
    }
}
