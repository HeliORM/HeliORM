package net.legrange.orm.rep;

/**
 *
 * @author gideon
 */
public class OrCriteria extends Criteria {

    private final Criteria left;
    private final Criteria right;

    public OrCriteria(Criteria left, Criteria right) {
        super(Type.OR);
        this.left = left;
        this.right = right;
    }

    public Criteria getLeft() {
        return left;
    }

    public Criteria getRight() {
        return right;
    }
}
