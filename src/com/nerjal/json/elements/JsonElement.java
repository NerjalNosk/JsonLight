package com.nerjal.json.elements;

import com.nerjal.json.JsonError;

import java.util.Arrays;

public abstract class JsonElement {
    private JsonComment[] comments = new JsonComment[]{};

    public final void addRootComment(JsonComment comment) {
        int size = this.comments.length;
        this.comments = Arrays.copyOf(this.comments,this.comments.length+1);
        this.comments[size] = comment;
    }

    public final void addRootComments(JsonComment[] comments) {
        int size = this.comments.length;
        this.comments = Arrays.copyOf(this.comments, size+comments.length);
        for (JsonComment comment : comments) {
            this.comments[size] = comment;
            size++;
        }
    }

    public final JsonComment[] getRootComments() {
        return this.comments;
    }

    public void clearRootComment() {
        this.comments = new JsonComment[]{};
    }

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

    public abstract String typeToString();

    public JsonObject getAsJsonObject() throws JsonError.JsonElementTypeException {
        throw new JsonError.JsonElementTypeException(String.format("%s is not an Object element",this.getClass().getName()));
    }
    public JsonArray getAsJsonArray() throws JsonError.JsonElementTypeException {
        throw new JsonError.JsonElementTypeException(String.format("%s is not an Array element",this.getClass().getName()));
    }
    public JsonComment getAsJsonComment() throws JsonError.JsonElementTypeException {
        throw new JsonError.JsonElementTypeException(String.format("%s is not a Comment element", this.getClass().getName()));
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
    public long getAsLong() throws JsonError.JsonElementTypeException {
        throw new JsonError.JsonElementTypeException(String.format("%s is not an int element",this.getClass().getName()));
    }
    public float getAsFloat() throws JsonError.JsonElementTypeException {
        throw new JsonError.JsonElementTypeException(String.format("%s is not a Float element",this.getClass().getName()));
    }
    public double getAsDouble() throws JsonError.JsonElementTypeException {
        throw new JsonError.JsonElementTypeException(String.format("%s is not an int element",this.getClass().getName()));
    }
    public boolean getAsBoolean() throws JsonError.JsonElementTypeException {
        throw new JsonError.JsonElementTypeException(String.format("%s is not a Boolean element",this.getClass().getName()));
    }
}
