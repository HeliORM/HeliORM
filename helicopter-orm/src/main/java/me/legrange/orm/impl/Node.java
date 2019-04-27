package me.legrange.orm.impl;

import me.legrange.orm.OrmException;

/**
 *
 * @author gideon
 */
public class Node {

    private final Part part;
    private final Node next;
    private final Node tangent;

    public Node(Part part, Node next, Node tangent) {
        this.part = part;
        this.next = next;
        this.tangent = tangent;
    }

    public boolean hasNext() {
        return next != null;
    }

    public boolean hasTangent() {
        return tangent != null;
    }

    public Part getPart() {
        return part;
    }

    public Node getNext() {
        return next;
    }

    public Node getTangent() {
        return tangent;
    }

    public static Node unroll(Part part) throws OrmException {
        return unroll0(part.head());
    }

    private static Node unroll0(Part part) throws OrmException {
        Node next = null;
        Node tangent = null;
        if (part.right() != null) {
            next = unroll0(part.right());
        }
        if (part instanceof ExpressionContinuationPart) {
            Part expr = ((ExpressionContinuationPart) part).getExpression();
            tangent = unroll(expr);
        } else if (part instanceof ContinuationPart) {
            Part expr = ((ContinuationPart) part).getExpression();
            tangent = unroll(expr);
        }
        return new Node(part, next, tangent);
    }

    public void dump(int depth) {
        for (int i = 0; i < depth; ++i) {
            System.out.print("\t");
        }
        System.out.println(part);
        if (hasTangent()) {
            getTangent().dump(depth + 1);
        }
        if (hasNext()) {
            getNext().dump(depth);
        }
    }

}
