package me.legrange.orm.rep;

/**
 *
 * @author gideon
 */
public class AndCriteria extends Criteria {

    private final Criteria left;
    private final Criteria right;

    public AndCriteria(Criteria left, Criteria right) {
        super(Type.AND);
        this.left = left;
        this.right = right;
    }

}
