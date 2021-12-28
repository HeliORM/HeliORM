package com.heliorm.annotation;

import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Repeatable(Indexes.class)
@Retention(RetentionPolicy.RUNTIME)
public @interface Index {


    /** Indicates if this index requires unique keys.
     *
     * @return The uniqueness of the index
     */
    boolean unique() default false;

    /** Which columns to include in the index.
     *
     * @return The index columns
     */
    String[] columns();



}
