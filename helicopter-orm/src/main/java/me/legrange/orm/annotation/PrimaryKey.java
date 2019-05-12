package me.legrange.orm.annotation;

/**
 *
 * @author gideon
 */
public @interface PrimaryKey {

    boolean autoIncrement() default true;

}
