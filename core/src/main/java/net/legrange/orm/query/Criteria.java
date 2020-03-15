package net.legrange.orm.query;

/**
 *
 * @author gideon
 */
public abstract class Criteria {

    public enum Type {
        LIST_FIELD, VALUE_FIELD, AND, OR;
    }

    private final Type type;

    public Criteria(Type type) {
        this.type = type;
    }

    public Type getType() {
        return type;
    }

    void dump() {
        System.out.printf("%s ", type);
    }
}
