package io.github.nerjalnosk.jsonlight.mapper;

import io.github.nerjalnosk.jsonlight.JsonError;
import io.github.nerjalnosk.jsonlight.elements.JsonElement;
import io.github.nerjalnosk.jsonlight.elements.JsonObject;
import io.github.nerjalnosk.jsonlight.elements.JsonString;
import io.github.nerjalnosk.jsonlight.mapper.annotations.*;
import io.github.nerjalnosk.jsonlight.mapper.errors.JsonCastingError;
import io.github.nerjalnosk.jsonlight.mapper.errors.JsonMapperFieldRequiredError;
import io.github.nerjalnosk.jsonlight.mapper.errors.JsonValueError;
import io.github.nerjalnosk.jsonlight.mapper.annotations.*;

import java.lang.reflect.*;
import java.util.*;

import static io.github.nerjalnosk.jsonlight.JsonError.*;

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
        E out = target.cast(Array.newInstance(subType, arrayList.size()));
        for (int i = 0; i < arrayList.size(); i++) {
            Array.set(out, i, arrayList.get(i));
        }
        return out;
    }

    /**
     * Gets all class fields including superclass fields unless {@link JsonSkipSuperclass} is present
     * @param fields - list of fields
     * @param type - class type
     * @return expanded list of fields
     */
    static List<Field> getAllFields(List<Field> fields, Class<?> type) {
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
     * @throws JsonMappingException if the mapping process met a structural error
     * @throws JsonElementTypeException if it cannot get element's type
     * @throws JsonCastingError if can't map element onto target
     * @throws JsonMapperFieldRequiredError if a required field is not present in structure
     */
    @SuppressWarnings("unchecked")
    public static <T> T map(JsonElement element, Class<T> target)
            throws JsonElementTypeException, JsonCastingError,
            JsonMapperFieldRequiredError, JsonValueError, RecursiveJsonElementException, JsonMappingException {
        return map(element, target, new HashSet<>());
    }

    private static <T> T map (JsonElement element, Class<T> target, Set<Integer> stack)
            throws JsonElementTypeException, JsonCastingError, JsonMapperFieldRequiredError,
            JsonValueError, RecursiveJsonElementException, JsonMappingException {
        if (!stack.add(element.hashCode()))
            throw new RecursiveJsonElementException("Unsupported recursive Json structure");

        if (target == Integer.class || target == int.class) {
            if (!element.isNumber())
                throw new JsonCastingError(element, target);
            return target.cast(Integer.valueOf(element.getAsInt()));
        } else if (target == Long.class || target == long.class) {
            if (!element.isNumber())
                throw new JsonCastingError(element, target);
            return target.cast(Long.valueOf(element.getAsLong()));
        } else if (target == Float.class || target == float.class) {
            if (!element.isNumber())
                throw new JsonCastingError(element, target);
            return target.cast(Float.valueOf(element.getAsFloat()));
        } else if (target == Double.class || target == double.class) {
            if (!element.isNumber())
                throw new JsonCastingError(element, target);
            return target.cast(Double.valueOf(element.getAsDouble()));

        } else if (target == Boolean.class || target == boolean.class) {
            if (!element.isBoolean())
                throw new JsonCastingError(element, target);
            return target.cast(element.getAsBoolean());

        } else if (target == String.class) {
            if (!element.isString())
                throw new JsonCastingError(element, target);
            return target.cast(element.getAsString());

        } else if (target.isEnum()) {
            if (!element.isString())
                throw new JsonCastingError( element, target);
            for (Object inst : target.getEnumConstants()) {
                String eName = ((Enum<?>)inst).name();
                if (eName.equalsIgnoreCase(element.getAsString())) {
                    return (T)(Enum.valueOf((Class<? extends Enum>) target, eName));
                }
            }
            throw new JsonValueError((JsonString) element, target);

        } else if (target.isArray()) {
            if (!element.isJsonArray())
                throw new JsonCastingError(element, target);
            Class<?> subType = target.getComponentType();
            ArrayList<Object> arr = new ArrayList<>();
            for (JsonElement elem : element.getAsJsonArray()) {
                arr.add(map(elem, subType,stack));
            }
            if (isPrimitive(subType)) {
                return arrayListToArray(arr, target);
            }
            return target.cast(arr.toArray());

        } else if (Collection.class.isAssignableFrom(target) || Map.class.isAssignableFrom(target)) {
            // not supposed to be passed as target!
            throw new JsonCastingError(element, target);
        }

        if (!element.isJsonObject() && !(element.isString() && element.typeToString().equals("null"))) {
            throw new JsonCastingError(element, target);
        }


        try {
            T instance = target.getDeclaredConstructor().newInstance();
            for (Field field : getAllFields(new LinkedList<>(), target)) {
                if (field.isAnnotationPresent(JsonIgnore.class)) continue;

                field.setAccessible(true);
                boolean required = field.isAnnotationPresent(JsonRequired.class);
                boolean ignoreException = field.isAnnotationPresent(JsonIgnoreExceptions.class);
                String name = field.getName();
                if (field.isAnnotationPresent(JsonNode.class)) {
                    JsonNode elem = field.getAnnotation(JsonNode.class);
                    required |= elem.required();
                    ignoreException |= elem.ignoreExceptions();
                    name = elem.value();
                }

                if (element.isString() && element.typeToString().equals("null") & !required)
                    return null;

                try {
                    if (!element.getAsJsonObject().contains(name)) {
                        if (required) {
                            if (field.isAnnotationPresent(JsonEnumDefault.class)) {
                                for (Field f : target.getFields()) {
                                    if (f.getName().equalsIgnoreCase(field.getAnnotation(JsonEnumDefault.class).value())) {
                                        field.set(instance, Enum.valueOf((Class) target, f.getName()));
                                        break;
                                    }
                                }
                                if (field.get(instance) == null)
                                    throw new JsonValueError((JsonString) element, target);
                            } else
                                throw new JsonMapperFieldRequiredError(name);
                        }
                        continue;
                    }

                    if (Collection.class.isAssignableFrom(field.getType())) {
                        if (!element.getAsJsonObject().get(name).isJsonArray())
                            throw new JsonCastingError(element.getAsJsonObject().get(name), target);

                        Type t = field.getGenericType();

                        Class<?> type = (Class<?>) ((ParameterizedType) t).getActualTypeArguments()[0];
                        List<Object> arr = new ArrayList<>();
                        for (JsonElement elem : element.getAsJsonObject().get(name).getAsJsonArray()) {
                            arr.add(map(elem, type, stack));
                        }
                        field.set(instance, arr);
                    } else if (Map.class.isAssignableFrom(field.getType())) {
                        if (!element.getAsJsonObject().get(name).isJsonObject())
                            throw new JsonCastingError(element.getAsJsonObject().get(name), target);

                        Type t = field.getGenericType();

                        Class<?> keyType = (Class<?>) ((ParameterizedType) t).getActualTypeArguments()[0];
                        Class<?> valueType = (Class<?>) ((ParameterizedType) t).getActualTypeArguments()[1];

                        if (keyType != String.class)
                            throw new JsonCastingError(element, keyType);

                        Map<String, Object> map = new HashMap<>();

                        for (JsonObject.JsonNode node : element.getAsJsonObject().get(name).getAsJsonObject().entrySet()) {
                            map.put(
                                    node.getKey(),
                                    map(node.getValue(), valueType, stack)
                            );
                        }
                        field.set(instance, map);
                    } else {
                        field.set(
                                instance,
                                map(element.getAsJsonObject().get(name), field.getType(), stack)
                        );
                    }
                } catch (JsonCastingError e) {
                    if (!ignoreException) throw e;
                }
            }
            return instance;
        } catch (IllegalAccessException | InstantiationException | InvocationTargetException | ChildNotFoundException | NoSuchMethodException e) {
            throw new JsonError.JsonMappingException(e);
        }
    }
}
