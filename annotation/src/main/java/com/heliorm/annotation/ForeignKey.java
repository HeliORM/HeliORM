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
     * Provide the SQL field name for this field. If left as undefined (blank) the meta
     * data will define the SQL field to have the same name as the Java field.
     *
     * @return The field name
     */
    String fieldName() default "";

    /**
     * Determine if the column on the database can be null.
     *
     * @return True if null is allowed
     */
    boolean nullable() default false;

    /**
     * The POJO class to which this field links.
     *
     * @return The foreign POJO class
     */
    Class<?> pojo();

}
