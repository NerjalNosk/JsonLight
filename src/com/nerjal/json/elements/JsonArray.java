package com.nerjal.json.elements;

import java.util.*;
import java.util.function.UnaryOperator;

public class JsonArray extends JsonElement implements Iterable<JsonElement> {
    private final List<JsonElement> list;

    public JsonArray() {
        this.list = new ArrayList<>();
    }
    public JsonArray(Collection<JsonElement> elements) {
        this.list = List.copyOf(elements);
    }

    public JsonElement get(int index) {
        return this.list.get(index);
    }
    public JsonElement[] getAll(int from, int to) {
        return (JsonElement[]) Arrays.copyOfRange(this.list.toArray(), from, to);
    }
    public boolean remove(JsonElement element) {
        return this.list.remove(element);
    }
    public JsonElement remove(int index) {
        return this.list.remove(index);
    }
    public Collection<JsonElement> removeAll(Collection<JsonElement> elements) {
        List<JsonElement> returnList = new ArrayList<>();
        elements.forEach(e -> {
            if (list.remove(e)) returnList.add(e);
        });
        return returnList;
    }
    public void add(JsonElement element) {
        this.list.add(element);
    }
    public void add(int index, JsonElement element) {
        this.list.add(index, element);
    }
    public void addAll(Collection<JsonElement> elements) {
        this.list.addAll(elements);
    }
    public void addAll(JsonElement[] elements) {
        this.addAll(List.of(elements));
    }
    public void replaceAll(UnaryOperator<JsonElement> operator) {
        this.list.replaceAll(operator);
    }

    public int size() {
        return this.list.size();
    }

    @Override
    public boolean isJsonArray() {
        return true;
    }
    @Override
    public JsonArray getAsJsonArray() {
        return this;
    }

    @Override
    public Iterator<JsonElement> iterator() {
        return this.list.iterator();
    }
}
