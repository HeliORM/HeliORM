package me.legrange.orm.impl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import me.legrange.orm.Continuation;
import me.legrange.orm.DateClause;
import me.legrange.orm.DateField;
import me.legrange.orm.EnumClause;
import me.legrange.orm.EnumField;
import me.legrange.orm.Field;
import me.legrange.orm.Join;
import me.legrange.orm.NumberClause;
import me.legrange.orm.NumberField;
import me.legrange.orm.Ordered;
import me.legrange.orm.OrmException;
import me.legrange.orm.StringClause;
import me.legrange.orm.StringField;
import me.legrange.orm.Table;

abstract class ContinuationPart<LT extends Table<LO>, LO, RT extends Table<RO>, RO> extends Part implements Continuation<LT, LO, RT, RO> {

    ContinuationPart(Part left) {
        super(left);
    }

    @Override
    public <F extends NumberField<LT, LO, C>, C extends Number> NumberClause<LT, LO, C, RT, RO> and(F field) {
        return new NumberClausePart(this, ClausePart.Operator.AND, field);
    }

    @Override
    public <F extends NumberField<LT, LO, C>, C extends Number> NumberClause<LT, LO, C, RT, RO> or(F field) {
        return new NumberClausePart(this, ClausePart.Operator.OR, field);
    }

    @Override
    public <F extends StringField<LT, LO>> StringClause<LT, LO, RT, RO> and(F field) {
        return new StringClausePart(this, ClausePart.Operator.AND, field);
    }

    @Override
    public <F extends StringField<LT, LO>> StringClause<LT, LO, RT, RO> or(F field) {
        return new StringClausePart(this, ClausePart.Operator.OR, field);
    }

    @Override
    public <F extends EnumField<LT, LO, C>, C extends Enum> EnumClause<LT, LO, C, RT, RO> and(F field) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public <F extends EnumField<LT, LO, C>, C extends Enum> EnumClause<LT, LO, C, RT, RO> or(F field) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public <F extends DateField<LT, LO>> DateClause<LT, LO, RT, RO> and(F field) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public <F extends DateField<LT, LO>> DateClause<LT, LO, RT, RO> or(F field) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public <RT extends Table<RO>, RO> Join<LT, LO, RT, RO> join(RT table) {
        return new JoinPart(this, table);
    }

    @Override
    public List<LO> list() throws OrmException {
        return getOrm().list(this);
    }

    @Override
    public Stream<LO> stream() throws OrmException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public LO one() throws OrmException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Optional<LO> oneOrNone() throws OrmException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public <F extends Field<LT, LO, C>, C> Ordered<LT, LO> orderBy(F field) {
        return new OrderedPart(this, OrderedPart.Direction.ASCENDING, field);
    }

    @Override
    public <F extends Field<LT, LO, C>, C> Ordered<LT, LO> orderByDesc(F field) {
        return new OrderedPart(this, OrderedPart.Direction.DESCENDING, field);
    }

    @Override
    public Type getType() {
        return Type.CONTINUATION;
    }

}
