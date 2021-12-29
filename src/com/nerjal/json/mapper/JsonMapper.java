package com.nerjal.json.mapper;

import com.nerjal.json.JsonError;
import com.nerjal.json.elements.JsonElement;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

public class JsonMapper {
    private static boolean isPrimitive(Class<?> clazz) {
        return clazz == int.class ||
                clazz == long.class ||
                clazz == float.class ||
                clazz == double.class ||
                clazz == boolean.class;
    }

    @SuppressWarnings("unchecked")
    private static <E> E[] arrayListToArray(ArrayList<?> arrayList, Class<E> clazz) {
        System.out.printf("%s\n", clazz);
        E[] out = (E[]) Array.newInstance(clazz, arrayList.size());
        for (int i = 0; i < arrayList.size(); i++) {
            Array.set(out, i, arrayList.get(i));
        }
        return out;
    }

    @SuppressWarnings("unchecked")
    public static <T> T map(JsonElement element, Class<T> target)
            throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException,
                    JsonError.JsonElementTypeException, JsonCastingError {
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
                return (T) arrayListToArray(arr, subType);
            }
//            target;
//            System.out.println(.getClass().isArray());
            return (T) arr.toArray();
//            return (T) arr.toArray((Object[]) Array.newInstance(subType, 0));
        }/* else if (Collection.class.isAssignableFrom(target)) {
            if (!element.isJsonArray())
                throw new JsonCastingError(element, target);

            Class<? extends Collection<?>> collTarget = (Class<? extends Collection<?>>) target;
        } else if (Map.class.isAssignableFrom(target)) {
            if (!element.isJsonObject())
                throw new JsonCastingError(element, target);
        }*/
        return target.getDeclaredConstructor().newInstance();
    }
}
