package com.heliorm.mojo.annotated;

import com.heliorm.annotation.Decimal;
import com.heliorm.annotation.ForeignKey;
import com.heliorm.annotation.Ignore;
import com.heliorm.annotation.PrimaryKey;
import com.heliorm.annotation.Text;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.lang.String.format;

/**
 * A helper class to extract POJO features from annotations.
 */
final class AnnotationHelper {

    private static final String FIELD_NAME = "fieldName";
    private static final String LENGTH = "length";
    private static final String NULLABLE = "nullable";

    private static final List<Class<? extends Annotation>> fieldAnnotations =
            Arrays.asList(PrimaryKey.class, ForeignKey.class, Ignore.class, Text.class, Decimal.class);

    static <A extends Annotation> Optional<A> getAnnotation(Class type, Class<A> aType) {
        return (Optional<A>) Optional.ofNullable(type.getAnnotation(aType));
    }

    static Optional<Annotation> getAnnotation(Field field) {
        Annotation res = null;
        for (Annotation an : field.getAnnotations()) {
            if (fieldAnnotations.contains(an.annotationType())) {
                if (res != null) {
                    throw new AnnotatedPojoException(format("Multiple POJO annotations on field '%s' on class '%s'. Only one must be used",
                            field.getName(), field.getDeclaringClass().getCanonicalName()));
                }
                res = an;
            }
        }
        return Optional.ofNullable(res);
    }

    static <A extends Annotation> List<A> getAnnotations(Class<?> target, Class<A> aType) {
        List<A> annotations = new ArrayList<>();
        annotations.addAll(Arrays.stream(target.getAnnotationsByType(aType)).collect(Collectors.toList()));
        if (!Object.class.equals(target)) {
            target = target.getSuperclass();
            annotations.addAll(getAnnotations(target, aType));
        }
        return annotations;
    }

    static <A extends Annotation> Optional<A> getAnnotation(Field field, Class<A> type) {
        return Optional.ofNullable(field.getAnnotation(type));
    }

    static boolean isPrimaryKey(Field field) {
        return getAnnotation(field, PrimaryKey.class).isPresent();
    }

    static boolean isForeignKey(Field field) {
        return getAnnotation(field, ForeignKey.class).isPresent();
    }

    static boolean isAutoNumber(Field field) {
        Optional<PrimaryKey> pk = getAnnotation(field, PrimaryKey.class);
        if (pk.isPresent()) {
            return pk.get().autoIncrement();
        }
        return false;
    }

    static boolean isNullable(Field field) {
        Optional<Annotation> an = getAnnotation(field);
        if (an.isPresent()) {
            if (hasMethod(an.get(), NULLABLE)) {
                return (boolean) runMethod(an.get(), NULLABLE);
            }
        }
        return false;
    }

    static Optional<Integer> getLength(Field field) {
        Optional<Annotation> an = getAnnotation(field);
        if (an.isPresent()) {
            if (hasMethod(an.get(), LENGTH)) {
                return Optional.of((Integer) runMethod(an.get(), LENGTH));
            }
        }
        return Optional.empty();
    }

    static Optional<String> getFieldName(Field field) {
        Optional<Annotation> an = getAnnotation(field);
        if (an.isPresent()) {
            return Optional.of((String) runMethod(an.get(), FIELD_NAME));
        }
        return Optional.empty();
    }

    private static boolean hasMethod(Annotation an, String name) {
        return getMethod(an, name).isPresent();
    }

    private static Object runMethod(Annotation an, String name) {
        try {
            Optional<Method> opt = getMethod(an, name);
            if (opt.isPresent()) {
                return opt.get().invoke(an);
            }
            throw new AnnotatedPojoException(format("No method %s on annotation %s", an.annotationType().getSimpleName()));
        } catch (InvocationTargetException | IllegalAccessException e) {
            throw new AnnotatedPojoException(format("Error calling %s on annotation %s (%s)", name, an.annotationType().getSimpleName(), e.getMessage()), e);
        }
    }

    private static Optional<Method> getMethod(Annotation an, String name) {
        try {
            return Optional.of(an.annotationType().getMethod(name, new Class[]{}));
        } catch (NoSuchMethodException e) {
            return Optional.empty();
        }
    }

    private AnnotationHelper() {
    }
}
