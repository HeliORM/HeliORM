package net.legrange.orm.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation marks a POJO field that we wish adjust default behavior on.
 * This influences the way the meta data is generated.
 *
 * @author gideon
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Column {

    /**
     * Provide the SQL field name for this column. If left undefined the meta
     * data will define the SQL field to have the same name as the Java field.
     *
     * @return The field name
     */
    String fieldName() default "";

    /**
     * Provide the SQL field length for this column, assuming it is a String.
     *
     * @return The field length
     */
    int length() default 0;

}
