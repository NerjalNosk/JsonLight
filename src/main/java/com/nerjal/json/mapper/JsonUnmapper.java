package com.nerjal.json.mapper;

import com.nerjal.json.elements.*;
import com.nerjal.json.mapper.annotations.JsonIgnore;
import com.nerjal.json.mapper.annotations.JsonNode;
import com.nerjal.json.mapper.annotations.JsonRequired;
import com.nerjal.json.mapper.errors.JsonMapperFieldRequiredError;
import com.nerjal.json.parser.options.NumberParseOptions;

import java.lang.reflect.Field;
import java.util.*;

import static com.nerjal.json.parser.options.NumberParseOptions.NumberFormat.*;

/**
 * Class for serializing any object to a {@link JsonElement}
 * @author nerjal
 */
public class JsonUnmapper {

    /**
     * Main method to serialize an object to a {@link JsonElement}
     * @param object the instance to serialize
     * @return the JsonElement fitting the specified object
     */
    public static <T> JsonElement serialize(T object) {
        return serialize(object, new HashSet<>());
    }

    private static <T> JsonElement serialize(T object, Set<Integer> stack) {

        if (object == null) return new JsonString((String) null);
        Class<?> target = object.getClass();
        if (target == Integer.class)
            return new JsonNumber((Integer)object);
        if (target == Long.class)
            return new JsonNumber((Long)object);
        if (target == Double.class)
            return new JsonNumber((Double)object, new NumberParseOptions(true, SCIENTIFIC));
        if (target == Float.class)
            return new JsonNumber((Float)object, new NumberParseOptions(true));
        if (target == Boolean.class)
            return new JsonBoolean((Boolean) object);
        if (target == String.class)
            return new JsonString((String) object);
        if (target.isEnum()) {
            JsonString string = new JsonString((String) null);
            for (Field field : target.getFields()) {
                field.setAccessible(true);
                try {
                    if (field.get(object) == object) {
                        string.setValue(field.getName());
                        break;
                    }
                } catch (IllegalAccessException ignored) {}
            }
            return string;
        }
        if (target.isArray()) {
            JsonArray array = new JsonArray();
            Arrays.asList((Object[]) object).forEach(o -> array.add(serialize(o, stack)));
            return array;
        }
        if (Collection.class.isAssignableFrom(target)) {
            Class<?> c = target.getSuperclass();
            while (c != null) {
                if (c == JsonElement.class) return (JsonElement) object;
                c = c.getSuperclass();
            }
            JsonArray array = new JsonArray();
            ((Collection<?>)object).forEach(o -> array.add(serialize(o, stack)));
            return array;
        }
        if (Map.class.isAssignableFrom(target)) {
            JsonObject obj = new JsonObject();
            ((Map<?,?>)object).forEach((key, value) -> {
                if (!(key instanceof String)) return;
                obj.put((String) key, serialize(value, stack));
            });
            return obj;
        }
        JsonObject obj = new JsonObject();
        for (Field field : JsonMapper.getAllFields(new LinkedList<>(), target)) {
            if (field.isAnnotationPresent(JsonIgnore.class)) continue;

            field.setAccessible(true);
            boolean nonNull = field.isAnnotationPresent(JsonRequired.class);
            String name = field.getName();

            if (field.isAnnotationPresent(JsonNode.class)) {
                JsonNode node = field.getAnnotation(JsonNode.class);
                nonNull |= node.required();
                name = node.value();
            }

            try {
                if (field.get(object) == null && nonNull) throw new JsonMapperFieldRequiredError(field.getName());
                obj.put(name, serialize(field.get(object), stack));
            } catch (IllegalAccessException | JsonMapperFieldRequiredError e) {
                throw new RuntimeException(e);
            }
        }
        return obj;
    }
}
