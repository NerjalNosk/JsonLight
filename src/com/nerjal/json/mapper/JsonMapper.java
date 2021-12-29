package com.nerjal.json.mapper;

import com.nerjal.json.JsonError;
import com.nerjal.json.elements.JsonElement;
import com.nerjal.json.elements.JsonObject;
import com.nerjal.json.mapper.annotations.JsonNode;
import com.nerjal.json.mapper.annotations.JsonRequired;
import com.nerjal.json.mapper.annotations.JsonSkipSuperclass;

import java.lang.reflect.*;
import java.util.*;

public class JsonMapper {
    private static boolean isPrimitive(Class<?> clazz) {
        return clazz == int.class ||
                clazz == long.class ||
                clazz == float.class ||
                clazz == double.class ||
                clazz == boolean.class;
    }

    private static <E> E arrayListToArray(ArrayList<?> arrayList, Class<E> target) {
        if (!target.isArray()) return null;
        Class<?> subType = target.getComponentType();
        var out = target.cast(Array.newInstance(subType, arrayList.size()));
        for (int i = 0; i < arrayList.size(); i++) {
            Array.set(out, i, arrayList.get(i));
        }
        return out;
    }

    /**
     * Gets all class fields including superclass fields unless @JsonSkipSuperclass is present
     * @param fields list of fields
     * @param type class type
     * @return expanded list of fields
     */
    private static List<Field> getAllFields(List<Field> fields, Class<?> type) {
        fields.addAll(Arrays.asList(type.getDeclaredFields()));

        if (type.getSuperclass() != null && !type.isAnnotationPresent(JsonSkipSuperclass.class)) {
            getAllFields(fields, type.getSuperclass());
        }

        return fields;
    }

    @SuppressWarnings("unchecked")
    public static <T> T map(JsonElement element, Class<T> target)
            throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException,
            JsonError.JsonElementTypeException, JsonCastingError, JsonError.ChildNotFoundException {

        if (target == Integer.class || target == int.class) {
            if (!element.isNumber())
                throw new JsonCastingError(element, target);
            return (T) Integer.valueOf(element.getAsInt());
        } else if (target == Long.class || target == long.class) {
            if (!element.isNumber())
                throw new JsonCastingError(element, target);
            return (T) Long.valueOf(element.getAsLong());

        } else if (target == Float.class || target == float.class) {
            if (!element.isNumber())
                throw new JsonCastingError(element, target);
            return (T) Float.valueOf(element.getAsFloat());
        } else if (target == Double.class || target == double.class) {
            if (!element.isNumber())
                throw new JsonCastingError(element, target);
            return (T) Double.valueOf(element.getAsDouble());

        } else if (target == Boolean.class || target == boolean.class) {
            if (!element.isBoolean())
                throw new JsonCastingError(element, target);
            return (T) Boolean.valueOf(element.getAsBoolean());

        } else if (target == String.class) {
            if (!element.isString())
                throw new JsonCastingError(element, target);
            return (T) element.getAsString();

        } else if (target.isArray()) {
            if (!element.isJsonArray())
                throw new JsonCastingError(element, target);
            Class<?> subType = target.getComponentType();
            var arr = new ArrayList<>();
            for (JsonElement elem : element.getAsJsonArray()) {
                arr.add(map(elem, subType));
            }
            if (isPrimitive(subType)) {
                return arrayListToArray(arr, target);
            }
            return (T) arr.toArray();

        } else if (Collection.class.isAssignableFrom(target) || Map.class.isAssignableFrom(target)) {
            // not supposed to be passed as target!
            throw new JsonCastingError(element, target);
        }

        if (!element.isJsonObject()) {
            throw new JsonCastingError(element, target);
        }


        T instance = target.getDeclaredConstructor().newInstance();
        for (Field field : getAllFields(new LinkedList<>(), target)) {
            field.setAccessible(true);
            boolean required = field.isAnnotationPresent(JsonRequired.class);
            String name = field.getName();
            if (field.isAnnotationPresent(JsonNode.class)) {
                var elem = field.getAnnotation(JsonNode.class);
                required |= elem.required();
                name = elem.value();
            }

            if (!element.getAsJsonObject().contains(name)) {
                if (required)
                    throw new JsonCastingError(element, target);
                else continue;
            }

            if (Collection.class.isAssignableFrom(field.getType())) {
                if (!element.getAsJsonObject().get(name).isJsonArray())
                    throw new JsonCastingError(element.getAsJsonObject().get(name), target);

                Type t = field.getGenericType();

                var type = (Class<?>) ((ParameterizedType) t).getActualTypeArguments()[0];
                var arr = new ArrayList<>();
                for (JsonElement elem : element.getAsJsonObject().get(name).getAsJsonArray()) {
                    arr.add(map(elem, type));
                }
                field.set(instance, arr);
            } else if (Map.class.isAssignableFrom(field.getType())) {
                if (!element.getAsJsonObject().get(name).isJsonObject())
                    throw new JsonCastingError(element.getAsJsonObject().get(name), target);

                Type t = field.getGenericType();

                var keyType = (Class<?>) ((ParameterizedType) t).getActualTypeArguments()[0];
                var valueType = (Class<?>) ((ParameterizedType) t).getActualTypeArguments()[1];

                if (keyType != String.class)
                    throw new JsonCastingError(element, keyType);

                var map = new HashMap<String, Object>();

                for (JsonObject.JsonNode node : element.getAsJsonObject().get(name).getAsJsonObject().entrySet()) {
                    map.put(
                            node.getKey(),
                            map(node.getValue(), valueType)
                    );
                }
                field.set(instance, map);
            } else {
                field.set(
                        instance,
                        map(element.getAsJsonObject().get(name), field.getType())
                );
            }
        }
        return instance;
    }
}
