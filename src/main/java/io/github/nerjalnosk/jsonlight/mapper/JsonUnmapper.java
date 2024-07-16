package io.github.nerjalnosk.jsonlight.mapper;

import io.github.nerjalnosk.jsonlight.JsonError;
import io.github.nerjalnosk.jsonlight.elements.*;
import io.github.nerjalnosk.jsonlight.mapper.annotations.JsonDocumentation;
import io.github.nerjalnosk.jsonlight.mapper.annotations.JsonIgnore;
import io.github.nerjalnosk.jsonlight.mapper.annotations.JsonNode;
import io.github.nerjalnosk.jsonlight.mapper.annotations.JsonRequired;
import io.github.nerjalnosk.jsonlight.mapper.errors.JsonMapperFieldRequiredError;
import io.github.nerjalnosk.jsonlight.parser.options.NumberParseOptions;
import io.github.nerjalnosk.jsonlight.parser.options.ObjectParseOptions;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

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
        return serialize(object, new LinkedHashMap<>());
    }

    private static <T> JsonElement serialize(T object, Map<Integer, JsonElement> stack) throws JsonError.JsonMappingException {
        if (object == null) return new JsonString((String) null);

        int i = System.identityHashCode(object); // avoid conflicting hashes
        if (stack.containsKey(i)) return stack.get(i);

        Class<?> target = object.getClass();

        // primitives
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

        // complex types

        // class documentation
        JsonComment[] comments = (target.isAnnotationPresent(JsonDocumentation.class)) ?
                computeDoc(target.getAnnotation(JsonDocumentation.class)) : new JsonComment[0];

        // enums
        if (target.isEnum()) {
            JsonString string = new JsonString((String) null);
            for (Object inst : target.getEnumConstants()) {
                if (inst == object) {
                    string.setValue(((Enum<?>)object).name());
                }
            }
            stack.put(i, string);
            string.addRootComments(comments);
            return string;
        }
        if (target.isArray()) {
            JsonArray array = new JsonArray();
            stack.put(i, array); // stack early to avoid recursive lock
            for (Object o : (Object[]) object) {
                array.add(serialize(o, stack));
            }
            array.addRootComments(comments);
            return array;
        }
        if (JsonElement.class.isAssignableFrom(target)) {
            stack.put(i, (JsonElement) object);
            // no documentation here, we kinda know it already
            return (JsonElement) object;
        }
        if (Collection.class.isAssignableFrom(target)) {
            JsonArray array = new JsonArray();
            stack.put(i, array); // stack early to avoid recursive lock
            for (Object o : (Collection<?>) object) {
                array.add(serialize(o, stack));
            }
            array.addRootComments(comments);
            return array;
        }
        if (Map.class.isAssignableFrom(target)) {
            JsonObject obj = new JsonObject();
            stack.put(i, obj); // stack early to avoid recursive lock
            for (Map.Entry<?,?> entry : ((Map<?,?>)object).entrySet()) {
                if (!(entry.getKey() instanceof String)) continue;
                obj.put(entry.getKey().toString(), serialize(entry.getValue(), stack));
            }
            obj.addRootComments(comments);
            return obj;
        }
        JsonObject obj = new JsonObject(new ObjectParseOptions(true));
        stack.put(i, obj); // stack early to avoid recursive lock
        for (Field field : JsonMapper.getAllFields(new LinkedList<>(), target)) {
            if (field.isAnnotationPresent(JsonIgnore.class) && field.getAnnotation(JsonIgnore.class).toJson()) continue;

            boolean b = field.isAccessible();
            field.setAccessible(true);
            boolean nonNull = field.isAnnotationPresent(JsonRequired.class);
            String name = field.getName();

            if (field.isAnnotationPresent(JsonNode.class)) {
                JsonNode node = field.getAnnotation(JsonNode.class);
                nonNull |= node.required();
                name = node.value();
            }

            if (field.isAnnotationPresent(JsonDocumentation.class)) {
                for (JsonComment comment : computeDoc(field.getAnnotation(JsonDocumentation.class))) {
                    obj.add(null, comment);
                }
            }

            try {
                if (field.get(object) == null && nonNull) throw new JsonMapperFieldRequiredError(field.getName(), null);
                obj.put(name, serialize(field.get(object), stack));
            } catch (IllegalAccessException | JsonMapperFieldRequiredError e) {
                throw new JsonError.JsonMappingException(e);
            } finally {
                field.setAccessible(b);
            }
        }
        obj.addRootComments(comments);
        return obj;
    }

    private static JsonComment[] computeDoc(JsonDocumentation documentation) {
        Collection<JsonComment> commentList = new ArrayList<>();
        String[] lines = docLines(documentation);
        if (documentation.format() == JsonDocumentation.Format.LINE) {
            for (String s : lines) {
                commentList.add(new JsonComment(s, false));
            }
        } else if (documentation.format() == JsonDocumentation.Format.BLOCK) {
            commentList.add(new JsonComment(String.join("\n", lines), true));
        } else {
            commentList.add(new JsonComment(String.join("\n", lines)));
        }
        return commentList.toArray(new JsonComment[0]);
    }

    private static String[] docLines(JsonDocumentation documentation) {
        List<String> list = new ArrayList<>();
        for (String s : documentation.value()) {
            String[] strings = s.split("(\\r\\n|\\r|\\n)");
            list.addAll(Arrays.stream(strings).map(String::trim).collect(Collectors.toList()));
        }
        return list.toArray(new String[0]);
    }
}
