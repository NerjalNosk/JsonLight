package io.github.nerjalnosk.jsonlight.mapper;

import io.github.nerjalnosk.jsonlight.JsonError;
import io.github.nerjalnosk.jsonlight.elements.*;
import io.github.nerjalnosk.jsonlight.mapper.annotations.*;
import io.github.nerjalnosk.jsonlight.mapper.errors.*;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.stream.Collectors;

final class CreationEngine {
    private CreationEngine() {}

    private static CreationException impossibleBuildError(Class<?> c, Exception e) {
        return new CreationException("Cannot build an instance of "+c,e);
    }

    private static CreationException noBuilderError(Class<?> c, Class<?> c2, String s, Exception e) {
        return noBuilderError(c, c2, s, e, false);
    }

    private static CreationException noBuilderError(Class<?> c, Class<?> c2, String s, Exception e, boolean field) {
        return new CreationException("Couldn't find a matching builder " + s + " for " + (field ? "field " : "") + c + " in " + c2, e);
    }

    @SuppressWarnings("unchecked")
    static <T> T createList(Class<T> listClass, JsonArray values) throws CreationException {
        List<?> list = null;
        if (listClass.equals(List.class)) {
            list = new ArrayList<>();
        } else if (listClass.isInterface()) {
            throw new UnsupportedOperationException("complex interface instantiation not implemented yet");
        } else if (Modifier.isAbstract(listClass.getModifiers())) {
            throw new UnsupportedOperationException("abstract class instantiation not implemented yet");
        } else {
            try {
                list = (List<?>) listClass.getConstructor().newInstance();
            } catch (NoSuchMethodException|InvocationTargetException|InstantiationException|IllegalAccessException e) {
                // ignored
            }
            if (list == null) {
                try {
                    list = (List<?>) listClass.getConstructor(int.class).newInstance(values.size());
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

    @SuppressWarnings("unchecked")
    static <T> T createMap(Class<T> mapClass, JsonObject object) throws CreationException {
        Map<String, ?> map = null;
        if (mapClass.equals(Map.class)) {
            map = new HashMap<>();
        } else if (mapClass.isInterface()) {
            throw new UnsupportedOperationException("complex interface instantiation not implemented yet");
        } else if (Modifier.isAbstract(mapClass.getModifiers())) {
            throw new UnsupportedOperationException(("abstract class instantiation not implemented yet"));
        } else {
            try {
                map = (Map<String, ?>) mapClass.getConstructor().newInstance();
            } catch (NoSuchMethodException|InvocationTargetException|InstantiationException|IllegalAccessException e) {
                // ignored
            }
            if (map == null) {
                try {
                    map = (Map<String, ?>) mapClass.getConstructor(int.class).newInstance(object.entrySet().size());
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

    static <T> T createInstance(Class<T> targetClass, JsonElement element, Map<Integer, ?> map)
            throws CreationException, JsonMapperError, JsonError.JsonElementTypeException, JsonError.JsonMappingException {
        T instance;
        if (targetClass.isAnnotationPresent(JsonInstanceProvider.class)) {
            JsonInstanceProvider provider = targetClass.getAnnotation(JsonInstanceProvider.class);
            instance = computeProvider(targetClass, provider, element, map);
        } else {
            instance = instantiate(targetClass, targetClass, new Class[]{});
            fieldMap(targetClass, instance, element, map);
        }
        return instance;
    }

    private static <T> T computeProvider(Class<T> targetClass, JsonInstanceProvider provider, JsonElement element, Map<Integer, ?> map)
            throws CreationException, JsonError.JsonElementTypeException, JsonError.JsonMappingException, JsonMapperError {
        Class<?> clazz = provider.clazz();
        if (clazz == JsonInstanceProvider.class) {
            clazz = targetClass;
        }
        String name = provider.builder().trim();
        T instance;
        if (provider.autoMapping()) {
            if (name.isEmpty()) {
                instance = instantiate(targetClass, clazz, provider.nullableArgs());
            } else {
                instance = instantiateWith(targetClass, clazz, name, provider.nullableArgs());
            }
            fieldMap(targetClass, instance, element, map);
        } else if (name.isEmpty()) {
            instance = build(targetClass, clazz, element, provider.nullableArgs());
        } else {
            instance = buildWith(targetClass, clazz, element, name, provider.nullableArgs());
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

    private static int fieldPriority(Field f) {
        if (f.isAnnotationPresent(JsonFieldOrder.class)) {
            JsonFieldOrder fieldOrder = f.getAnnotation(JsonFieldOrder.class);
            if (fieldOrder.value() >= 0) return fieldOrder.value();
        }
        if (f.isAnnotationPresent(JsonNode.class)) {
            JsonNode node = f.getAnnotation(JsonNode.class);
            if (node.order() >= 0) return node.order();
        }
        return 0;
    }

    private static int fieldComparator(Field f1, Field f2) {
        int i1 = fieldPriority(f1);
        int i2 = fieldPriority(f2);
        if (i1 == i2) return f1.getName().compareTo(f2.getName());
        return -Integer.compare(i1, i2);
    }

    private static <T> void fieldMap(Class<T> targetClass, T instance, JsonElement element, Map<Integer, ?> map)
            throws JsonMapperError, JsonError.JsonElementTypeException, JsonError.JsonMappingException, CreationException {
        if (!element.isJsonObject()) {
            throw new JsonMapperTypeError("Cannot parse object from " + element.typeToString(), element);
        }
        JsonObject object = element.getAsJsonObject();
        Map<Integer, List<Field>> postInitMap = new HashMap<>();

        List<Field> fields = getAllFields(new LinkedList<>(), targetClass);
        fields.sort(CreationEngine::fieldComparator);

        for (Field field : fields) {
            if (field.isAnnotationPresent(JsonIgnore.class) && field.getAnnotation(JsonIgnore.class).fromJson()) {
                continue;
            }

            boolean a = field.isAccessible();
            field.setAccessible(true);

            boolean req = field.isAnnotationPresent(JsonRequired.class);
            boolean ignoreAll = field.isAnnotationPresent(JsonIgnoreExceptions.class);
            boolean ignoreCast = field.isAnnotationPresent(JsonIgnoreExceptions.IgnoreCastingError.class);
            boolean ignoreNoChild = field.isAnnotationPresent(JsonIgnoreExceptions.IgnoreNoChildException.class);
            boolean ignoreNoAccess = field.isAnnotationPresent(JsonIgnoreExceptions.IgnoreNoAccess.class);
            String name = field.getName();
            if (field.isAnnotationPresent(JsonNode.class)) {
                JsonNode elem = field.getAnnotation(JsonNode.class);
                req |= elem.required();
                ignoreAll |= elem.ignoreExceptions();
                name = elem.value();
            }

            Class<?> type = field.getType();

            try {
                if (!object.contains(name)) {
                    if (req) {
                        // enum default
                        if (type.isEnum() && field.isAnnotationPresent(JsonEnumDefault.class)) {
                            JsonEnumDefault enumDefault = field.getAnnotation(JsonEnumDefault.class);
                            for (Object o : type.getEnumConstants()) {
                                if (((Enum<?>) o).name().equalsIgnoreCase(enumDefault.value())) {
                                    field.set(instance, o);
                                    break;
                                }
                            }
                            if (field.get(instance) == null) {
                                throw new JsonValueError(name, type);
                            }
                            continue;
                        }
                        // default provider
                        if (field.isAnnotationPresent(JsonDefaultProvider.class)) {
                            JsonDefaultProvider defaultProvider = field.getAnnotation(JsonDefaultProvider.class);
                            if (defaultProvider.postInit()) {
                                postInitMap.computeIfAbsent(defaultProvider.priority(), i -> new ArrayList<>()).add(field);
                            } else {
                                resolveDefaultProvider(field, defaultProvider, instance, element);
                            }
                            continue;
                        }
                        throw new JsonMapperFieldRequiredError(name, object);
                    }
                    continue;
                }
                JsonElement sub = object.get(name);
                // JsonInstanceProvider
                if (field.isAnnotationPresent(JsonInstanceProvider.class)) {
                    JsonInstanceProvider instanceProvider = field.getAnnotation(JsonInstanceProvider.class);
                    field.set(instance, computeProvider(type, instanceProvider, sub, map));
                    continue;
                }
                // default parse
                field.set(instance, JsonMapper.map(sub, type, map));
            } catch (IllegalAccessException e) {
                if (!(ignoreAll || ignoreNoAccess)) throw new JsonError.JsonMappingException(e);
            } catch (JsonError.ChildNotFoundException e) {
                if (!(ignoreAll || ignoreNoChild)) throw new JsonError.JsonMappingException(e);
            } catch (JsonCastingError e) {
                if (!(ignoreAll || ignoreCast)) throw new JsonError.JsonMappingException(e);
            } catch (IllegalArgumentException e) {
                throw new JsonMapperTypeError(e, element);
            } finally {
                field.setAccessible(a);
            }
        }
        // postInit
        for (int i : postInitMap.keySet().stream().sorted().collect(Collectors.toList())) {
            List<Field> l = postInitMap.get(i);
            for (Field field : l) {
                if (!field.isAnnotationPresent(JsonDefaultProvider.class)) continue;
                resolveDefaultProvider(field, field.getAnnotation(JsonDefaultProvider.class), instance, element);
            }
        }
    }

    private static <T> void resolveDefaultProvider(Field f, JsonDefaultProvider defaultProvider, T instance, JsonElement element) throws CreationException {
        try {
            f.set(instance, resolveDefaultProvider(DefaultProvider.ofAnnotation(defaultProvider), instance, element));
        } catch (IllegalAccessException e) {
            throw impossibleBuildError(instance.getClass(), e);
        }
    }

    /**
     * Tries to resolve the provider specification of a {@link DefaultProvider}
     * @param provider The provider to resolve
     * @param instance The class instance for which to resolve the provider.
     *                 Relevant for non-static method resolving.
     * @param element The JsonElement to be used with the provider.
     * @return The resolved provider result.
     * @param <T> The result type of the provider. Supposedly a field value type.
     * @param <U> The type of the instance.
     * @throws CreationException If the provider couldn't be resolved.
     */
    public static <T, U> T resolveDefaultProvider(DefaultProvider provider, U instance, JsonElement element) throws CreationException {
        Class<?> clazz = provider.clazz;
        if (clazz == DefaultProvider.class) {
            clazz = instance.getClass();
        }
        String name = provider.value.trim();
        Method m = null;
        boolean b = true;
        try {
            m = clazz.getDeclaredMethod(name, element.getClass());
            b = m.isAccessible();
            m.setAccessible(true);
            boolean useInstance = clazz == instance.getClass() && Modifier.isStatic(m.getModifiers());
            return (T) (m.invoke(useInstance ? instance : null, element));
        } catch (NoSuchMethodException e) {
            throw noBuilderError(instance.getClass(), clazz, name, e, true);
        } catch (InvocationTargetException | IllegalAccessException e) {
            throw impossibleBuildError(instance.getClass(), e);
        } catch (ClassCastException e) {
            throw new CreationException("Impossible cast", e);
        } finally {
            if (m != null) m.setAccessible(b);
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

    /**
     * Specification of a default entity provider entry, to be used
     * with {@link #resolveDefaultProvider(DefaultProvider, Object, JsonElement)}
     */
    public final static class DefaultProvider {
        public final int priority;
        public final String value;
        public final Class<?> clazz;

        private DefaultProvider(int i, String s, Class<?> c) {
            this.priority = i;
            this.value = s;
            this.clazz = c;
        }

        public static DefaultProvider ofMethod(Method m) {
            return ofMethod(m, 1);
        }

        public static DefaultProvider ofMethod(Method m, int i) {
            return new DefaultProvider(i, m.getName(), m.getDeclaringClass());
        }

        public static DefaultProvider ofValue(String s) {
            return ofValue(s, 1);
        }

        public static DefaultProvider ofValue(String s, int i) {
            return new DefaultProvider(i, s, DefaultProvider.class);
        }

        public static DefaultProvider ofAnnotation(JsonDefaultProvider annotation) {
            return new DefaultProvider(annotation.priority(), annotation.value(), annotation.clazz());
        }
    }
}
