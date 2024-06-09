package io.github.nerjalnosk.jsonlight.mapper;

import io.github.nerjalnosk.jsonlight.elements.JsonArray;
import io.github.nerjalnosk.jsonlight.elements.JsonElement;
import io.github.nerjalnosk.jsonlight.elements.JsonObject;
import io.github.nerjalnosk.jsonlight.mapper.annotations.*;
import io.github.nerjalnosk.jsonlight.mapper.errors.*;

import java.lang.reflect.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

import static io.github.nerjalnosk.jsonlight.JsonError.*;

/**
 * Class for mapping a {@link JsonElement} onto any class
 * @author CodedSakura
 */
public class JsonMapper {
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
    public static <T> T map(JsonElement element, Class<T> target)
            throws JsonElementTypeException, JsonCastingError,
            JsonMapperError, JsonMappingException, CreationException {
        return map(element, target, new HashMap<>());
    }

    static <T> T map(JsonElement element, Class<T> target, Map<Integer, ?> map)
            throws JsonCastingError, JsonElementTypeException, JsonMapperError, JsonMappingException, CreationException {
        Integer hash = System.identityHashCode(element);
        if (map.containsKey(hash)) return target.cast(map.get(hash));

        // == primitives ==
        // numbers
        boolean num = element.isNumber();
        if (target == Byte.class || target == byte.class) {
            if (!num) throw new JsonCastingError(element, target);
            return target.cast(element.getAsByte());
        } else if (target == Short.class || target == short.class) {
            if (!num) throw new JsonCastingError(element, target);
            return target.cast(element.getAsShort());
        } else if (target == Integer.class || target == int.class) {
            if (!num) throw new JsonCastingError(element, target);
            return target.cast(element.getAsInt());
        } else if (target == Long.class || target == long.class) {
            if (!num) throw new JsonCastingError(element, target);
            return target.cast(element.getAsLong());
        } else if (target == Float.class || target == float.class) {
            if (!num) throw new JsonCastingError(element, target);
            return target.cast(element.getAsFloat());
        } else if (target == Double.class || target == double.class) {
            if (!num) throw new JsonCastingError(element, target);
            return target.cast(element.getAsDouble());
        } else if (target == BigInteger.class) {
            if (!num) throw new JsonCastingError(element, target);
            return target.cast(element.getAsBigInt());
        } else if (target == BigDecimal.class) {
            if (!num) throw new JsonCastingError(element, target);
            return target.cast(element.getAsBigDecimal());
        }
        // bool
        else if (target == Boolean.class || target == boolean.class) {
            if (!element.isBoolean()) throw new JsonCastingError(element, target);
            return target.cast(element.getAsBoolean());
        }
        // string
        else if (target == String.class) {
            if (!element.isString()) throw new JsonCastingError(element, target);
            return target.cast(element.getAsString());
        }
        // == non-primitives ==
        // enum
        else if (target.isEnum()) {
            return resolveEnum(target, element);
        }
        // list
        else if (target.isAssignableFrom(List.class)) {
            if (!element.isJsonArray()) throw new JsonCastingError(element, target);
            return target.cast(CreationEngine.createList(target, (JsonArray) element));
        }
        // map
        else if (target.isAssignableFrom(Map.class)) {
            if (!element.isJsonObject()) throw new JsonCastingError(element, target);
            return target.cast(CreationEngine.createMap(target, (JsonObject) element));
        }
        return CreationEngine.createInstance(target, element, map);
    }

    @SuppressWarnings("unchecked")
    private static <T> T resolveEnum(Class<T> enumClass, JsonElement element)
            throws JsonElementTypeException, JsonMapperError {
        for (Method m : enumClass.getMethods()) {
            if (m.isAnnotationPresent(JsonEnumProvider.class) &&
                    m.getReturnType().equals(enumClass) &&
                    m.getParameters().length == 1 &&
                    m.getParameters()[0].getType().equals(JsonElement.class) &&
                    Modifier.isStatic(m.getModifiers())
            ) {
                try {
                    return (T) m.invoke(null, element);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    throw new JsonMapEnumError(e, element, enumClass);
                }
            }
        }
        if (element.isString()) {
            for (T t : enumClass.getEnumConstants()) {
                if (((Enum<?>)t).name().equalsIgnoreCase(element.getAsString())) {
                    return t;
                }
            }
            throw new JsonMapEnumError(element.getAsJsonString(), enumClass);
        } else if (element.isNumber()) {
            try {
                return enumClass.getEnumConstants()[element.getAsInt()];
            } catch (IndexOutOfBoundsException e) {
                throw new JsonMapEnumError(e, element, enumClass);
            }
        }
        throw new JsonMapEnumError("Unable to process enum from element "+element+" to "+enumClass, element, enumClass);
    }
}
