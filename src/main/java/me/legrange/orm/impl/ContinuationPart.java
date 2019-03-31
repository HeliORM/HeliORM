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
import me.legrange.orm.Order;
import me.legrange.orm.OrmException;
import me.legrange.orm.StringClause;
import me.legrange.orm.StringField;
import me.legrange.orm.Table;

abstract class ContinuationPart<T extends Table<O>, O, RT extends Table<RO>, RO> extends Part implements Continuation<T, O, RT, RO> {

    ContinuationPart(Part left) {
        super(left);
    }

    @Override
    public <F extends NumberField<T, O, C>, C extends Number> NumberClause<T, O, C, RT, RO> and(F field) {
        return new NumberClausePart(this, ClausePart.Operator.AND, field);
    }

    @Override
    public <F extends NumberField<T, O, C>, C extends Number> NumberClause<T, O, C, RT, RO> or(F field) {
        return new NumberClausePart(this, ClausePart.Operator.OR, field);
    }

    @Override
    public <F extends StringField<T, O>> StringClause<T, O, RT, RO> and(F field) {
        return new StringClausePart(this, ClausePart.Operator.AND, field);
    }

    @Override
    public <F extends StringField<T, O>> StringClause<T, O, RT, RO> or(F field) {
        return new StringClausePart(this, ClausePart.Operator.OR, field);
    }

    @Override
    public <F extends EnumField<T, O, C>, C extends Enum> EnumClause<T, O, C, RT, RO> and(F field) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public <F extends EnumField<T, O, C>, C extends Enum> EnumClause<T, O, C, RT, RO> or(F field) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public <F extends DateField<T, O>> DateClause<T, O, RT, RO> and(F field) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public <F extends DateField<T, O>> DateClause<T, O, RT, RO> or(F field) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public <RT extends Table<RO>, RO> Join<T, O, RT, RO> join(RT table) {
        return new JoinPart(this, table);
    }

    @Override
    public List<RO> list() throws OrmException {
        return getOrm().list(this);
    }

    @Override
    public Stream<RO> stream() throws OrmException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public RO one() throws OrmException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Optional<RO> oneOrNone() throws OrmException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public <F extends Field<RT, RO, C>, C> Order<RT, RO> orderBy(F field) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public <F extends Field<RT, RO, C>, C> Order<RT, RO> orderByDesc(F field) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Type getType() {
        return Type.CONTINUATION;
    }

}
