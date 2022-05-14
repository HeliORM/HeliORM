package com.heliorm.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Apply this annotation to a POJO field to indicate that it is the primary key
 * for a POJO/Database mapping.
 *
 * @author gideon
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface PrimaryKey {
    /**
     * Provide the SQL field name for this field. If left as undefined (blank) the meta
     * data will define the SQL field to have the same name as the Java field.
     *
     * @return The field name
     */
    String fieldName() default "";

    /**
     * True for auto-increment keys, false if you want to do your own key
     * management. Default is true.
     *
     * @return true for auto-increment keys
     */
    boolean autoIncrement() default true;

}
