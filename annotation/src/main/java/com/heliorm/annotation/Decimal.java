package com.heliorm.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation marks a decimal POJO field that we wish adjust default behavior on. This annotation applies to
 * Float, float, Double, double and BigDecimal types.
 * <p>
 * This influences the way the metadata is generated.
 *
 * @author gideon
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Decimal {

    /**
     * Provide the SQL field name for this field. If left as undefined (blank) the meta
     * data will define the SQL field to have the same name as the Java field.
     *
     * @return The field name
     */
    String fieldName() default "";

    /**
     * Provide precision for a decimal.
     *
     * @return The precision
     */
    int precision() default 18;

    /**
     * Provide scale for a decimal.
     *
     * @return The scale
     */
    int scale() default 2;

    /**
     * Determine if the column on the database can be null.
     *
     * @return True if null is allowed
     */
    boolean nullable() default false;

    /**
     * Provide the default value
     *
     * @return The default value
     */
    double defaultValue() default 0.0;

}
