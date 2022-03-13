package com.nerjal.json.mapper;

import com.nerjal.json.JsonError;
import com.nerjal.json.elements.JsonElement;
import com.nerjal.json.elements.JsonObject;
import com.nerjal.json.mapper.annotations.JsonIgnore;
import com.nerjal.json.mapper.annotations.JsonNode;
import com.nerjal.json.mapper.annotations.JsonRequired;
import com.nerjal.json.mapper.annotations.JsonSkipSuperclass;
import com.nerjal.json.mapper.errors.JsonCastingError;
import com.nerjal.json.mapper.errors.JsonMapperFieldRequiredError;

import java.lang.reflect.*;
import java.util.*;

/**
 * Class for mapping a {@link JsonElement} onto any class
 * @author CodedSakura
 */
public class JsonMapper {
    /**
     * Checks if a {@link Class} is a primitive (yes I know it's not full)
     * @param clazz - input {@link Class}
     * @return whether clazz is a primitive or not
     */
    private static boolean isPrimitive(Class<?> clazz) {
        return clazz == int.class ||
                clazz == long.class ||
                clazz == float.class ||
                clazz == double.class ||
                clazz == boolean.class;
    }

    /**
     * Converts an {@link ArrayList} to an array
     * @param arrayList - input {@link ArrayList}
     * @param target - target type array, i.e. int[] for ArrayList<Integer>
     * @return converted arrayList
     */
    private static <E> E arrayListToArray(ArrayList<?> arrayList, Class<E> target) {
        if (!target.isArray()) return null;
        Class<?> subType = target.getComponentType();
        Object out = target.cast(Array.newInstance(subType, arrayList.size()));
        for (int i = 0; i < arrayList.size(); i++) {
            Array.set(out, i, arrayList.get(i));
        }
        return (E)out;
    }

    /**
     * Gets all class fields including superclass fields unless {@link JsonSkipSuperclass} is present
     * @param fields - list of fields
     * @param type - class type
     * @return expanded list of fields
     */
    private static List<Field> getAllFields(List<Field> fields, Class<?> type) {
        fields.addAll(Arrays.asList(type.getDeclaredFields()));

        if (type.getSuperclass() != null && !type.isAnnotationPresent(JsonSkipSuperclass.class)) {
            getAllFields(fields, type.getSuperclass());
        }

        return fields;
    }

    /**
     * Main method to map a JsonElement onto a class
     * @param element - input JsonElement
     * @param target - target Class
     * @return an instance of target class with fields set appropriately to the input element
     * @throws InvocationTargetException if fails to invoke target
     * @throws InstantiationException if fails to instance target
     * @throws NoSuchMethodException if target has no constructor
     * @throws IllegalAccessException if can't access a field or the constructor
     * @throws JsonError.JsonElementTypeException if can't get element's type
     * @throws JsonCastingError if can't map element onto target
     * @throws JsonMapperFieldRequiredError if a required field is not present in structure
     */
    @SuppressWarnings("unchecked")
    public static <T> T map(JsonElement element, Class<T> target)
            throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException,
            JsonError.JsonElementTypeException, JsonError.ChildNotFoundException, JsonCastingError, JsonMapperFieldRequiredError {

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
            ArrayList arr = new ArrayList<>();
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
            if (field.isAnnotationPresent(JsonIgnore.class)) continue;

            field.setAccessible(true);
            boolean required = field.isAnnotationPresent(JsonRequired.class);
            String name = field.getName();
            if (field.isAnnotationPresent(JsonNode.class)) {
                JsonNode elem = field.getAnnotation(JsonNode.class);
                required |= elem.required();
                name = elem.value();
            }

            if (!element.getAsJsonObject().contains(name)) {
                if (required)
                    throw new JsonMapperFieldRequiredError(name);
                else continue;
            }

            if (Collection.class.isAssignableFrom(field.getType())) {
                if (!element.getAsJsonObject().get(name).isJsonArray())
                    throw new JsonCastingError(element.getAsJsonObject().get(name), target);

                Type t = field.getGenericType();

                Class type = (Class<?>) ((ParameterizedType) t).getActualTypeArguments()[0];
                List arr = new ArrayList<>();
                for (JsonElement elem : element.getAsJsonObject().get(name).getAsJsonArray()) {
                    arr.add(map(elem, type));
                }
                field.set(instance, arr);
            } else if (Map.class.isAssignableFrom(field.getType())) {
                if (!element.getAsJsonObject().get(name).isJsonObject())
                    throw new JsonCastingError(element.getAsJsonObject().get(name), target);

                Type t = field.getGenericType();

                Class keyType = (Class<?>) ((ParameterizedType) t).getActualTypeArguments()[0];
                Class valueType = (Class<?>) ((ParameterizedType) t).getActualTypeArguments()[1];

                if (keyType != String.class)
                    throw new JsonCastingError(element, keyType);

                Map map = new HashMap<String, Object>();

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
