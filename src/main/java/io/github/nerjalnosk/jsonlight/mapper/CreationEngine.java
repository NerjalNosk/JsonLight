package io.github.nerjalnosk.jsonlight.mapper;

import io.github.nerjalnosk.jsonlight.elements.*;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

final class CreationEngine {
    private CreationEngine() {}

    public static <U, T extends List<U>> T createList(Class<T> listClass, JsonArray values) throws CreationException {
        List<?> list = null;
        if (listClass == List.class) {
            list = new ArrayList<>();
        } else if (listClass.isInterface()) {
            throw new UnsupportedOperationException("complex interface instantiation not implemented yet");
        } else if (Modifier.isAbstract(listClass.getModifiers())) {
            throw new UnsupportedOperationException("abstract class instantiation not implemented yet");
        } else {
            try {
                list = listClass.getConstructor().newInstance();
            } catch (NoSuchMethodException|InvocationTargetException|InstantiationException|IllegalAccessException e) {
                // ignored
            }
            if (list == null) {
                try {
                    list = listClass.getConstructor(int.class).newInstance(values.size());
                } catch (NoSuchMethodException|InvocationTargetException|InstantiationException|IllegalAccessException e) {
                    throw new CreationException("Couldn't find an appropriate constructor for class "+listClass, e);
                }
            }
        }
        List<Object> oList = (List<Object>) list;
        for (JsonElement element : values) {
            oList.add(getEncapsulated(element));
        }
        return listClass.cast(list);
    }

    public static <U, T extends Map<String, U>> T createMap(Class<T> mapClass, JsonObject object) throws CreationException {
        Map<String, ?> map = null;
        if (mapClass == Map.class) {
            map = new HashMap<>();
        } else if (mapClass.isInterface()) {
            throw new UnsupportedOperationException("complex interface instantiation not implemented yet");
        } else if (Modifier.isAbstract(mapClass.getModifiers())) {
            throw new UnsupportedOperationException(("abstract class instantiation not implemented yet"));
        } else {
            try {
                map = mapClass.getConstructor().newInstance();
            } catch (NoSuchMethodException|InvocationTargetException|InstantiationException|IllegalAccessException e) {
                // ignored
            }
            if (map == null) {
                try {
                    map = mapClass.getConstructor(int.class).newInstance(object.entrySet().size());
                } catch (NoSuchMethodException|InvocationTargetException|InstantiationException|IllegalAccessException e) {
                    throw new CreationException("Couldn't find an appropriate constructor for class "+mapClass, e);
                }
            }
        }
        Map<String, Object> oMap = (Map<String, Object>) map;
        for (JsonObject.JsonNode node : object) {
            oMap.put(node.getKey(), getEncapsulated(node.getValue()));
        }
        return mapClass.cast(map);
    }

    static Object getEncapsulated(JsonElement element) throws CreationException {
        if (element == JsonString.NULL) {
            return null;
        }
        if (element instanceof JsonArray) {
            JsonArray array = (JsonArray) element;
            return createList(List.class, array);
        }
        if (element instanceof JsonString) {
            JsonString string = (JsonString) element;
            return string.getAsString();
        }
        if (element instanceof JsonNumber) {
            JsonNumber number = (JsonNumber) element;
            return number.getAsNumber();
        }
        if (element instanceof JsonBoolean) {
            JsonBoolean bool = (JsonBoolean) element;
            return bool.getAsBoolean();
        }
        JsonObject object = (JsonObject) element;
        return createMap(Map.class, object);
    }

    public static class CreationException extends Exception {
        public CreationException(String s) {
            super(s);
        }
        public CreationException(String s, Exception e) {
            super(s, e);
        }
    }
}
