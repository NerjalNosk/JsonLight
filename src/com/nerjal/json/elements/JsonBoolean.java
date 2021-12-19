package com.nerjal.json.elements;

public class JsonBoolean extends JsonElement {
    private boolean value = false;

    public JsonBoolean() {}
    public JsonBoolean(boolean b) {
        this.value = b;
    }

    public void setValue(boolean b) {
        this.value = b;
    }

    @Override
    public boolean isBoolean() {
        return true;
    }
    @Override
    public boolean isPrimitive() {
        return true;
    }
    @Override
    public boolean getAsBoolean() {
        return this.value;
    }

    @Override
    public String toString() {
        return this.value ? "true" : "false";
    }
}
