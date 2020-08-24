package com.heliorm.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Use this annotation to indicate that a field is a foreign key linking it to
 * another POJO.
 *
 * @author gideon
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ForeignKey {

    /**
     * The POJO class to which this field links.
     *
     * @return The foreign POJO class
     */
    Class<?> pojo();

}
