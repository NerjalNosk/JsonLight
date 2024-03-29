package io.github.nerjalnosk.jsonlight.mapper;

import io.github.nerjalnosk.jsonlight.JsonError;
import io.github.nerjalnosk.jsonlight.elements.*;
import io.github.nerjalnosk.jsonlight.mapper.annotations.JsonIgnore;
import io.github.nerjalnosk.jsonlight.mapper.annotations.JsonNode;
import io.github.nerjalnosk.jsonlight.mapper.annotations.JsonRequired;
import io.github.nerjalnosk.jsonlight.mapper.errors.JsonMapperFieldRequiredError;
import io.github.nerjalnosk.jsonlight.parser.options.NumberParseOptions;

import java.lang.reflect.Field;
import java.util.*;

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
    public static <T> JsonElement serialize(T object) throws JsonError.JsonMappingException {
        return serialize(object, new HashSet<>());
    }

    private static <T> JsonElement serialize(T object, Set<Integer> stack) throws JsonError.JsonMappingException {

        if (object == null) return new JsonString((String) null);
        Class<?> target = object.getClass();
        if (target == Integer.class)
            return new JsonNumber((Integer)object);
        if (target == Long.class)
            return new JsonNumber((Long)object);
        if (target == Double.class)
            return new JsonNumber((Double)object, new NumberParseOptions(true, NumberParseOptions.NumberFormat.SCIENTIFIC));
        if (target == Float.class)
            return new JsonNumber((Float)object, new NumberParseOptions(true));
        if (target == Boolean.class)
            return new JsonBoolean((Boolean) object);
        if (target == String.class)
            return new JsonString((String) object);
        if (target.isEnum()) {
            JsonString string = new JsonString((String) null);
            for (Object inst : target.getEnumConstants()) {
                if (inst == object) {
                    string.setValue(((Enum<?>)object).name());
                }
            }
            return string;
        }
        if (target.isArray()) {
            JsonArray array = new JsonArray();
            for (Object o : (Object[]) object) {
                array.add(serialize(o, stack));
            }
            return array;
        }
        if (Collection.class.isAssignableFrom(target)) {
            Class<?> c = target.getSuperclass();
            while (c != null) {
                if (c == JsonElement.class) return (JsonElement) object;
                c = c.getSuperclass();
            }
            JsonArray array = new JsonArray();
            for (Object o : (Collection<?>) object) {
                array.add(serialize(o, stack));
            }
            return array;
        }
        if (Map.class.isAssignableFrom(target)) {
            JsonObject obj = new JsonObject();
            for (Map.Entry<?,?> entry : ((Map<?,?>)object).entrySet()) {
                if (!(entry.getKey() instanceof String)) continue;
                obj.put((String) entry.getKey(), serialize(entry.getValue(), stack));
            }
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
                throw new JsonError.JsonMappingException(e);
            }
        }
        return obj;
    }
}
