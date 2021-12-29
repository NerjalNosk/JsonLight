package com.nerjal.json.elements;

public class JsonNumber extends JsonElement {
    private Number value;

    public JsonNumber() {
        this.value = 0;
    }
    public JsonNumber(Number n) {
        this.value = n;
    }
    public JsonNumber fromIntegerString(String s) {
        return new JsonNumber(Integer.parseInt(s));
    }
    public JsonNumber fromFloatString(String s) {
        return new JsonNumber(Float.parseFloat(s));
    }

    public void setValue(Number n) {
        this.value = n;
    }

    @Override
    public boolean isNumber() {
        return true;
    }
    @Override
    public boolean isPrimitive() {
        return true;
    }
    @Override
    public Number getAsNumber() {
        return this.value;
    }
    @Override
    public int getAsInt() {
        return this.value.intValue();
    }
    @Override
    public long getAsLong() {
        return this.value.longValue();
    }
    @Override
    public float getAsFloat() {
        return this.value.floatValue();
    }
    @Override
    public double getAsDouble() {
        return this.value.doubleValue();
    }

    @Override
    public String toString() {
        return String.valueOf(this.value);
    }
}
