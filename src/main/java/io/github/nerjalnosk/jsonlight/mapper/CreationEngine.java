package io.github.nerjalnosk.jsonlight.mapper;

import io.github.nerjalnosk.jsonlight.elements.*;
import io.github.nerjalnosk.jsonlight.mapper.annotations.JsonInstanceProvider;
import io.github.nerjalnosk.jsonlight.mapper.errors.JsonMapperError;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;

final class CreationEngine {
    private CreationEngine() {}

    private static CreationException impossibleBuildError(Class<?> c, Exception e) {
        return new CreationException("Cannot build an instance of "+c,e);
    }

    private static CreationException noBuilderError(Class<?> c, Class<?> c2, String s, Exception e) {
        return new CreationException("Couldn't find a matching builder " + s + " for " + c + " in " + c2, e);
    }

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

    public static <T> T createInstance(Class<T> targetClass, JsonElement element) throws CreationException, JsonMapperError {
        T instance;
        if (targetClass.isAnnotationPresent(JsonInstanceProvider.class)) {
            JsonInstanceProvider provider = targetClass.getAnnotation(JsonInstanceProvider.class);
            Class<?> clazz = provider.clazz();
            if (clazz == null) {
                clazz = targetClass;
            }
            String name = provider.builder().trim();
            if (provider.autoMapping()) {
                if (!name.isEmpty()) {
                    instance = instantiateWith(targetClass, clazz, name, provider.nullableArgs());
                } else {
                    instance = instantiate(targetClass, clazz, provider.nullableArgs());
                }
                // TODO:1 auto map out from Json
            } else if (name.isEmpty()) {
                instance = build(targetClass, clazz, element, provider.nullableArgs());
            } else {
                instance = buildWith(targetClass, clazz, element, name, provider.nullableArgs());
            }
        } else {
            instance = instantiate(targetClass, targetClass, new Class[]{});
            // TODO:1 auto map out from JSON
        }
        return instance;
    }

    private static <T> T instantiate(Class<T> targetClass, Class<?> builderClass, Class<?>[] nArgs) throws CreationException {
        if (builderClass != targetClass && !targetClass.isAssignableFrom(builderClass)) {
            throw new CreationException("Cannot instantiate " + targetClass + " from a " + builderClass + " constructor");
        }
        try {
            Object[] args = Arrays.copyOf(new Object[]{}, nArgs.length);
            return targetClass.cast(builderClass.getDeclaredConstructor(nArgs).newInstance(args));
        } catch (NoSuchMethodException|InvocationTargetException|InstantiationException|IllegalAccessException e) {
            throw impossibleBuildError(targetClass, e);
        }
    }

    private static <T> T instantiateWith(Class<T> targetClass, Class<?> builderClass, String builderName, Class<?>[] nArgs) throws CreationException {
        //
        if (builderClass != targetClass && !targetClass.isAssignableFrom(builderClass)) {
            throw new CreationException("Cannot instantiate " + targetClass + " from a " + builderClass + " constructor");
        }
        try {
            Method m = builderClass.getDeclaredMethod(builderName, nArgs);
            if ((!m.getReturnType().isAssignableFrom(targetClass)) || !Modifier.isStatic(m.getModifiers())) {
                throw noBuilderError(targetClass, builderClass, builderName, null);
            }
            Object[] args = Arrays.copyOf(new Object[]{}, nArgs.length);
            return targetClass.cast(m.invoke(null, args));
        } catch (NoSuchMethodException e) {
            throw noBuilderError(targetClass, builderClass, builderName, e);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw impossibleBuildError(targetClass, e);
        }
    }

    private static <T> T build(Class<T> targetClass, Class<?> builderClass, JsonElement element, Class<?>[] nArgs) throws CreationException {
        if (builderClass != targetClass && !targetClass.isAssignableFrom(builderClass)) {
            throw new CreationException("Cannot instantiate " + targetClass + " from a " + builderClass + " constructor");
        }
        try {
            Object[] args = Arrays.copyOf(new Object[]{element}, nArgs.length+1);
            Class<?>[] cArgs = Arrays.copyOf(new Class<?>[]{element.getClass()}, nArgs.length+1);
            System.arraycopy(nArgs, 0, cArgs, 1, nArgs.length);
            return targetClass.cast(builderClass.getDeclaredConstructor(cArgs).newInstance(args));
        } catch (NoSuchMethodException ignored) {
            // ignored in case element parameter is located at the end
        } catch (InvocationTargetException|InstantiationException|IllegalAccessException e) {
            throw impossibleBuildError(targetClass, e);
        }
        try {
            Object[] args = Arrays.copyOf(new Object[]{}, nArgs.length+1);
            args[nArgs.length] = element;
            Class<?>[] cArgs = Arrays.copyOf(nArgs, nArgs.length+1);
            cArgs[nArgs.length] = element.getClass();
            return targetClass.cast(builderClass.getDeclaredConstructor(cArgs).newInstance(args));
        } catch (NoSuchMethodException|InvocationTargetException|InstantiationException|IllegalAccessException e) {
            throw impossibleBuildError(targetClass, e);
        }
    }

    private static <T> T buildWith(Class<T> targetClass, Class<?> builderClass, JsonElement element,
                                   String builderName, Class<?>[] nArgs) throws CreationException {
        Method method = null;
        Object[] args = null;
        try {
            args = Arrays.copyOf(new Object[]{element}, nArgs.length+1);
            Class<?>[] cArgs = Arrays.copyOf(new Class<?>[]{element.getClass()}, nArgs.length+1);
            System.arraycopy(nArgs, 0, cArgs, 1, nArgs.length);
            Method m = builderClass.getDeclaredMethod(builderName, cArgs);
            if (m.getReturnType().isAssignableFrom(targetClass) && Modifier.isStatic(m.getModifiers())) method = m;
        } catch (NoSuchMethodException e) {
            // ignored in case element parameter is located at the end
        }
        if (method == null) {
            try {
                args = Arrays.copyOf(new Object[]{}, nArgs.length + 1);
                args[nArgs.length] = element;
                Class<?>[] cArgs = Arrays.copyOf(nArgs, nArgs.length + 1);
                cArgs[nArgs.length] = element.getClass();
                Method m = builderClass.getDeclaredMethod(builderName, cArgs);
                if (m.getReturnType().isAssignableFrom(targetClass) && Modifier.isStatic(m.getModifiers())) method = m;
            } catch (NoSuchMethodException e) {
                throw noBuilderError(targetClass, builderClass, builderName, e);
            }
        }
        if (method == null) {
            throw noBuilderError(targetClass, builderClass, builderName, null);
        }
        try {
            return targetClass.cast(method.invoke(null, args));
        } catch (InvocationTargetException | IllegalAccessException e) {
            throw impossibleBuildError(targetClass, e);
        }
    }

    private static <T> void fieldMap(Class<T> targetClass, T instance, JsonElement element)
            throws JsonMapperError, JsonError.JsonElementTypeException, JsonError.JsonMappingException {
        if (!element.isJsonObject()) {
            throw new JsonMapperTypeError("Cannot parse object from " + element.typeToString(), element);
        }
        JsonObject object = element.getAsJsonObject();
        Set<Field> postInit = new HashSet<>();

        for (Field field : getAllFields(new LinkedList<>(), targetClass)) {
            if (field.isAnnotationPresent(JsonIgnore.class) && field.getAnnotation(JsonIgnore.class).fromJson()) {
                continue;
            }

            boolean a = field.isAccessible();
            field.setAccessible(true);

            boolean req = field.isAnnotationPresent(JsonRequired.class);
            boolean ign = field.isAnnotationPresent(JsonIgnoreExceptions.class);
            String name = field.getName();
            if (field.isAnnotationPresent(JsonNode.class)) {
                JsonNode elem = field.getAnnotation(JsonNode.class);
                req |= elem.required();
                ign |= elem.ignoreExceptions();
                name = elem.value();
            }

            try {
                if (!object.contains(name)) {
                    if (req) {
                        // enum default
                        if (field.getType().isEnum() && field.isAnnotationPresent(JsonEnumDefault.class)) {
                            JsonEnumDefault enumDefault = field.getAnnotation(JsonEnumDefault.class);
                            for (Object o : field.getType().getEnumConstants()) {
                                if (((Enum<?>) o).name().equalsIgnoreCase(enumDefault.value())) {
                                    field.set(instance, o);
                                    break;
                                }
                            }
                            if (field.get(instance) == null) {
                                throw new JsonValueError(name, field.getType());
                            }
                        }
                        // default provider
                        throw new JsonMapperFieldRequiredError(name, object);
                    }
                    continue;
                }
                // JsonInstanceProvider
                // default parse
            } catch (IllegalAccessException e) {
                throw new JsonError.JsonMappingException(e);
            } finally {
                field.setAccessible(a);
            }
        }
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

    public static class CreationException extends Exception {
        public CreationException(String s) {
            super(s);
        }
        public CreationException(String s, Exception e) {
            super(s, e);
        }
    }
}
