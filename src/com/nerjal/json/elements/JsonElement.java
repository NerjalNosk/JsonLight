package com.nerjal.json.elements;

import com.nerjal.json.JsonError;

public abstract class JsonElement {
    public boolean isJsonObject() {
        return false;
    }
    public boolean isJsonArray() {
        return false;
    }
    public boolean isString() {
        return false;
    }
    public boolean isNumber() {
        return false;
    }
    public boolean isBoolean() {
        return false;
    }
    public boolean isPrimitive() {
        return false;
    }
    public boolean isComment() {
        return false;
    }

    public JsonObject getAsJsonObject() throws JsonError.JsonElementTypeException {
        throw new JsonError.JsonElementTypeException(String.format("%s is not an Object element",this.getClass().getName()));
    }
    public JsonArray getAsJsonArray() throws JsonError.JsonElementTypeException {
        throw new JsonError.JsonElementTypeException(String.format("%s is not an Array element",this.getClass().getName()));
    }
    public String getAsString() throws JsonError.JsonElementTypeException {
        throw new JsonError.JsonElementTypeException(String.format("%s is not a String element",this.getClass().getName()));
    }
    public Number getAsNumber() throws JsonError.JsonElementTypeException {
        throw new JsonError.JsonElementTypeException(String.format("%s is not a Number element",this.getClass().getName()));
    }
    public int getAsInt() throws JsonError.JsonElementTypeException {
        throw new JsonError.JsonElementTypeException(String.format("%s is not an int element",this.getClass().getName()));
    }
    public float getAsFloat() throws JsonError.JsonElementTypeException {
        throw new JsonError.JsonElementTypeException(String.format("%s is not a Float element",this.getClass().getName()));
    }
    public boolean getAsBoolean() throws JsonError.JsonElementTypeException {
        throw new JsonError.JsonElementTypeException(String.format("%s is not a Boolean element",this.getClass().getName()));
    }
}
