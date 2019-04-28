package me.legrange.orm.rep;

/**
 *
 * @author gideon
 */
abstract class Criteria {

    public enum Type {
        FIELD, AND, OR;
    }

    private final Type type;

    public Criteria(Type type) {
        this.type = type;
    }

}
