package io.github.nerjalnosk.jsonlight.mapper.errors;

import io.github.nerjalnosk.jsonlight.elements.JsonElement;

public abstract class JsonMapperError extends Exception {
    public final JsonElement element;

    protected JsonMapperError(JsonElement element) {
        this.element = element;
    }

    protected JsonMapperError(String s, JsonElement element) {
        super(s);
        this.element = element;
    }

    protected JsonMapperError(Throwable t, JsonElement element) {
        super(t);
        this.element = element;
    }

    protected JsonMapperError(String s, Throwable t, JsonElement element) {
        super(s, t);
        this.element = element;
    }

    @Override
    public String getMessage() {
        return String.format("%s ; Json: %s", super.getMessage(), this.element);
    }
}
