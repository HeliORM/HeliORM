package com.heliorm.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation used to indicate the class is a POJO and should be processed by
 * the processor
 *
 * @author gideon
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Pojo {

    /**
     * Specify the database table name if you want to override the one selected
     * by the processor.
     *
     * @return The table name.
     */
    String tableName() default "";

}
