package net.legrange.orm.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation marks a POJO field for a custom data type that we don't
 * support by default.
 *
 * This influences the way the meta data is generated, as well as how the data
 * is serialized and de-serialized
 *
 * @author gideon
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Custom {

    Class<?> typeAdapter();

}
