package me.legrange.orm.annotation;

/**
 * Annotation used to indicate the class is a Pojo.
 *
 * @author gideon
 */
public @interface Pojo {

    String tableName() default "";

}
