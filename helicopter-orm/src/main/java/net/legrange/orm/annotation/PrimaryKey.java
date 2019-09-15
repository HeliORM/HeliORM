package net.legrange.orm.annotation;

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
     * True for auto-increment keys, false if you want to do your own key
     * management. Default is true.
     *
     * @return true for auto-increment keys
     */
    boolean autoIncrement() default true;

}
