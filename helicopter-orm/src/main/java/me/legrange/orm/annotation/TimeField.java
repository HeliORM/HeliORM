package me.legrange.orm.annotation;

/**
 *
 * @author gideon
 */
public @interface TimeField {

    public enum TimeType {
        DATE, TIME, TIMESTAMP;
    }

    TimeType type() default TimeType.TIMESTAMP;

}
