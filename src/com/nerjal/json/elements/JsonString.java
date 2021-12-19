package com.nerjal.json.elements;

public class JsonString extends JsonElement {
    private String value;
    
    public JsonString(String value) {
        this.value = value;
    }
    public JsonString() {
        this.value = null;
    }

    public void setValue(String s) {
        this.value = s;
    }
    
    @Override
    public boolean isString() {
        return true;
    }
    @Override
    public boolean isPrimitive() {
        return true;
    }
    @Override
    public String getAsString() {
        return this.value;
    }

    @Override
    public String toString() {
        return this.value;
    }
}
